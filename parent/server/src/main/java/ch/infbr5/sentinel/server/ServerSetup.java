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

		int noOfRecords = QueryHelper.getCheckpoints().size() + QueryHelper.getConfigurationValues().size()
				+ QueryHelper.findAusweise().size();

		return (noOfRecords == 0);
	}

	public static void setupDatabase() {
		EntityManager em = EntityManagerHelper.getEntityManager();
		em.getTransaction().begin();

		// Standart Zone anlegen
		List<Zutrittsregel> regeln = new ArrayList<Zutrittsregel>(1);
		Zutrittsregel r = ObjectFactory.createZutrittsregel();
		em.persist(r);
		regeln.add(r);
		Zone zone = ObjectFactory.createZone("Kommandoposten", regeln, false);
		em.persist(zone);

		// Standart Checkin anlegen
		List<Zone> checkInZonen = new ArrayList<Zone>();
		checkInZonen.add(zone);
		List<Zone> checkOutZonen = new ArrayList<Zone>();
		Checkpoint checkpoint = ObjectFactory.createCheckpoint("Haupteingang", checkInZonen, checkOutZonen);
		em.persist(checkpoint);

		// Standart Passwort anlgen
		ConfigurationValue v = ObjectFactory.createConfigurationValue("PASSWORD_admin", "1234", 0, "");
		em.persist(v);

		ConfigurationValue v2 = ObjectFactory.createConfigurationValue("IdentityCardPassword", "1nf8r5!", 0, "");
		em.persist(v2);

		ConfigurationValue v3 = ObjectFactory.createConfigurationValue("URL_IPCAM_1", "http://192.168.2.90/image.jpg", 0, "");
		em.persist(v3);

		ConfigurationValue v4 = ObjectFactory.createConfigurationValue("URL_IPCAM_2", "http://192.168.2.91/image.jpg", 0, "");
		em.persist(v4);

		ConfigurationValue v5 = ObjectFactory.createConfigurationValue("URL_IPCAM_3", "http://192.168.2.92/image.jpg", 0, "");
		em.persist(v5);

		ConfigurationValue v6 = ObjectFactory.createConfigurationValue("URL_IPCAM_4", "http://192.168.2.93/image.jpg", 0, "");
		em.persist(v6);

		em.getTransaction().commit();
	}

}
