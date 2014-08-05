package ch.infbr5.sentinel.server.model;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.persistence.EntityManager;

import org.junit.Ignore;

import ch.infbr5.sentinel.server.db.EntityManagerHelper;
import ch.infbr5.sentinel.server.db.QueryHelper;
@Ignore
public class ModelTest {

	private static void createCheckpoint(EntityManager em,
			List<Zone> checkInZonen) {
		// CheckPoint erstellen
		Checkpoint c = ObjectFactory.createCheckpoint("Hauptzugang",
				checkInZonen, null);
		em.persist(c);
	}

	private static Einheit createEinheit(EntityManager em) {
		Einheit einheit = ObjectFactory.createEinheit("Stab Inf Br 5");
		em.persist(einheit);

		return einheit;
	}

	private static void createPerson1(EntityManager em, Einheit einheit) {
		Calendar cal = Calendar.getInstance();
		cal.set(1976, 6, 14, 0, 0, 0);
		Person person = ObjectFactory.createPerson(einheit, "756.1425.1256.23",
				Grad.MAJ, "Müller", "Hans", cal, "C HQ");
		em.persist(person);

		Ausweis a = ObjectFactory.createAusweis(person, new QueryHelper(em).createUniqueBarcode());

		em.persist(a);
	}

	private static void createPerson2(EntityManager em, Einheit einheit) {
		Calendar cal = Calendar.getInstance();
		cal.set(1970, 1, 11, 0, 0, 0);
		Person person = ObjectFactory.createPerson(einheit, "756.1411.222.22",
				Grad.FOUR, "Rüdisühli", "Claude", cal, "H HQ");
		em.persist(person);

		Ausweis a = ObjectFactory.createAusweis(person, new QueryHelper(em).createUniqueBarcode());

		em.persist(a);
	}

	private static List<Zutrittsregel> createRegeln(EntityManager em) {
		// Regel
		List<Zutrittsregel> regeln = new ArrayList<Zutrittsregel>();
		Zutrittsregel r = ObjectFactory.createZutrittsregel();
		em.persist(r);
		regeln.add(r);

		return regeln;
	}

	private static List<Zone> createZonen(EntityManager em,
			List<Zutrittsregel> regeln) {
		// Zonen erstellen
		List<Zone> checkInZonen = new ArrayList<Zone>();
		Zone z = ObjectFactory.createZone("HQ", regeln, false);
		em.persist(z);
		checkInZonen.add(z);

		return checkInZonen;
	}

	private static boolean deleteDir(File dir) {
		if (dir.isDirectory()) {
			String[] children = dir.list();
			for (String element : children) {
				boolean success = ModelTest.deleteDir(new File(dir, element));
				if (!success) {
					return false;
				}
			}
		}

		// The directory is now empty so delete it
		return dir.delete();
	}

	public static void main(String[] args) {
		try {
			ModelTest.setUp();
		} catch (Exception e) {
			e.printStackTrace();
		}

		ModelTest.setUpDB();
	}

	private static void setUp() throws Exception {
		String currentDir = new File(".").getAbsolutePath();
		System.out.println(currentDir);
		File dbDir = new File("db");
		if (dbDir.exists() && dbDir.isDirectory()) {
			ModelTest.deleteDir(dbDir);
		}
	}

	private static void setUpDB() {
		EntityManagerHelper.setDebugMode(false);
		EntityManager em = EntityManagerHelper.createEntityManager();
		em.getTransaction().begin();

		List<Zutrittsregel> regeln = ModelTest.createRegeln(em);

		List<Zone> checkInZonen = ModelTest.createZonen(em, regeln);

		ModelTest.createCheckpoint(em, checkInZonen);

		Einheit einheit = ModelTest.createEinheit(em);

		ModelTest.createPerson1(em, einheit);
		ModelTest.createPerson2(em, einheit);

		em.getTransaction().commit();

		em.close();
	}
}
