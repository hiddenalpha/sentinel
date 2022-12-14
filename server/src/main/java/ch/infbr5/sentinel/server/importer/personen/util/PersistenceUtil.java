package ch.infbr5.sentinel.server.importer.personen.util;

import java.util.Date;

import javax.persistence.EntityManager;

import ch.infbr5.sentinel.server.db.QueryHelper;
import ch.infbr5.sentinel.server.model.Ausweis;
import ch.infbr5.sentinel.server.model.Einheit;
import ch.infbr5.sentinel.server.model.Grad;
import ch.infbr5.sentinel.server.model.ObjectFactory;
import ch.infbr5.sentinel.server.model.Person;
import ch.infbr5.sentinel.server.ws.PersonDetails;
import ch.infbr5.sentinel.server.ws.importer.mapping.PersonenAttribute;

public class PersistenceUtil {

   private static String NAME_EINHEIT_ARCHIV = "_Archiv_";

   private static String NAME_EINHEIT_GAST = "GAST";

   private final EntityManager entityManager;

   public PersistenceUtil(final EntityManager entityManager) {
      this.entityManager = entityManager;
   }

   private QueryHelper getQueryHelper() {
      return new QueryHelper(entityManager);
   }

   /**
    * Gibt die Einheit _Archiv_ zurück. Falls diese nicht existiert wird sie
    * erzeugt.
    *
    * @return _Archiv_ Einheit
    */
   public Einheit getArchivEinheit() {
      return createEinheit(NAME_EINHEIT_ARCHIV);
   }

   /**
    * Erzeugt auf jedenfall die Person.
    *
    * @param personDetails
    *           PersonDetails.
    * @param einheit
    *           Einheit.
    * @return Person.
    */
   public Person createPerson(final PersonDetails personDetails, final Einheit einheit) {
      return getQueryHelper().createPerson(einheit, personDetails.getAhvNr(), Grad.getGrad(personDetails.getGrad()),
            personDetails.getName(), personDetails.getVorname(), personDetails.getGeburtsdatum(),
            personDetails.getFunktion());
   }

   /**
    * Search a person with the given AHV-Nr.
    *
    * @param PersonDetails
    *           personDetails
    * @return Person or null.
    */
   public Person findPerson(final PersonDetails personDetail) {
      return getQueryHelper().getPerson(personDetail.getAhvNr());
   }

   public Person findPerson(final DataRow dataRow) {
      return getQueryHelper().getPerson(dataRow.getValue(PersonenAttribute.AHVNr));
   }

   /**
    * Entfernt den gültigen Ausweis der Person, falls die Person einen hat.
    * Ausserdem wird der Ausweis deaktiviert.
    *
    * @param person
    *           Person
    */
   public void removeValidAusweis(final Person person) {
      final Ausweis ausweis = person.getValidAusweis();
      if (ausweis != null) {
         deactivateAusweis(ausweis);
         person.setValidAusweis(null);
      }
   }

   /**
    * Deaktiviert den Ausweis. Dannach ist der Ausweis nicht mehr gültig.
    *
    * @param ausweis
    *           Ausweis
    */
   public void deactivateAusweis(final Ausweis ausweis) {
      if (ausweis != null) {
         ausweis.setInvalid(true);
         ausweis.setErstellt(true);
         ausweis.setGueltigBis(new Date());
      }
   }

   /**
    * Sucht die Einheit in der Datenbank aufgrund des Namens.
    *
    * @param String
    *           Name der Einheit.
    * @return Falls die Einheit existiert, dann die Einheit sonst null.
    */
   private Einheit findEinheit(final String name) {
      return getQueryHelper().getEinheit(name);
   }

   /**
    * Sucht die Einheit in der Datenbank aufgrund des Namens.
    *
    * Falls keine Einheit gefunden wird, wird eine mit diesem Name erstellt und
    * persistiert.
    *
    * @param String
    *           Name der Einheit.
    * @return Immer eine Einheit.
    */
   private Einheit createEinheit(final String name) {
      Einheit einheit = findEinheit(name);
      if (einheit == null) {
         einheit = ObjectFactory.createEinheit(name);
         entityManager.persist(einheit);
      }
      return einheit;
   }

   /**
    * Pr�ft ob die Einheit mit diesem Namen existiert.
    *
    * @param name
    *           Name der Einheit
    * @return True, falls die Einheit existiert, anderenfalls false.
    */
   private boolean existsEinheit(final String name) {
      final Einheit einheit = findEinheit(name);
      return (einheit != null);
   }

   /**
    * Gibt die Einheit zum Namen zur�ck. Falls die Einheit nicht existiert wird
    * sie erzeugt.
    *
    * @param einheitName
    *           Name der Einheit
    * @param isKompletterBestand
    *           Gibt an ob der Bestand Komplett ist. Falls ja dann wird die
    *           Einheit definitv erzeugt, falls sie nicht existiert.
    *           Anderenfalls wird die Gast Einheit geladen falls die Einheit
    *           nicht existiert.
    * @return Einheit (nie NULL).
    */
   public Einheit createEinheitKompletterBestand(final String einheitName, final boolean isKompletterBestand) {
      Einheit einheit;
      if (isKompletterBestand) {
         einheit = createEinheit(einheitName);
      } else {
         if (existsEinheit(einheitName)) {
            einheit = findEinheit(einheitName);
         } else {
            einheit = createEinheit(NAME_EINHEIT_GAST);
         }
      }
      return einheit;
   }

   public void updatePerson(final Person person, final PersonDetails details, final boolean isKompletterBestand) {
      person.setAhvNr(details.getAhvNr());
      person.setName(details.getName());
      person.setVorname(details.getVorname());
      person.setFunktion(details.getFunktion());
      person.setGrad(Grad.getGrad(details.getGrad()));
      person.setGeburtsdatum(details.getGeburtsdatum());
      final Einheit einheit = createEinheitKompletterBestand(details.getEinheitText(), isKompletterBestand);
      person.setEinheit(einheit);
   }

}
