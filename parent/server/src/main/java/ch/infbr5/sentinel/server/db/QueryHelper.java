package ch.infbr5.sentinel.server.db;

import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.Query;

import org.apache.log4j.Logger;

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

public class QueryHelper {

	private static Logger log = Logger.getLogger(QueryHelper.class);

	private EntityManager em;

	public QueryHelper(EntityManager entityManager) {
		this.em = entityManager;
	}

	public String createUniqueBarcode() {
		return createUniqueBarcode(ObjectFactory.BARCODE_LAENGE, ObjectFactory.BARCODE_PREFIX);
	}

	public String createUniqueBarcode(int len, String prefix) {
		String barcode;
		Ausweis ausweis;
		do {
			Random zufall = new Random();

			barcode = prefix.concat(String.valueOf(100000 + zufall.nextInt((int) Math.pow(10, len) - 100000)));

			ausweis = findAusweisByBarcode(barcode);
		} while (isAusweisVorhanden(ausweis));

		return barcode;
	}

	@SuppressWarnings("unchecked")
	public Ausweis findAusweisByBarcode(String barcode) {
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
	public List<Ausweis> findAusweise() {
		Query q = em.createNamedQuery(Ausweis.FIND_AUSWEISE_VALUE);
		return q.getResultList();
	}

	@SuppressWarnings("unchecked")
	public List<Ausweis> findAusweiseZumDrucken() {
		Query q = em.createNamedQuery(Ausweis.FIND_AUSWEISE_ZUM_DRUCKEN);

		return q.getResultList();
	}

	public int invalidateAusweise(Long personId) {
		Person personParam = getPerson(personId);
		Query q = em.createNamedQuery(Ausweis.INVALIDATE_AUSWEISE_BY_PERSON_VALUE);
		q.setParameter("personParam", personParam);

		return q.executeUpdate();
	}

	public void createAusweis(Long personId) {
		Person personParam = getPerson(personId);
		Ausweis ausweis = ObjectFactory.createAusweis(personParam, createUniqueBarcode());
		em.persist(ausweis);

		Person p = ausweis.getPerson();
		p.setValidAusweis(ausweis);
		em.persist(p);
	}

	public Person createPerson(Einheit einheit, String ahvNr, Grad grad, String name, String vorname,
			Calendar geburtsdatum, String funktion) {
		Person person = ObjectFactory.createPerson(einheit, ahvNr, grad, name, vorname, geburtsdatum, funktion);
		em.persist(person);
		return person;
	}

	@SuppressWarnings("unchecked")
	public List<ZonenPraesenz> findZonenPraesenz(long zoneId, PraesenzStatus status) {
		Query q = em.createNamedQuery("findOpenZonenPraesenByStatusAndZone");
		q.setParameter("statusParam", status);
		q.setParameter("zoneParam", zoneId);

		return q.getResultList();
	}

	@SuppressWarnings("unchecked")
	public List<ZonenPraesenz> findZonenPraesenz(Zone zone, Person person) {
		Query q = em.createNamedQuery("findOpenZonenPraesenzByPersonAndZone");
		q.setParameter("personParam", person);
		q.setParameter("zoneParam", zone);

		return q.getResultList();
	}

	public List<Person> getPersonen() {
		return getPersonen(false, false, null);
	}

	@SuppressWarnings("unchecked")
	public List<Person> getPersonen(boolean nurMitAusweis, boolean nachEinheit, String einheit) {
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

	public Person getPerson(Long id) {
		Query q = em.createNamedQuery(Person.GET_PERSON_BY_ID_VALUE);
		q.setParameter("personId", id);

		try {
			return (Person) q.getSingleResult();
		} catch (NoResultException e) {
			return null;
		}
	}

	public Person getPerson(String avhNr) {
		Query q = em.createNamedQuery(Person.GET_PERSON_BY_AVHNR);
		q.setParameter("personAhvNr", avhNr);
		try {
			return (Person) q.getSingleResult();
		} catch (NoResultException e) {
			return null;
		} catch (NonUniqueResultException e) {
			log.warn("Primary Key " + avhNr + " is not unique.");
			return null;
		}
	}

	public Person getPerson(String name, String vorname, Calendar geb) {
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

	public void removeAllPersonData() {
		// --- Personen und Ausweise loeschen ---
		Query q = em.createNamedQuery(Person.GET_PERSONEN_VALUE);
		for (Iterator iterator = q.getResultList().iterator(); iterator.hasNext();) {
			Person p = (Person) iterator.next();
			em.remove(p);
		}

		// --- Einheiten loeschen ---
		q = em.createNamedQuery(Einheit.GET_EINHEITEN_VALUE);
		for (Iterator iterator = q.getResultList().iterator(); iterator.hasNext();) {
			Einheit p = (Einheit) iterator.next();
			em.remove(p);
		}

		// --- Praesenz loeschen
		q = em.createNamedQuery(ZonenPraesenz.GET_ALL_ZONEN_PRAESENZ);
		for (Iterator iterator = q.getResultList().iterator(); iterator.hasNext();) {
			ZonenPraesenz zp = (ZonenPraesenz) iterator.next();
			em.remove(zp);
		}

	}

	public void persistAllPersonData(List<Person> personen) {
		// aus existenz prüfen und IDs auf null setzen.
		for (Iterator<Person> iterator = personen.iterator(); iterator.hasNext();) {
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

	public Checkpoint getCheckpoint(Long checkpointId) {
		Query q = em.createNamedQuery("getCheckpointById");
		q.setParameter("checkpointId", checkpointId);

		try {
			return (Checkpoint) q.getSingleResult();
		} catch (NoResultException e) {
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	public List<Checkpoint> getCheckpoints() {
		Query q = em.createNamedQuery("getCheckpoints");
		return q.getResultList();
	}

	public Long getCountOfZonenPraesenz(long zoneId, PraesenzStatus status) {
		Query q = em.createNamedQuery("getCountOfOpenZonenPraesenByStatusAndZone");
		q.setParameter("statusParam", status);
		q.setParameter("zoneParam", zoneId);

		return (Long) q.getSingleResult();
	}

	@SuppressWarnings("unchecked")
	public List<SystemMeldung> getSystemMeldungen(long checkpointId, int maxResult) {
		Query q = em.createNamedQuery("findLogEintrag");
		q.setParameter("checkpointId", checkpointId);
		q.setMaxResults(maxResult);
		return q.getResultList();
	}

	private boolean isAusweisVorhanden(Ausweis ausweis) {
		return ausweis != null;
	}

	@SuppressWarnings("unchecked")
	public List<ConfigurationValue> findConfigurationValue(String key) {
		Query q = em.createNamedQuery("findConfigurationValueByKey");
		q.setParameter("keyParam", key);
		return q.getResultList();
	}

	public ConfigurationValue getConfigurationValueById(Long id) {
		Query q = em.createNamedQuery("getConfigurationValueById");
		q.setParameter("idParam", id);
		List results = q.getResultList();
		if (results.size() == 1) {
			return (ConfigurationValue) results.get(0);
		} else {
			return null;
		}
	}

	/**
	 * Gibt ALLE Konfigurations-Werte zurück.
	 *
	 * @return List - Alle Konfigurationswerte
	 */
	@SuppressWarnings("unchecked")
	public List<ConfigurationValue> getConfigurationValues() {
		Query q = em.createNamedQuery("getConfigurationValues");
		return q.getResultList();
	}

	@SuppressWarnings("unchecked")
	public List<PrintJob> getPrintJobs() {
		Query q = em.createNamedQuery(PrintJob.GET_PRINTJOBS);
		return q.getResultList();
	}

	@SuppressWarnings("unchecked")
	public List<PrintJob> getPrintJobs(Long id) {
		Query q = em.createNamedQuery(PrintJob.GET_PRINTJOB_BY_ID);
		q.setParameter("printjobId", id);
		return q.getResultList();
	}

	public Einheit getEinheitById(Long einheitId) {
		Query q = em.createNamedQuery(Einheit.GET_EINHEIT_BY_ID_VALUE);
		q.setParameter("einheitId", einheitId);

		try {
			return (Einheit) q.getSingleResult();
		} catch (NoResultException e) {
			return null;
		}
	}

	public Einheit getEinheit(String name) {
		Query q = em.createNamedQuery(Einheit.GET_EINHEIT_BY_NAME);
		q.setParameter("einheitName", name);

		try {
			return (Einheit) q.getSingleResult();
		} catch (NoResultException e) {
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	public List<Einheit> getEinheiten() {
		Query q = em.createNamedQuery(Einheit.GET_EINHEITEN_VALUE);
		return q.getResultList();
	}

	@SuppressWarnings("unchecked")
	public List<Zone> getZonen() {
		Query q = em.createNamedQuery(Zone.GET_ZONEN_VALUE);
		return q.getResultList();
	}

	@SuppressWarnings("unchecked")
	public List<BewegungsMeldung> getBewegungsMeldungenSeit(long timeInMillis) {
		Query q = em.createNamedQuery("findBewegungsMeldungenSeit");
		q.setParameter("timeInMillis", timeInMillis);
		return q.getResultList();
	}

	public GefechtsMeldung getGefechtsMeldungen(long id) {
		Query q = em.createNamedQuery("findGefechtsMeldung");
		q.setParameter("id", id);
		return (GefechtsMeldung) q.getSingleResult();
	}

	@SuppressWarnings("unchecked")
	public List<GefechtsMeldung> getGefechtsMeldungenSeit(long timeInMillis) {
		Query q = em.createNamedQuery("findGefechtsMeldungenSeit");
		q.setParameter("timeInMillis", timeInMillis);
		return q.getResultList();
	}

	@SuppressWarnings("unchecked")
	public List<SystemMeldung> getSystemMeldungenSeit(long timeInMillis) {
		Query q = em.createNamedQuery("findSystemMeldungenSeit");
		q.setParameter("timeInMillis", timeInMillis);
		return q.getResultList();
	}

	@SuppressWarnings("unchecked")
	public List<GefechtsMeldung> getPersonTriggerEintraege(Person person) {
		Query query = em.createNamedQuery("getPersonTriggerEintraege");
		query.setParameter("idPerson", person.getId());
		return query.getResultList();
	}

	public void removeAllConfiguration() {
		Query query = em.createNamedQuery(ConfigurationValue.QUERY_ALL_CONFIGURATION);
		for (ConfigurationValue value : (List<ConfigurationValue>) query.getResultList()) {
			em.remove(value);
		}
	}

	public void persistAllConfiguration(List<ConfigurationValue> values) {
		for (ConfigurationValue value : values) {
			value.setId(null);
			em.persist(value);
		}
	}
}
