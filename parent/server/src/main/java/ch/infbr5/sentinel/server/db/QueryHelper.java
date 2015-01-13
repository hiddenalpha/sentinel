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

   private final EntityManager em;

   public QueryHelper(final EntityManager entityManager) {
      this.em = entityManager;
   }

   public String createUniqueBarcode() {
      return createUniqueBarcode(ObjectFactory.BARCODE_LAENGE, ObjectFactory.BARCODE_PREFIX);
   }

   public String createUniqueBarcode(final int len, final String prefix) {
      String barcode;
      Ausweis ausweis;
      do {
         final Random zufall = new Random();

         barcode = prefix.concat(String.valueOf(100000 + zufall.nextInt((int) Math.pow(10, len) - 100000)));

         ausweis = findAusweisByBarcode(barcode);
      } while (isAusweisVorhanden(ausweis));

      return barcode;
   }

   @SuppressWarnings("unchecked")
   public Ausweis findAusweisByBarcode(final String barcode) {
      final Query q = em.createNamedQuery(Ausweis.FIND_AUSWEIS_BY_BARCODE_VALUE);
      q.setParameter("barcodeParam", barcode);

      final List<Ausweis> results = q.getResultList();
      if (results.size() == 1) {
         return results.get(0);
      } else {
         return null;
      }
   }

   @SuppressWarnings("unchecked")
   public List<Ausweis> findAusweise() {
      final Query q = em.createNamedQuery(Ausweis.FIND_AUSWEISE_VALUE);
      return q.getResultList();
   }

   @SuppressWarnings("unchecked")
   public List<Ausweis> findAusweiseZumDrucken() {
      final Query q = em.createNamedQuery(Ausweis.FIND_AUSWEISE_ZUM_DRUCKEN);

      return q.getResultList();
   }

   public int invalidateAusweise(final Long personId) {
      final Person personParam = getPerson(personId);
      final Query q = em.createNamedQuery(Ausweis.INVALIDATE_AUSWEISE_BY_PERSON_VALUE);
      q.setParameter("personParam", personParam);

      return q.executeUpdate();
   }

   public void createAusweis(final Long personId) {
      final Person personParam = getPerson(personId);
      final Ausweis ausweis = ObjectFactory.createAusweis(personParam, createUniqueBarcode());
      em.persist(ausweis);

      final Person p = ausweis.getPerson();
      p.setValidAusweis(ausweis);
      em.persist(p);
   }

   public Person createPerson(final Einheit einheit, final String ahvNr, final Grad grad, final String name,
         final String vorname, final Calendar geburtsdatum, final String funktion) {
      final Person person = ObjectFactory.createPerson(einheit, ahvNr, grad, name, vorname, geburtsdatum, funktion);
      em.persist(person);
      return person;
   }

   @SuppressWarnings("unchecked")
   public List<ZonenPraesenz> findZonenPraesenz(final long zoneId, final PraesenzStatus status) {
      final Query q = em.createNamedQuery("findOpenZonenPraesenByStatusAndZone");
      q.setParameter("statusParam", status);
      q.setParameter("zoneParam", zoneId);

      return q.getResultList();
   }

   @SuppressWarnings("unchecked")
   public List<ZonenPraesenz> findZonenPraesenz(final Zone zone, final Person person) {
      final Query q = em.createNamedQuery("findOpenZonenPraesenzByPersonAndZone");
      q.setParameter("personParam", person);
      q.setParameter("zoneParam", zone);

      return q.getResultList();
   }

   public List<Person> getPersonen() {
      return getPersonen(false, false, null);
   }

   @SuppressWarnings("unchecked")
   public List<Person> getPersonen(final boolean nurMitAusweis, final boolean nachEinheit, final String einheit) {
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

   public Person getPerson(final Long id) {
      final Query q = em.createNamedQuery(Person.GET_PERSON_BY_ID_VALUE);
      q.setParameter("personId", id);

      try {
         return (Person) q.getSingleResult();
      } catch (final NoResultException e) {
         return null;
      }
   }

   public Person getPerson(final String avhNr) {
      final Query q = em.createNamedQuery(Person.GET_PERSON_BY_AVHNR);
      q.setParameter("personAhvNr", avhNr);
      try {
         return (Person) q.getSingleResult();
      } catch (final NoResultException e) {
         return null;
      } catch (final NonUniqueResultException e) {
         log.warn("Primary Key " + avhNr + " is not unique.");
         return null;
      }
   }

   public Person getPerson(final String name, final String vorname, final Calendar geb) {
      final Query q = em.createNamedQuery(Person.GET_PERSON_BY_NAME_AND_DATE);
      q.setParameter("personName", name);
      q.setParameter("personVorname", vorname);
      q.setParameter("personGeburtsdatum", geb);
      try {
         return (Person) q.getSingleResult();
      } catch (final NoResultException e) {
         return null;
      }
   }

   public void removeAllPersonData() {
      // --- Personen und Ausweise loeschen ---
      Query q = em.createNamedQuery(Person.GET_PERSONEN_VALUE);
      for (final Iterator iterator = q.getResultList().iterator(); iterator.hasNext();) {
         final Person p = (Person) iterator.next();
         em.remove(p);
      }

      // --- Einheiten loeschen ---
      q = em.createNamedQuery(Einheit.GET_EINHEITEN_VALUE);
      for (final Iterator iterator = q.getResultList().iterator(); iterator.hasNext();) {
         final Einheit p = (Einheit) iterator.next();
         em.remove(p);
      }

      // --- Praesenz loeschen
      q = em.createNamedQuery(ZonenPraesenz.GET_ALL_ZONEN_PRAESENZ);
      for (final Iterator iterator = q.getResultList().iterator(); iterator.hasNext();) {
         final ZonenPraesenz zp = (ZonenPraesenz) iterator.next();
         em.remove(zp);
      }

   }

   public void persistAllPersonData(final List<Person> personen) {
      // aus existenz pruefen und IDs auf null setzen.
      for (final Iterator<Person> iterator = personen.iterator(); iterator.hasNext();) {
         final Person person = iterator.next();

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

   public Checkpoint getCheckpoint(final Long checkpointId) {
      final Query q = em.createNamedQuery("getCheckpointById");
      q.setParameter("checkpointId", checkpointId);
      return (Checkpoint) getSingleResult(q);
   }

   @SuppressWarnings("unchecked")
   public List<Checkpoint> getCheckpoints() {
      final Query q = em.createNamedQuery("getCheckpoints");
      return q.getResultList();
   }

   public Long getCountOfZonenPraesenz(final long zoneId, final PraesenzStatus status) {
      final Query q = em.createNamedQuery("getCountOfOpenZonenPraesenByStatusAndZone");
      q.setParameter("statusParam", status);
      q.setParameter("zoneParam", zoneId);

      return (Long) q.getSingleResult();
   }

   @SuppressWarnings("unchecked")
   public List<SystemMeldung> getSystemMeldungen() {
      final Query q = em.createNamedQuery("findSystemMeldungen");
      return q.getResultList();
   }

   private boolean isAusweisVorhanden(final Ausweis ausweis) {
      return ausweis != null;
   }

   @SuppressWarnings("unchecked")
   public List<ConfigurationValue> findConfigurationValue(final String key) {
      final Query q = em.createNamedQuery("findConfigurationValueByKey");
      q.setParameter("keyParam", key);
      return q.getResultList();
   }

   public ConfigurationValue findConfigurationValueByKey(final String key) {
      final Query q = em.createNamedQuery("findConfigurationValueByKey");
      q.setParameter("keyParam", key);

      final List<ConfigurationValue> result = q.getResultList();
      if (result.isEmpty()) {
         return null;
      } else {
         return result.get(0);
      }
   }

   public ConfigurationValue getConfigurationValueById(final Long id) {
      final Query q = em.createNamedQuery("getConfigurationValueById");
      q.setParameter("idParam", id);
      final List results = q.getResultList();
      if (results.size() == 1) {
         return (ConfigurationValue) results.get(0);
      } else {
         return null;
      }
   }

   /**
    * Gibt ALLE Konfigurations-Werte zur�ck.
    *
    * @return List - Alle Konfigurationswerte
    */
   @SuppressWarnings("unchecked")
   public List<ConfigurationValue> getConfigurationValues() {
      final Query q = em.createNamedQuery("getConfigurationValues");
      return q.getResultList();
   }

   @SuppressWarnings("unchecked")
   public List<PrintJob> getPrintJobs() {
      final Query q = em.createNamedQuery(PrintJob.GET_PRINTJOBS);
      return q.getResultList();
   }

   public PrintJob getPrintJob(final Long id) {
      final Query q = em.createNamedQuery(PrintJob.GET_PRINTJOB_BY_ID);
      q.setParameter("printjobId", id);
      return (PrintJob) getSingleResult(q);
   }

   public Einheit getEinheitById(final Long einheitId) {
      final Query q = em.createNamedQuery(Einheit.GET_EINHEIT_BY_ID_VALUE);
      q.setParameter("einheitId", einheitId);
      return (Einheit) getSingleResult(q);
   }

   public Einheit getEinheit(final String name) {
      final Query q = em.createNamedQuery(Einheit.GET_EINHEIT_BY_NAME);
      q.setParameter("einheitName", name);
      return (Einheit) getSingleResult(q);
   }

   @SuppressWarnings("unchecked")
   public List<Einheit> getEinheiten() {
      final Query q = em.createNamedQuery(Einheit.GET_EINHEITEN_VALUE);
      return q.getResultList();
   }

   @SuppressWarnings("unchecked")
   public List<Zone> getZonen() {
      final Query q = em.createNamedQuery(Zone.GET_ZONEN_VALUE);
      return q.getResultList();
   }

   @SuppressWarnings("unchecked")
   public List<BewegungsMeldung> getBewegungsMeldungen() {
      final Query q = em.createNamedQuery("findBewegungsMeldungen");
      return q.getResultList();
   }

   @SuppressWarnings("unchecked")
   public List<BewegungsMeldung> getBewegungsMeldungenSeit(final long timeInMillis) {
      final Query q = em.createNamedQuery("findBewegungsMeldungenSeit");
      q.setParameter("timeInMillis", timeInMillis);
      return q.getResultList();
   }

   public GefechtsMeldung getGefechtsMeldungen(final long id) {
      final Query q = em.createNamedQuery("findGefechtsMeldung");
      q.setParameter("id", id);
      return (GefechtsMeldung) q.getSingleResult();
   }

   @SuppressWarnings("unchecked")
   public List<GefechtsMeldung> getGefechtsMeldungen() {
      final Query q = em.createNamedQuery("findGefechtsMeldungen");
      return q.getResultList();
   }

   @SuppressWarnings("unchecked")
   public List<GefechtsMeldung> getGefechtsMeldungenSeit(final long timeInMillis) {
      final Query q = em.createNamedQuery("findGefechtsMeldungenSeit");
      q.setParameter("timeInMillis", timeInMillis);
      return q.getResultList();
   }

   @SuppressWarnings("unchecked")
   public List<SystemMeldung> getSystemMeldungenSeit(final long timeInMillis) {
      final Query q = em.createNamedQuery("findSystemMeldungenSeit");
      q.setParameter("timeInMillis", timeInMillis);
      return q.getResultList();
   }

   @SuppressWarnings("unchecked")
   public List<GefechtsMeldung> getPersonTriggerEintraege(final Person person) {
      final Query query = em.createNamedQuery("getPersonTriggerEintraege");
      query.setParameter("idPerson", person.getId());
      return query.getResultList();
   }

   public void removeAllConfiguration() {
      final Query query = em.createNamedQuery(ConfigurationValue.QUERY_ALL_CONFIGURATION);
      for (final ConfigurationValue value : (List<ConfigurationValue>) query.getResultList()) {
         em.remove(value);
      }
   }

   public void persistAllConfiguration(final List<ConfigurationValue> values) {
      for (final ConfigurationValue value : values) {
         value.setId(null);
         em.persist(value);
      }
   }

   private Object getSingleResult(final Query q) {
      try {
         return q.getSingleResult();
      } catch (final NoResultException e) {
         return null;
      }
   }

}
