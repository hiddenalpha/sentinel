package ch.infbr5.sentinel.server.importer.personen.util;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.apache.log4j.Logger;

import ch.infbr5.sentinel.common.util.DateFormater;
import ch.infbr5.sentinel.server.model.Grad;
import ch.infbr5.sentinel.server.model.Person;
import ch.infbr5.sentinel.server.utils.DateParser;
import ch.infbr5.sentinel.server.ws.PersonDetails;
import ch.infbr5.sentinel.server.ws.importer.mapping.PersonenAttribute;
import ch.infbr5.sentinel.server.ws.importer.mapping.PersonenImportColumnMapping;
import ch.infbr5.sentinel.server.ws.importer.modification.UpdatePersonAttributeDiff;

public class DataRow {

   private static Logger log = Logger.getLogger(DataRow.class);

   private final String[] data;

   private final List<PersonenImportColumnMapping> mappings;

   private Calendar geburtstag;

   public DataRow(final String[] data, final List<PersonenImportColumnMapping> mappings) {
      this.data = data;
      this.mappings = mappings;
      try {
         this.geburtstag = DateParser.parseDateStringToCalendar(this.getValue(PersonenAttribute.Geburtstag));
      } catch (final ParseException e) {
         log.warn("Geburtsdatum von " + getValue(PersonenAttribute.Geburtstag) + " konnte nicht geparst werden.");
         this.geburtstag = Calendar.getInstance();
      }
   }

   public Calendar getGeburtstag() {
      return this.geburtstag;
   }

   public boolean isValid() {
      return validationErrorMessage() == null;
   }

   /**
    * Pr�ft ob der Datensatz g�ltig ist.
    *
    * Valid bedeutet: - Alle Attribute vorhanden und nicht leer oder null -
    * Geburtsdatum, AHV-Nur, Grad in einem g�ltigen Format.
    *
    * @return True, falls ja, anderenfalls nein.
    */
   public String validationErrorMessage() {
      final String errorMessage = null;

      String str = this.getValue(PersonenAttribute.Name);
      if (!ValidationRules.isValidString(str)) {
         return "Name nicht gesetzt.";
      }

      str = this.getValue(PersonenAttribute.Vorname);
      if (!ValidationRules.isValidString(str)) {
         return "Vorname nicht gesetzt.";
      }

      str = this.getValue(PersonenAttribute.Funktion);
      if (!ValidationRules.isValidString(str)) {
         return "Funktion nicht gesetzt.";
      }

      str = this.getValue(PersonenAttribute.Einheit);
      if (!ValidationRules.isValidString(str)) {
         return "Einheit nicht gesetzt.";
      }

      str = this.getValue(PersonenAttribute.AHVNr);
      if (!ValidationRules.isValidString(str) || !ValidationRules.isValidAhvNr(str)) {
         return "AHVNr nicht gesetzt oder nicht gültig (" + str + ").";
      }

      str = this.getValue(PersonenAttribute.Geburtstag);
      if (!ValidationRules.isValidString(str) || !ValidationRules.isValidGeburtstag(str)) {
         return "Geburtstag nicht gesetzt oder nicht gültig (" + str + ").";
      }

      str = this.getValue(PersonenAttribute.Grad);
      if (!ValidationRules.isValidString(str) || !ValidationRules.isValidGrad(str)) {
         return "Grad nicht gesetzt oder nicht gültig (" + str + ").";
      }

      return errorMessage;
   }

   public String getValue(final PersonenAttribute attribute) {
      for (final PersonenImportColumnMapping mapping : mappings) {
         if (mapping.getPersonenAttribute().equals(attribute)) {
            return data[mapping.getColumn().getIndex()];
         }
      }
      return null;
   }

   /**
    * Berechnet ob ein neue Ausweis benoetigt wird. Der Abgleich wird zwischen
    * dem Datensatz und der �bergebenen Person gemacht.
    *
    * Momentan ist der Fall so, dass es einen Ausweis ben�tigt, sobald sich
    * irgendein PersonenAttribut ge�ndert hat.
    *
    * @param oldPerson
    *           Alte Personendaten.
    * @return True, falls ein neuer Ausweis benoetigt wird, anderenfalls false.
    */
   public boolean wirdNeuerAusweisBenoetigt(final Person oldPerson) {
      return hasDiffs(oldPerson);
   }

   /**
    * Prüft ob es Differenzen hat.
    *
    * @param person
    *           Personen.
    * @return True, falls es Differenzen hat, anderenfalls false.
    */
   public boolean hasDiffs(final Person person) {
      return !getDiffs(person).isEmpty();
   }

   public List<UpdatePersonAttributeDiff> getDiffs(final Person oldPerson) {
      final List<UpdatePersonAttributeDiff> diffs = new ArrayList<>();

      if (!oldPerson.getAhvNr().equals(this.getValue(PersonenAttribute.AHVNr))) {
         diffs.add(createDiff(PersonenAttribute.AHVNr, oldPerson.getAhvNr(), this.getValue(PersonenAttribute.AHVNr)));
      }
      if (!oldPerson.getName().equals(this.getValue(PersonenAttribute.Name))) {
         diffs.add(createDiff(PersonenAttribute.Name, oldPerson.getName(), this.getValue(PersonenAttribute.Name)));
      }
      if (!oldPerson.getVorname().equals(this.getValue(PersonenAttribute.Vorname))) {
         diffs.add(createDiff(PersonenAttribute.Vorname, oldPerson.getVorname(),
               this.getValue(PersonenAttribute.Vorname)));
      }
      if (!oldPerson.getFunktion().equals(this.getValue(PersonenAttribute.Funktion))) {
         diffs.add(createDiff(PersonenAttribute.Funktion, oldPerson.getFunktion(),
               this.getValue(PersonenAttribute.Funktion)));
      }

      if ((oldPerson.getGrad() == null)
            || (!oldPerson.getGrad().equals(Grad.getGrad(this.getValue(PersonenAttribute.Grad))))) {
         String oldValue = "";
         if (oldPerson.getGrad() != null) {
            oldValue = oldPerson.getGrad().toString();
         }
         diffs.add(createDiff(PersonenAttribute.Grad, oldValue, this.getValue(PersonenAttribute.Grad)));
      }

      if ((oldPerson.getGeburtsdatum() == null)
            || (!DateFormater.formatToDate(oldPerson.getGeburtsdatum()).equals(
                  this.getValue(PersonenAttribute.Geburtstag)))) {
         String oldValue = "";
         if (oldPerson.getGeburtsdatum() != null) {
            oldValue = DateFormater.formatToDate(oldPerson.getGeburtsdatum());
         }
         diffs.add(createDiff(PersonenAttribute.Geburtstag, oldValue, this.getValue(PersonenAttribute.Geburtstag)));
      }

      if ((oldPerson.getEinheit() == null)
            || (!oldPerson.getEinheit().getName().equals(this.getValue(PersonenAttribute.Einheit)))) {
         String oldValue = "";
         if (oldPerson.getEinheit() != null) {
            oldValue = oldPerson.getEinheit().getName();
         }
         diffs.add(createDiff(PersonenAttribute.Einheit, oldValue, this.getValue(PersonenAttribute.Einheit)));
      }
      return diffs;
   }

   public PersonDetails createPersonDetails() {
      final PersonDetails p = new PersonDetails();
      p.setAhvNr(this.getValue(PersonenAttribute.AHVNr));
      p.setName(this.getValue(PersonenAttribute.Name));
      p.setFunktion(this.getValue(PersonenAttribute.Funktion));
      p.setGeburtsdatum(this.getGeburtstag());
      p.setVorname(this.getValue(PersonenAttribute.Vorname));
      p.setEinheitId(-1l);
      p.setEinheitText(this.getValue(PersonenAttribute.Einheit));
      p.setGrad(this.getValue(PersonenAttribute.Grad));
      return p;
   }

   private UpdatePersonAttributeDiff createDiff(final PersonenAttribute personenAttribute, final String oldValue,
         final String newValue) {
      final UpdatePersonAttributeDiff diff = new UpdatePersonAttributeDiff();
      diff.setPersonenAttribute(personenAttribute);
      diff.setNewValue(newValue);
      diff.setOldValue(oldValue);
      return diff;
   }

}
