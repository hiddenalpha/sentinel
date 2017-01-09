package ch.infbr5.sentinel.server.db;

import java.util.Properties;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.xml.ws.WebServiceContext;

import org.apache.log4j.Logger;

import ch.infbr5.sentinel.server.ws.EntityManagerHandler;

public class EntityManagerHelper {

	private static final String PERSISTENCE_UNIT_NAME = "sentinel";

	private static EntityManagerFactory factory;

	private static boolean debugMode = false;

	private static boolean inMemoryMode = false;

	private static Logger log = Logger.getLogger(EntityManagerHelper.class);

	public static EntityManager createEntityManager() {
		log.trace("create new entity manager in thread " + Thread.currentThread().getName());
		return EntityManagerHelper.getFactory().createEntityManager();
	}

	public static EntityManager getEntityManager(WebServiceContext context) {
		return (EntityManager) context.getMessageContext().get(EntityManagerHandler.ENTITY_MANAGER_PROPERTY);
	}

	public static void close() {
		if (factory != null) {
			if (factory.isOpen()) {
				factory.close();
			}
			factory = null;
		}
	}

	public static void setDebugMode(boolean mode) {
		debugMode = mode;
	}

	public static void setInMemoryMode(boolean mode) {
		inMemoryMode = mode;
	}

	private static EntityManagerFactory getFactory() {
		if (factory == null) {
			createFactory();
		}
		return factory;
	}

	private static synchronized void createFactory() {
		if (factory == null) {
			Properties props = new Properties();
			if (EntityManagerHelper.debugMode) {
				props.put("javax.persistence.jdbc.driver", "org.apache.derby.jdbc.ClientDriver");
				if (EntityManagerHelper.inMemoryMode) {
					props.put("javax.persistence.jdbc.url", "jdbc:derby://localhost:1527/memory:db;create=true");
				} else {
					props.put("javax.persistence.jdbc.url", "jdbc:derby://localhost:1527/db;create=true");
				}
			} else {
				props.put("javax.persistence.jdbc.driver", "org.apache.derby.jdbc.EmbeddedDriver");
				if (EntityManagerHelper.inMemoryMode) {
					props.put("javax.persistence.jdbc.url", "jdbc:derby:memory:db;create=true");
				} else {
					props.put("javax.persistence.jdbc.url", "jdbc:derby:db;create=true");
				}
			}
			factory = Persistence.createEntityManagerFactory(EntityManagerHelper.PERSISTENCE_UNIT_NAME, props);
		}
	}

}
