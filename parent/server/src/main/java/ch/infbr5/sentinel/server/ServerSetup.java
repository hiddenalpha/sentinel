package ch.infbr5.sentinel.server;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;

import ch.infbr5.sentinel.common.config.ConfigConstants;
import ch.infbr5.sentinel.server.db.EntityManagerHelper;
import ch.infbr5.sentinel.server.db.QueryHelper;
import ch.infbr5.sentinel.server.model.Checkpoint;
import ch.infbr5.sentinel.server.model.ObjectFactory;
import ch.infbr5.sentinel.server.model.Zone;
import ch.infbr5.sentinel.server.model.Zutrittsregel;

public class ServerSetup {

	public static boolean databaseIsEmpty() {
		EntityManager em = EntityManagerHelper.createEntityManager();
		QueryHelper queryHelper = new QueryHelper(em);
		int noCheckpoints = queryHelper.getCheckpoints().size();
		int noConfigurations = queryHelper.getConfigurationValues().size();
		int noAusweise = queryHelper.findAusweise().size();
		em.close();

		int noOfRecords = noCheckpoints + noConfigurations  + noAusweise;
		return noOfRecords == 0;
	}

	public static void setupDatabase() {
		EntityManager em = EntityManagerHelper.createEntityManager();
		em.getTransaction().begin();

		// Zutrittsregeln
		Zutrittsregel regel = ObjectFactory.createZutrittsregel();
		em.persist(regel);

		List<Zutrittsregel> regeln = new ArrayList<Zutrittsregel>();
		regeln.add(regel);

		// Zone
		Zone zone = ObjectFactory.createZone("Kommandoposten", regeln, false);
		em.persist(zone);

		// Checkpoint
		List<Zone> checkInZonen = new ArrayList<Zone>();
		checkInZonen.add(zone);
		List<Zone> checkOutZonen = new ArrayList<Zone>();
		Checkpoint checkpoint = ObjectFactory.createCheckpoint("Haupteingang", checkInZonen, checkOutZonen);
		em.persist(checkpoint);

		// Konfiguration
		em.persist(ObjectFactory.createConfigurationValue(ConfigConstants.ADMIN_PASSWORD, "leitnes", 0, ""));
		em.persist(ObjectFactory.createConfigurationValue(ConfigConstants.SUPERUSER_PASSWORD, "sentinel", 0, ""));
		em.persist(ObjectFactory.createConfigurationValue(ConfigConstants.IDENTITY_CARD_PASSWORD, "1nf8r5!", 0, ""));

		em.getTransaction().commit();
		em.close();
	}

}
