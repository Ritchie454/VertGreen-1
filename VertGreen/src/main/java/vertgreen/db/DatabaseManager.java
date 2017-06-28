package vertgreen.db;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import vertgreen.Config;
import vertgreen.VertGreen;
import org.hibernate.jpa.HibernatePersistenceProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import java.util.Properties;

public class DatabaseManager {

    private static final Logger log = LoggerFactory.getLogger(DatabaseManager.class);

    private EntityManagerFactory emf;
    private Session sshTunnel;
    private DatabaseState state = DatabaseState.UNINITIALIZED;

    private static final int SSH_TUNNEL_PORT = 9333;

    private String jdbcUrl;
    private String dialect;
    private int poolSize;

    /**
     * @param jdbcUrl  connection to the database
     * @param dialect  set to null or empty String to have it auto detected by Hibernate, chosen jdbc driver must support that
     * @param poolSize max size of the connection pool
     */
    public DatabaseManager(String jdbcUrl, String dialect, int poolSize) {
        this.jdbcUrl = jdbcUrl;
        this.dialect = dialect;
        this.poolSize = poolSize;
    }

    
    public synchronized void startup() {
        if (state == DatabaseState.READY || state == DatabaseState.INITIALIZING) {
            throw new IllegalStateException("Can't start the database, when it's current state is " + state);
        }

        state = DatabaseState.INITIALIZING;

        try {
            if (Config.CONFIG.isUseSshTunnel()) {
                //don't connect again if it's already connected
                if (sshTunnel == null || !sshTunnel.isConnected()) {
                    connectSSH();
                }
            }

            Properties properties = new Properties();
            properties.put("configLocation", "hibernate.cfg.xml");

            properties.put("hibernate.connection.provider_class", "org.hibernate.hikaricp.internal.HikariCPConnectionProvider");
            properties.put("hibernate.connection.url", jdbcUrl);
            if (dialect != null && !"".equals(dialect)) properties.put("hibernate.dialect", dialect);
            properties.put("hibernate.cache.region.factory_class", "org.hibernate.cache.ehcache.EhCacheRegionFactory");

            properties.put("hibernate.hbm2ddl.auto", "update");

            properties.put("hibernate.hikari.maximumPoolSize", Integer.toString(poolSize));

            properties.put("hibernate.hikari.connectionTimeout", Integer.toString(Config.HIKARI_TIMEOUT_MILLISECONDS));
            properties.put("hibernate.hikari.dataSource.ApplicationName", "VertGreen_" + Config.CONFIG.getDistribution());

            properties.put("hibernate.hikari.validationTimeout", "1000");


            LocalContainerEntityManagerFactoryBean emfb = new LocalContainerEntityManagerFactoryBean();
            emfb.setPackagesToScan("vertgreen.db.entity");
            emfb.setJpaVendorAdapter(new HibernateJpaVendorAdapter());
            emfb.setJpaProperties(properties);
            emfb.setPersistenceUnitName("vertgreen.test");
            emfb.setPersistenceProviderClass(HibernatePersistenceProvider.class);
            emfb.afterPropertiesSet();

            closeEntityManagerFactory();

            emf = emfb.getObject();

            log.info("Started Hibernate");
            state = DatabaseState.READY;
        } catch (Exception ex) {
            state = DatabaseState.FAILED;
            throw new RuntimeException("Failed starting database connection", ex);
        }
    }

    public void reconnectSSH() {
        connectSSH();
        //try a test query and if successful set state to ready
        EntityManager em = getEntityManager();
        try {
            em.getTransaction().begin();
            em.createNativeQuery("SELECT 1;").getResultList();
            em.getTransaction().commit();
            state = DatabaseState.READY;
        } finally {
            em.close();
        }
    }

    private synchronized void connectSSH() {
        if (!Config.CONFIG.isUseSshTunnel()) {
            log.warn("Cannot connect ssh tunnel as it is not specified in the config");
            return;
        }
        if (sshTunnel != null && sshTunnel.isConnected()) {
            log.info("Tunnel is already connected, disconnect first before reconnecting");
            return;
        }
        try {
            //establish the tunnel
            log.info("Starting SSH tunnel");

            java.util.Properties config = new java.util.Properties();
            JSch jsch = new JSch();
            JSch.setLogger(new JSchLogger());

            //Parse host:port
            String sshHost = Config.CONFIG.getSshHost().split(":")[0];
            int sshPort = Integer.parseInt(Config.CONFIG.getSshHost().split(":")[1]);

            Session session = jsch.getSession(Config.CONFIG.getSshUser(),
                    sshHost,
                    sshPort
            );
            jsch.addIdentity(Config.CONFIG.getSshPrivateKeyFile());
            config.put("StrictHostKeyChecking", "no");
            config.put("ConnectionAttempts", "3");
            session.setConfig(config);
            session.setServerAliveInterval(500);//milliseconds
            session.connect();

            log.info("SSH Connected");

            //forward the port
            int assignedPort = session.setPortForwardingL(
                    SSH_TUNNEL_PORT,
                    "localhost",
                    Config.CONFIG.getForwardToPort()
            );

            sshTunnel = session;

            log.info("localhost:" + assignedPort + " -> " + sshHost + ":" + Config.CONFIG.getForwardToPort());
            log.info("Port Forwarded");
        } catch (Exception e) {
            throw new RuntimeException("Failed to start SSH tunnel", e);
        }
    }

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public boolean isAvailable() {
        if (state != DatabaseState.READY) {
            return false;
        }

        //is the ssh connection still alive?
        if (sshTunnel != null && !sshTunnel.isConnected()) {
            log.error("SSH tunnel lost connection.");
            state = DatabaseState.FAILED;
            //immediately try to reconnect the tunnel
            //DBConnectionWatchdogAgent should take further care of this
            VertGreen.executor.submit(this::reconnectSSH);
            return false;
        }

        return state == DatabaseState.READY;
    }

    private synchronized void closeEntityManagerFactory() {
        if (emf != null && emf.isOpen()) {
            try {
                emf.close();
            } catch (IllegalStateException ignored) {
                //it has already been closed, nothing to catch here
            }
        }
    }

    public DatabaseState getState() {
        return state;
    }

    public enum DatabaseState {
        UNINITIALIZED,
        INITIALIZING,
        FAILED,
        READY,
        SHUTDOWN
    }

    public void shutdown() {
        log.info("DatabaseManager shutdown call received, shutting down");
        state = DatabaseState.SHUTDOWN;
        closeEntityManagerFactory();

        if (sshTunnel != null)
            sshTunnel.disconnect();
    }

    private static class JSchLogger implements com.jcraft.jsch.Logger {

        private static final Logger logger = LoggerFactory.getLogger("JSch");

        @Override
        public boolean isEnabled(int level) {
            return true;
        }

        @Override
        public void log(int level, String message) {
            switch (level) {
                case com.jcraft.jsch.Logger.DEBUG:
                    logger.debug(message);
                    break;
                case com.jcraft.jsch.Logger.INFO:
                    logger.info(message);
                    break;
                case com.jcraft.jsch.Logger.WARN:
                    logger.warn(message);
                    break;
                case com.jcraft.jsch.Logger.ERROR:
                case com.jcraft.jsch.Logger.FATAL:
                    logger.error(message);
                    break;
                default:
                    throw new RuntimeException("Invalid log level");
            }
        }
    }

}