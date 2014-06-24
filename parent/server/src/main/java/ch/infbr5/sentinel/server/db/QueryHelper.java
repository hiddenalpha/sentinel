package ch.infbr5.sentinel.server.db;

import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.logging.Logger;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.Query;

import ch.infbr5.sentinel.server.model.Ausweis;
import ch.infbr5.sentinel.server.model.Checkpoint;
import ch.infbr5.sentinel.server.model.ConfigurationValue;
import ch.infbr5.sentinel.server.model.Einheit;
import ch.infbr5.sentinel.server.model.Grad;
import ch.infbr5.sentinel.server.model.ObjectFactory;
import ch.infbr5.sentinel.server.model.Person;
import ch.infbr5.sentinel.server.model.PraesenzStatus;
import ch.infbr5.sentinel.server.model.PrintJob;
import ch.infbr5.sentinel.server.model.Zone;
import ch.infbr5.sentinel.server.model.ZonenPraesenz;
import ch.infbr5.sentinel.server.model.journal.BewegungsMeldung;
import ch.infbr5.sentinel.server.model.journal.GefechtsMeldung;
import ch.infbr5.sentinel.server.model.journal.SystemMeldung;

import com.google.common.collect.Lists;

public class QueryHelper {

	private static Logger logger = Logger.getLogger(QueryHelper.class.getName());

	public static String createUniqueBarcode(int len, String prefix) {
		String barcode;
		Ausweis ausweis;
		do {
			Random zufall = new Random();

			barcode = prefix.concat(String.valueOf(100000 + zufall
					.nextInt((int) Math.pow(10, len) - 100000)));

			ausweis = QueryHelper.findAusweisByBarcode(barcode);
		} while (QueryHelper.isAusweisVorhanden(ausweis));

		return barcode;
	}

	@SuppressWarnings("unchecked")
	public static Ausweis findAusweisByBarcode(String barcode) {
		EntityManager em = EntityManagerHelper.getEntityManager();
		Query q = em.createNamedQuery(Ausweis.FIND_AUSWEIS_BY_BARCODE_VALUE);
		q.setParameter("barcodeParam", barcode);

		List<Ausweis> results = (List<Ausweis>) q.getResultList();
		if (results.size() == 1) {
			return (Ausweis) results.get(0);
		} else {
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	public static List<Ausweis> findAusweise() {
		EntityManager em = EntityManagerHelper.getEntityManager();
		Query q = em.createNamedQuery(Ausweis.FIND_AUSWEISE_VALUE);

		return q.getResultList();
	}

	@SuppressWarnings("unchecked")
	public static List<Ausweis> findAusweiseZumDrucken() {
		EntityManager em = EntityManagerHelper.getEntityManager();
		Query q = em.createNamedQuery(Ausweis.FIND_AUSWEISE_ZUM_DRUCKEN);

		return q.getResultList();
	}

	public static int invalidateAusweise(Long personId) {
		EntityManager em = EntityManagerHelper.getEntityManager();
		Person personParam = getPerson(personId);
		Query q = em
				.createNamedQuery(Ausweis.INVALIDATE_AUSWEISE_BY_PERSON_VALUE);
		q.setParameter("personParam", personParam);

		return q.executeUpdate();
	}

	public static void createAusweis(Long personId) {
		EntityManager em = EntityManagerHelper.getEntityManager();
		Person personParam = getPerson(personId);
		Ausweis ausweis = ObjectFactory.createAusweis(personParam);
		em.persist(ausweis);

		Person p = ausweis.getPerson();
		p.setValidAusweis(ausweis);
		em.persist(p);
	}

	public static Person createPerson(Einheit einheit, String ahvNr, Grad grad,
			String name, String vorname, Calendar geburtsdatum, String funktion) {
		EntityManager em = EntityManagerHelper.getEntityManager();
		Person person = ObjectFactory.createPerson(einheit, ahvNr, grad, name,
				vorname, geburtsdatum, funktion);
		em.persist(person);
		return person;
	}

	@SuppressWarnings("unchecked")
	public static List<ZonenPraesenz> findZonenPraesenz(long zoneId,
			PraesenzStatus status) {
		EntityManager em = EntityManagerHelper.getEntityManager();
		Query q = em.createNamedQuery("findOpenZonenPraesenByStatusAndZone");
		q.setParameter("statusParam", status);
		q.setParameter("zoneParam", zoneId);

		return q.getResultList();
	}

	@SuppressWarnings("unchecked")
	public static List<ZonenPraesenz> findZonenPraesenz(Zone zone, Person person) {
		EntityManager em = EntityManagerHelper.getEntityManager();
		Query q = em.createNamedQuery("findOpenZonenPraesenzByPersonAndZone");
		q.setParameter("personParam", person);
		q.setParameter("zoneParam", zone);

		return q.getResultList();
	}

	public static List<Person> getPersonen() {
		return getPersonen(false, false, null);
	}

	@SuppressWarnings("unchecked")
	public static List<Person> getPersonen(boolean nurMitAusweis,
			boolean nachEinheit, String einheit) {
		EntityManager em = EntityManagerHelper.getEntityManager();
		Query q;
		if (nurMitAusweis) {
			if (nachEinheit) {
				q = em.createNamedQuery(Person.GET_PERSONEN_MIT_AUSWEIS_NACH_EINHEIT);
				q.setParameter("einheitName", einheit);
			} else {
				q = em.createNamedQuery(Person.GET_PERSONEN_MIT_AUSWEIS);
			}
		} else {
			if (nachEinheit) {
				q = em.createNamedQuery(Person.GET_PERSONEN_NACH_EINHEIT);
				q.setParameter("einheitName", einheit);
			} else {
				q = em.createNamedQuery(Person.GET_PERSONEN_VALUE);
			}
		}

		return q.getResultList();
	}

	public static Person getPerson(Long id) {
		EntityManager em = EntityManagerHelper.getEntityManager();
		Query q = em.createNamedQuery(Person.GET_PERSON_BY_ID_VALUE);
		q.setParameter("personId", id);

		try {
			return (Person) q.getSingleResult();
		} catch (NoResultException e) {
			return null;
		}
	}

	public static Person getPerson(String avhNr) {
		EntityManager em = EntityManagerHelper.getEntityManager();
		Query q = em.createNamedQuery(Person.GET_PERSON_BY_AVHNR);
		q.setParameter("personAhvNr", avhNr);
		try {
			return (Person) q.getSingleResult();
		} catch (NoResultException e) {
			return null;
		} catch (NonUniqueResultException e) {
			logger.severe("Primary Key " + avhNr + " is not unique.");
			return null;
		}
	}

	public static Person getPerson(String name, String vorname, Calendar geb) {
		EntityManager em = EntityManagerHelper.getEntityManager();
		Query q = em.createNamedQuery(Person.GET_PERSON_BY_NAME_AND_DATE);
		q.setParameter("personName", name);
		q.setParameter("personVorname", vorname);
		q.setParameter("personGeburtsdatum", geb);
		try {
			return (Person) q.getSingleResult();
		} catch (NoResultException e) {
			return null;
		}
	}

	public static void removeAllPersonData() {
		EntityManager em = EntityManagerHelper.getEntityManager();

		// --- Personen und Ausweise loeschen ---
		Query q = em.createNamedQuery(Person.GET_PERSONEN_VALUE);
		for (Iterator iterator = q.getResultList().iterator(); iterator
				.hasNext();) {
			Person p = (Person) iterator.next();
			em.remove(p);
		}

		// --- Einheiten loeschen ---
		q = em.createNamedQuery(Einheit.GET_EINHEITEN_VALUE);
		for (Iterator iterator = q.getResultList().iterator(); iterator
				.hasNext();) {
			Einheit p = (Einheit) iterator.next();
			em.remove(p);
		}

		// --- Praesenz loeschen
		q = em.createNamedQuery(ZonenPraesenz.GET_ALL_ZONEN_PRAESENZ);
		for (Iterator iterator = q.getResultList().iterator(); iterator
				.hasNext();) {
			ZonenPraesenz zp = (ZonenPraesenz) iterator.next();
			em.remove(zp);
		}

	}

	public static void persistAllPersonData(List<Person> personen) {
		EntityManager em = EntityManagerHelper.getEntityManager();

		// aus existenz prüfen und IDs auf null setzen.
		for (Iterator<Person> iterator = personen.iterator(); iterator
				.hasNext();) {
			Person person = iterator.next();

			person.setId(null);
			if (person.getValidAusweis() != null) {
				person.getValidAusweis().setId(null);
				em.persist(person.getValidAusweis());
			}

			if (person.getEinheit() != null) {
				person.getEinheit().setId(null);
				em.persist(person.getEinheit());
			}

			em.persist(person);
		}

	}

	public static Checkpoint getCheckpoint(Long checkpointId) {
		EntityManager em = EntityManagerHelper.getEntityManager();
		Query q = em.createNamedQuery("getCheckpointById");
		q.setParameter("checkpointId", checkpointId);

		try {
			return (Checkpoint) q.getSingleResult();
		} catch (NoResultException e) {
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	public static List<Checkpoint> getCheckpoints() {
		EntityManager em = EntityManagerHelper.getEntityManager();
		Query q = em.createNamedQuery("getCheckpoints");

		return q.getResultList();
	}

	public static Long getCountOfZonenPraesenz(long zoneId,
			PraesenzStatus status) {
		EntityManager em = EntityManagerHelper.getEntityManager();
		Query q = em
				.createNamedQuery("getCountOfOpenZonenPraesenByStatusAndZone");
		q.setParameter("statusParam", status);
		q.setParameter("zoneParam", zoneId);

		return (Long) q.getSingleResult();
	}

	@SuppressWarnings("unchecked")
	public static List<SystemMeldung> getSystemMeldungen(long checkpointId,
			int maxResult) {
		EntityManager em = EntityManagerHelper.getEntityManager();
		Query q = em.createNamedQuery("findLogEintrag");
		q.setParameter("checkpointId", checkpointId);
		q.setMaxResults(maxResult);
		return q.getResultList();
	}

	// public static OperatorEintrag getPersonTriggerEintrag(String barcode) {
	// EntityManager em = EntityManagerHelper.getEntityManager();
	// Query q = em.createNamedQuery("getPersonTriggerEintrag");
	// q.setParameter("barcode", barcode);
	//
	// try {
	// return (OperatorEintrag) q.getSingleResult();
	// } catch (Exception e) {
	// return null;
	// }
	// }

	private static boolean isAusweisVorhanden(Ausweis ausweis) {
		return ausweis != null;
	}

	/*public static boolean updateOperatorEintrag(GefechtsMeldung operatorEintrag) {
		EntityManager em = EntityManagerHelper.getEntityManager();
		boolean transactionDone = true;
		try {
			GefechtsMeldung operatorEintragX = em.find(GefechtsMeldung.class, operatorEintrag.getId());
			operatorEintragX.setAction(operatorEintrag.getAction());
			operatorEintragX.setCause(operatorEintrag.getCause());
			operatorEintragX.setCreator(operatorEintrag.getCreator());
			operatorEintragX.setDate(operatorEintrag.getDate());
			operatorEintragX.setDone(true);
			operatorEintragX.setPersonTriggerId(operatorEintrag
					.getPersonTriggerId());
		} catch (Exception e) {
			transactionDone = false;
		}

		return transactionDone;
	}*/

	@SuppressWarnings("unchecked")
	public static List<ConfigurationValue> findConfigurationValue(String key) {

		EntityManager em = EntityManagerHelper.getEntityManager();
		Query q = em.createNamedQuery("findConfigurationValueByKey");
		q.setParameter("keyParam", key);

		return q.getResultList();

	}

	public static ConfigurationValue getConfigurationValueById(Long id) {
		EntityManager em = EntityManagerHelper.getEntityManager();
		Query q = em.createNamedQuery("getConfigurationValueById");
		q.setParameter("idParam", id);
		List results = q.getResultList();
		if (results.size() == 1) {
			return (ConfigurationValue) results.get(0);
		} else {
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	public static List<ConfigurationValue> getConfigurationValues() {
		EntityManager em = EntityManagerHelper.getEntityManager();
		Query q = em.createNamedQuery("getConfigurationValues");

		return q.getResultList();
	}

	@SuppressWarnings("unchecked")
	public static List<PrintJob> getPrintJobs() {
		EntityManager em = EntityManagerHelper.getEntityManager();
		Query q = em.createNamedQuery(PrintJob.GET_PRINTJOBS);

		return q.getResultList();
	}

	@SuppressWarnings("unchecked")
	public static List<PrintJob> getPrintJobs(Long id) {
		EntityManager em = EntityManagerHelper.getEntityManager();
		Query q = em.createNamedQuery(PrintJob.GET_PRINTJOB_BY_ID);
		q.setParameter("printjobId", id);

		return q.getResultList();
	}

	public static Einheit getEinheitById(Long einheitId) {
		EntityManager em = EntityManagerHelper.getEntityManager();
		Query q = em.createNamedQuery(Einheit.GET_EINHEIT_BY_ID_VALUE);
		q.setParameter("einheitId", einheitId);

		try {
			return (Einheit) q.getSingleResult();
		} catch (NoResultException e) {
			return null;
		}
	}

	public static Einheit getEinheit(String name) {
		EntityManager em = EntityManagerHelper.getEntityManager();
		Query q = em.createNamedQuery(Einheit.GET_EINHEIT_BY_NAME);
		q.setParameter("einheitName", name);

		try {
			return (Einheit) q.getSingleResult();
		} catch (NoResultException e) {
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	public static List<Einheit> getEinheiten() {
		EntityManager em = EntityManagerHelper.getEntityManager();
		Query q = em.createNamedQuery(Einheit.GET_EINHEITEN_VALUE);

		return q.getResultList();
	}

	@SuppressWarnings("unchecked")
	public static List<Zone> getZonen() {
		EntityManager em = EntityManagerHelper.getEntityManager();
		Query q = em.createNamedQuery(Zone.GET_ZONEN_VALUE);

		return q.getResultList();
	}

	public static List<BewegungsMeldung> getBewegungsMeldungen(long checkpointId,
			int i) {
		// TODO Auto-generated method stub
		return Lists.newArrayList();
	}

	public static List<GefechtsMeldung> getGefechtsMeldungen(long checkpointId,
			int maxResult) {
		EntityManager em = EntityManagerHelper.getEntityManager();
		Query q = em.createNamedQuery("findGefechtsMeldungen");
		q.setParameter("checkpointId", checkpointId);
		q.setMaxResults(maxResult);
		return q.getResultList();
	}

	public static GefechtsMeldung getGefechtsMeldungen(long id) {
		EntityManager em = EntityManagerHelper.getEntityManager();
		Query q = em.createNamedQuery("findGefechtsMeldung");
		q.setParameter("id", id);
		return (GefechtsMeldung) q.getSingleResult();
	}

}
