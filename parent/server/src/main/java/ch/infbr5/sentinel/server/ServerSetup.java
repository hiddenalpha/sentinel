package ch.infbr5.sentinel.server;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;

import ch.infbr5.sentinel.server.db.EntityManagerHelper;
import ch.infbr5.sentinel.server.db.QueryHelper;
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

		// Standart Zone anlegen
		List<Zutrittsregel> regeln = new ArrayList<Zutrittsregel>(1);
		Zutrittsregel r = ObjectFactory.createZutrittsregel();
		em.persist(r);
		regeln.add(r);
		Zone zone = ObjectFactory.createZone("Kommandoposten", regeln, false);
		em.persist(zone);

		// Standart Passwort anlgen
		ConfigurationValue v2 = ObjectFactory.createConfigurationValue("IdentityCardPassword", "1nf8r5!", 0, "");
		em.persist(v2);

		em.getTransaction().commit();
		em.close();
	}

}
