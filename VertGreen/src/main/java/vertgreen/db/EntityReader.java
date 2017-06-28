package vertgreen.db;


import vertgreen.VertGreen;
import vertgreen.db.entity.BlacklistEntry;
import vertgreen.db.entity.GuildConfig;
import vertgreen.db.entity.IEntity;
import vertgreen.db.entity.UConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;
import java.util.List;

public class EntityReader {

    private static final Logger log = LoggerFactory.getLogger(EntityReader.class);

    public static UConfig getUConfig(String id) {
        return getEntity(id, UConfig.class);
    }

    public static GuildConfig getGuildConfig(String id) {
        return getEntity(id, GuildConfig.class);
    }

    private static <E extends IEntity> E getEntity(String id, Class<E> clazz) throws DatabaseNotReadyException {
        DatabaseManager dbManager = VertGreen.getDbManager();
        if (!dbManager.isAvailable()) {
            throw new DatabaseNotReadyException();
        }

        EntityManager em = dbManager.getEntityManager();
        E config = null;
        try {
            config = em.find(clazz, id);
        } catch (PersistenceException e) {
            log.error("Error while trying to find entity of class {} from DB for id {}", clazz.getName(), id, e);
            throw new DatabaseNotReadyException(e);
        } finally {
            em.close();
        }
        //return a fresh object if we didn't found the one we were looking for
        if (config == null) config = newInstance(id, clazz);
        return config;
    }

    private static <E extends IEntity> E newInstance(String id, Class<E> clazz) {
        try {
            E entity = clazz.newInstance();
            entity.setId(id);
            return entity;
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException("Could not create an entity of class " + clazz.getName(), e);
        }
    }

    public static List<BlacklistEntry> loadBlacklist() {
        DatabaseManager dbManager = VertGreen.getDbManager();
        if (!dbManager.isAvailable()) {
            throw new DatabaseNotReadyException("The database is not available currently. Please try again later.");
        }
        EntityManager em = dbManager.getEntityManager();
        List<BlacklistEntry> result;
        try {
            result = em.createQuery("SELECT b FROM BlacklistEntry b", BlacklistEntry.class).getResultList();
        } finally {
            em.close();
        }
        return result;
    }
}
