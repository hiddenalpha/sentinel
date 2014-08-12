package ch.infbr5.sentinel.server;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;

import ch.infbr5.sentinel.server.db.EntityManagerHelper;
import ch.infbr5.sentinel.server.db.QueryHelper;
import ch.infbr5.sentinel.server.model.Checkpoint;
import ch.infbr5.sentinel.server.model.ConfigurationValue;
import ch.infbr5.sentinel.server.model.ObjectFactory;
import ch.infbr5.sentinel.server.model.Zone;
import ch.infbr5.sentinel.server.model.Zutrittsregel;

public class ServerSetup {

	public static boolean databaseIsEmpty() {

		EntityManager em = EntityManagerHelper.createEntityManager();
		QueryHelper qh = new QueryHelper(em);

		int noOfRecords = qh.getCheckpoints().size() + qh.getConfigurationValues().size() + qh.findAusweise().size();

		em.close();

		return (noOfRecords == 0);
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

		// Standart Passwort anlgen
		ConfigurationValue v2 = ObjectFactory.createConfigurationValue("IdentityCardPassword", "1nf8r5!", 0, "");
		em.persist(v2);

		em.getTransaction().commit();

		em.close();
	}

}
