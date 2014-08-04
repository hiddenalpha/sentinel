package ch.infbr5.sentinel.server.db;

import java.util.Properties;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.apache.log4j.Logger;

public class EntityManagerHelper {

	private static Logger log = Logger.getLogger(EntityManagerHelper.class);

	private static final String PERSISTENCE_UNIT_NAME = "sentinel";
	private static EntityManagerFactory factory;
	private static boolean debugMode = false;
	private static boolean inMemoryMode = false;

	private static final ThreadLocal<EntityManager> threadLocalEntityManager = new ThreadLocal<EntityManager>();

	public static EntityManager getEntityManager() {
		EntityManager em = EntityManagerHelper.threadLocalEntityManager.get();
		if ((em == null) || (em.isOpen()==false)) {
			em = EntityManagerHelper.getFactory().createEntityManager();
			EntityManagerHelper.threadLocalEntityManager.set(em);
			log.debug("create EntityManager for Thread " + Thread.currentThread().getName());
		}

		return em;
	}

	public static void close(){
		EntityManagerHelper.factory.close();
		EntityManagerHelper.factory = null;
	}

	public static EntityManagerFactory getFactory() {
		if (EntityManagerHelper.factory == null) {
			Properties props = new Properties();

			if (EntityManagerHelper.debugMode) {
				props.put("javax.persistence.jdbc.driver", "org.apache.derby.jdbc.ClientDriver");
				if (EntityManagerHelper.inMemoryMode){
					props.put("javax.persistence.jdbc.url", "jdbc:derby://localhost:1527/memory:db;create=true");
				} else {
					props.put("javax.persistence.jdbc.url", "jdbc:derby://localhost:1527/db;create=true");
				}
			} else {
				props.put("javax.persistence.jdbc.driver", "org.apache.derby.jdbc.EmbeddedDriver");
				if (EntityManagerHelper.inMemoryMode){
					props.put("javax.persistence.jdbc.url", "jdbc:derby:memory:db;create=true");
				} else {
					props.put("javax.persistence.jdbc.url", "jdbc:derby:db;create=true");
				}
			}

			EntityManagerHelper.factory = Persistence
					.createEntityManagerFactory(EntityManagerHelper.PERSISTENCE_UNIT_NAME, props);
		}

		return EntityManagerHelper.factory;
	}

	public static void setDebugMode(boolean mode) {
		EntityManagerHelper.debugMode = mode;
	}

	public static void setInMemoryMode(boolean mode){
		EntityManagerHelper.inMemoryMode  = mode;
	}
}
