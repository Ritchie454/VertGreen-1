package vertgreen.db;

import vertgreen.VertGreen;
import vertgreen.db.entity.BlacklistEntry;
import vertgreen.db.entity.GuildConfig;
import vertgreen.db.entity.IEntity;
import vertgreen.db.entity.UConfig;
import org.hibernate.exception.JDBCConnectionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;

public class EntityWriter {

    private static final Logger log = LoggerFactory.getLogger(EntityWriter.class);

    public static void mergeUConfig(UConfig config) {
        merge(config);
    }

    public static void mergeGuildConfig(GuildConfig config) {
        merge(config);
    }

    public static void mergeBlacklistEntry(BlacklistEntry ble) {
        merge(ble);
    }

    private static void merge(IEntity entity) {
        DatabaseManager dbManager = VertGreen.getDbManager();
        if (!dbManager.isAvailable()) {
            throw new DatabaseNotReadyException();
        }

        EntityManager em = dbManager.getEntityManager();
        try {
            em.getTransaction().begin();
            em.merge(entity);
            em.getTransaction().commit();
        } catch (JDBCConnectionException e) {
            log.error("Failed to merge entity {}", entity, e);
            throw new DatabaseNotReadyException(e);
        } finally {
            em.close();
        }
    }

    public static void deleteBlacklistEntry(long id) {
        DatabaseManager dbManager = VertGreen.getDbManager();
        if (!dbManager.isAvailable()) {
            throw new DatabaseNotReadyException("The database is not available currently. Please try again later.");
        }

        EntityManager em = dbManager.getEntityManager();
        try {
            BlacklistEntry ble = em.find(BlacklistEntry.class, id);

            if (ble != null) {
                em.getTransaction().begin();
                em.remove(ble);
                em.getTransaction().commit();
            }
        } finally {
            em.close();
        }
    }
}
