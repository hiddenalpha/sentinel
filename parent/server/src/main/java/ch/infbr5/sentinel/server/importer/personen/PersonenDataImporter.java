package ch.infbr5.sentinel.server.importer.personen;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.EntityManager;

import org.apache.log4j.Logger;

import ch.infbr5.sentinel.server.db.PersonImageStore;
import ch.infbr5.sentinel.server.db.QueryHelper;
import ch.infbr5.sentinel.server.importer.personen.util.DataRow;
import ch.infbr5.sentinel.server.importer.personen.util.DtoMapper;
import ch.infbr5.sentinel.server.importer.personen.util.PersistenceUtil;
import ch.infbr5.sentinel.server.importer.personen.util.ValidationRules;
import ch.infbr5.sentinel.server.model.Einheit;
import ch.infbr5.sentinel.server.model.Person;
import ch.infbr5.sentinel.server.ws.importer.mapping.PersonenAttribute;
import ch.infbr5.sentinel.server.ws.importer.mapping.PersonenImportColumn;
import ch.infbr5.sentinel.server.ws.importer.mapping.PersonenImportColumnMapping;
import ch.infbr5.sentinel.server.ws.importer.modification.ModificationArchivePerson;
import ch.infbr5.sentinel.server.ws.importer.modification.ModificationDto;
import ch.infbr5.sentinel.server.ws.importer.modification.ModificationError;
import ch.infbr5.sentinel.server.ws.importer.modification.ModificationNewPerson;
import ch.infbr5.sentinel.server.ws.importer.modification.ModificationUpdatePerson;
import ch.infbr5.sentinel.server.ws.importer.modification.ModificationUpdatePersonAndNewAusweis;
import ch.infbr5.sentinel.server.ws.importer.modification.UpdatePersonAttributeDiff;

abstract class PersonenDataImporter {

   private static Logger log = Logger.getLogger(PersonenDataImporter.class);

   private final String filenameData;

   private final boolean isKompletterBestand;

   private final List<PersonenImportColumn> columns = new ArrayList<>();

   private List<PersonenImportColumnMapping> columnMappings = new ArrayList<>();

   private ModificationDto modifcationDto;

   private final EntityManager entityManager;

   public PersonenDataImporter(final EntityManager em, final String filenameData, final boolean isKompletterBestand) {
      this.entityManager = em;
      this.filenameData = filenameData;
      this.isKompletterBestand = isKompletterBestand;

      // Alle Überschriften ablegen
      final String[] headerLine = getHeaderLine();
      for (int i = 0; i < headerLine.length; i++) {
         final String columnName = headerLine[i];
         if (columnName != null && !"".equals(columnName)) {
            columns.add(new PersonenImportColumn(i, columnName));
         }
      }
   }

   /**
    * Gibt die Header Line als String Array zur�ck. Hinterl�sst keine offenen
    * Ressourcen.
    *
    * @return Header Informationen.
    */
   abstract String[] getHeaderLine();

   /**
    * �ffnet die Datei und liest die erste Datenzeile ein. Die Datei wird nicht
    * geschlossen. In Kombination mit getNextDataLine().
    *
    * @return Erste Datenzeile.
    */
   abstract String[] getFirstDataLine();

   /**
    * Gibt die n�chste Datenzeile zur�ck. Nachdem getFirstDataLine aufgerufen
    * wurde. Schliesst die Datei erst wieder nachdem die letzte Zeile erreicht
    * wurde.
    *
    * @return Nächste Datenzeile.
    */
   abstract String[] getNextDataLine();

   /**
    * Forciert das Schliessen des Files, wenn man nicht alle Zeilen liest.
    */
   abstract void forceClose();

   /**
    * Gibt die Anzahl Daten Zeilen zur�ck.
    *
    * @return Anzahl Daten Zeilen.
    */
   abstract int getCountDataLines();

   protected String getFilenameData() {
      return filenameData;
   }

   protected boolean isKompletterBestand() {
      return isKompletterBestand;
   }

   public List<PersonenImportColumn> getColumns() {
      return columns;
   }

   public List<PersonenImportColumnMapping> getColumnMappings() {
      return columnMappings;
   }

   public void setColumnMappings(final List<PersonenImportColumnMapping> columnMappings) {
      this.columnMappings = columnMappings;
   }

   public void setModifications(final ModificationDto modificationDto) {
      this.modifcationDto = modificationDto;
   }

   public ModificationDto getModifications() {
      return modifcationDto;
   }

   /**
    * Evaluiert die Spalten�berschriften zu den Personenattributen.
    *
    * @return Column Mapping.
    */
   public List<PersonenImportColumnMapping> calculateColumnMappings() {
      getColumnMappings().clear();

      // Mappings Personenattribute zu Pattern von Headernamen
      final Map<PersonenAttribute, String[]> map = new HashMap<>();
      map.put(PersonenAttribute.Name, new String[] { ".*name.*" });
      map.put(PersonenAttribute.Vorname, new String[] { ".*vorname.*" });
      map.put(PersonenAttribute.Funktion, new String[] { ".*funktion.*" });
      map.put(PersonenAttribute.Grad, new String[] { ".*grad.*" });
      map.put(PersonenAttribute.AHVNr, new String[] { ".*ahv.*", ".*versicherung.*" });
      map.put(PersonenAttribute.Einheit, new String[] { ".*einheit.*", ".*einteilung.*" });
      map.put(PersonenAttribute.Geburtstag, new String[] { ".*geburtsdatum.*", ".*geb.*" });

      // Alle Attribute durchlaufen
      for (final PersonenAttribute attribute : map.keySet()) {

         // M�gliche Kolonen f�r Attribut auf Basis der Daten
         final List<PersonenImportColumn> possibleColumns = calculatePossibleColumns(attribute);

         // Auf Basis des Headers die Kolone suchen
         final String[] patterns = map.get(attribute);
         PersonenImportColumn columnToMap = null;
         for (final String pattern : patterns) {
            if (columnToMap == null) {
               columnToMap = findColumn(pattern, attribute);
            }
         }

         // Pr�fen ob die Header Kolone in der m�gliche Kolone enthalten ist
         if (!possibleColumns.contains(columnToMap)) {
            columnToMap = null;
         }

         // Mapping erstellen
         final PersonenImportColumnMapping mappingColumn = new PersonenImportColumnMapping(attribute, columnToMap);
         mappingColumn.setPossibleColumns(possibleColumns.toArray(new PersonenImportColumn[possibleColumns.size()]));
         getColumnMappings().add(mappingColumn);
      }

      return getColumnMappings();
   }

   /**
    * Berechnet die m�glichen Spalten f�r das entsprechende Attribute. Es wertet
    * die ersten 10 Datens�tze aus.
    *
    * @param attribute
    *           Attribut
    * @return M�gliche Spalten.
    */
   private List<PersonenImportColumn> calculatePossibleColumns(final PersonenAttribute attribute) {
      final List<PersonenImportColumn> columnProduct = new ArrayList<>();

      final int countDataLines = getCountDataLines();
      int dataLinesToAnalyze = 10;
      int countOfMatchingLines = 5;

      if (dataLinesToAnalyze >= countDataLines) {
         dataLinesToAnalyze = countDataLines;
         countOfMatchingLines = countDataLines / 2; // rundet ab
      }

      String[] dataline = getFirstDataLine();

      for (int r = 0; r < dataLinesToAnalyze; r++) {
         for (int s = 0; s < dataline.length; s++) {
            // Zur n�chsten Spalte springen, falls es keine Kolone daf�r gibt
            if (this.columns.size() <= s) {
               continue;
            }
            final String data = dataline[s];
            if (ValidationRules.isValidString(data)) {

               boolean specialColumn = false;

               if (ValidationRules.isValidGrad(data)) {
                  specialColumn = true;
                  if (attribute.equals(PersonenAttribute.Grad)) {
                     columnProduct.add(this.columns.get(s));
                  }
               }
               if (ValidationRules.isValidAhvNr(data)) {
                  specialColumn = true;
                  if (attribute.equals(PersonenAttribute.AHVNr)) {
                     columnProduct.add(this.columns.get(s));
                  }
               }
               if (ValidationRules.isValidGeburtstag(data)) {
                  specialColumn = true;
                  if (attribute.equals(PersonenAttribute.Geburtstag)) {
                     columnProduct.add(this.columns.get(s));
                  }
               }
               if (!specialColumn) {
                  if (attribute.equals(PersonenAttribute.Name) || attribute.equals(PersonenAttribute.Vorname)
                        || attribute.equals(PersonenAttribute.Einheit) || attribute.equals(PersonenAttribute.Funktion)) {
                     columnProduct.add(this.columns.get(s));
                  }
               }
            }
         }
         dataline = getNextDataLine();
      }
      forceClose();

      // Pr�ft ob die Kolone mind. ein vordefiniertes Mal vorhanden ist.
      final List<PersonenImportColumn> effectiveListe = new ArrayList<>();
      for (final PersonenImportColumn c : this.columns) {
         if (Collections.frequency(columnProduct, c) >= countOfMatchingLines) {
            effectiveListe.add(c);
         }
      }

      return effectiveListe;
   }

   /**
    * Pr�ft ob der Import g�ltig ist. Er ist g�ltig wenn f�rr jedes
    * Personenattribut ein Spaltenmapping existiert.
    *
    * @return True falls g�ltig, anderenfalls false.
    */
   public boolean isValidImportData() {
      if (fileHasMinimalRequirements() != null) {
         return false;
      }
      for (final PersonenAttribute attribute : PersonenAttribute.values()) {
         boolean found = false;
         for (final PersonenImportColumnMapping mapping : columnMappings) {
            if (mapping.getPersonenAttribute().equals(attribute)) {
               if (mapping.getColumn() != null) {
                  found = true;
               }
            }
         }
         if (!found) {
            log.warn("Import ung�ltig: Kein Mapping f�r das Attribute " + attribute + ".");
            return false;
         }
      }
      return true;
   }

   public ModificationDto calculateModifications() {

      if (!isValidImportData()) {
         throw new IllegalStateException(
               "Der Import ist nicht gültig daher können die Modifikationen nicht berechnet werden.");
      }

      final Set<String> personenAHVNrs = new HashSet<>();
      final Set<String> einheitenNamen = new HashSet<>();

      final List<ModificationNewPerson> modsNewPerson = new ArrayList<>();
      final List<ModificationUpdatePerson> modsUpdatePerson = new ArrayList<>();
      final List<ModificationUpdatePersonAndNewAusweis> modsNewAusweis = new ArrayList<>();
      final List<ModificationArchivePerson> modsArchivePerson = new ArrayList<>();
      final List<ModificationError> modsError = new ArrayList<>();

      for (String[] dataLine = getFirstDataLine(); dataLine != null; dataLine = getNextDataLine()) {

         // Datensatz in Objekt kapseln
         final DataRow dataRow = new DataRow(dataLine, columnMappings);

         // Person in Datenbank suchen. Dann erzeugen oder aktualisieren.
         String ahvNr = "";
         String einheitsName = "";

         if (dataRow.isValid()) {
            final Person person = getPersistenceUtil().findPerson(dataRow);
            if (person == null) {
               // Kann kein Ausweis nachsich ziehen, da kein Foto vorhanden
               // ist.s
               final ModificationNewPerson mod = new ModificationNewPerson();
               mod.setPersonDetails(dataRow.createPersonDetails());
               modsNewPerson.add(mod);

               ahvNr = mod.getPersonDetails().getAhvNr();
               einheitsName = mod.getPersonDetails().getEinheitText();
            } else {
               if (dataRow.hasDiffs(person)) {

                  final List<UpdatePersonAttributeDiff> diffs = dataRow.getDiffs(person);

                  // Es wird nur ein neuer Ausweis ausgestellt, wenn sich
                  // Attribute unterscheiden
                  // und wenn die Person bereits einen gültigen Ausweis hatte.
                  if (dataRow.wirdNeuerAusweisBenoetigt(person) && person.getValidAusweis() != null) {
                     modsNewAusweis.add(createModificationUpdatePersonAndNewAusweis(person, dataRow, diffs));
                  } else if (istPersonInArchivUndEsWirdEinerNeuenEinheitZugewiesenUndEsExistiertEinFoto(dataRow, person)) {
                     modsNewAusweis.add(createModificationUpdatePersonAndNewAusweis(person, dataRow, diffs));
                  } else {
                     final ModificationUpdatePerson mod = new ModificationUpdatePerson();
                     mod.setPersonDetailsOld(DtoMapper.toPersonDetails(person));
                     mod.setPersonDetailsNew(dataRow.createPersonDetails());
                     mod.setUpdatePersonenDiffs(diffs.toArray(new UpdatePersonAttributeDiff[diffs.size()]));
                     modsUpdatePerson.add(mod);
                  }

               }

               ahvNr = person.getAhvNr();
               einheitsName = person.getEinheit().getName();
            }

            personenAHVNrs.add(ahvNr);
            einheitenNamen.add(einheitsName);
         } else {
            final ModificationError mod = new ModificationError();
            mod.setPersonDetails(dataRow.createPersonDetails());
            mod.setErrorMessage(dataRow.validationErrorMessage());
            modsError.add(mod);
         }

      }

      // Alte Personen archivieren
      if (isKompletterBestand()) {
         final List<Person> personen = getQueryHelper().getPersonen();
         for (final Person p : personen) {
            // Falls die Person bei einem kompletten Bestandes import nicht
            // daher gekommen ist und
            // falls die Einheit bereits bekannt ist, dann wird sie archiviert.
            if (!personenAHVNrs.contains(p.getAhvNr())) {
               if ((p.getEinheit() != null) && (einheitenNamen.contains(p.getEinheit().getName()))) {
                  final ModificationArchivePerson mod = new ModificationArchivePerson();
                  mod.setPersonDetails(DtoMapper.toPersonDetails(p));
                  modsArchivePerson.add(mod);
               }
            }
         }
      }

      modifcationDto = new ModificationDto();
      modifcationDto.setModificationNewPersons(modsNewPerson.toArray(new ModificationNewPerson[modsNewPerson.size()]));
      modifcationDto.setModificationUpdatePersons(modsUpdatePerson
            .toArray(new ModificationUpdatePerson[modsUpdatePerson.size()]));
      modifcationDto.setModificationNewAusweise(modsNewAusweis
            .toArray(new ModificationUpdatePersonAndNewAusweis[modsNewAusweis.size()]));
      modifcationDto.setModificationArchivePersons(modsArchivePerson
            .toArray(new ModificationArchivePerson[modsArchivePerson.size()]));
      modifcationDto.setModificationErrors(modsError.toArray(new ModificationError[modsError.size()]));

      return modifcationDto;
   }

   private ModificationUpdatePersonAndNewAusweis createModificationUpdatePersonAndNewAusweis(final Person person,
         final DataRow dataRow, final List<UpdatePersonAttributeDiff> diffs) {
      final ModificationUpdatePersonAndNewAusweis mod = new ModificationUpdatePersonAndNewAusweis();
      mod.setPersonDetailsOld(DtoMapper.toPersonDetails(person));
      mod.setPersonDetailsNew(dataRow.createPersonDetails());
      mod.setUpdatePersonenDiffs(diffs.toArray(new UpdatePersonAttributeDiff[diffs.size()]));
      return mod;
   }

   /**
    * Prüft ob die Person ursprünglich der ARCHIV-Einheit zugewiesen war. Prüft
    * ob die Person nun eine neue Einheit, also nicht mehr der ARCHIV-Einheit
    * zugewiesen ist. Prüft ob die Person ein Foto hat.
    *
    * Die Kombination daraus (UND) ergibt, dass ein Ausweis erstellt wird.
    *
    * @param dataRow
    *           Neue Daten im DataRow.
    * @param person
    *           Aktuelle Personen Daten im System.
    * @return True falls alle Bediengungen stimmen, sonst false.
    */
   private boolean istPersonInArchivUndEsWirdEinerNeuenEinheitZugewiesenUndEsExistiertEinFoto(final DataRow dataRow,
         final Person person) {
      final boolean isArchivEinheit = person.getEinheit().getName()
            .equals(getPersistenceUtil().getArchivEinheit().getName());
      final boolean isNewEinheitAssigned = !person.getEinheit().getName()
            .equals(dataRow.getValue(PersonenAttribute.Einheit));
      final boolean existsFoto = PersonImageStore.hasImage(person.getAhvNr());
      return isArchivEinheit && isNewEinheitAssigned && existsFoto;
   }

   public String fileHasMinimalRequirements() {
      String result = null;
      if (columns.size() < 7) {
         return "Datei besitzt nicht minimal 7 Spalten.";
      }
      if (getCountDataLines() <= 0) {
         return "Datei besitzt keine Daten.";
      }
      if (columnMappings.isEmpty()) {
         calculateColumnMappings();
         for (final PersonenImportColumnMapping mapping : columnMappings) {
            if (mapping.getPersonenAttribute().equals(PersonenAttribute.Geburtstag)) {
               if (mapping.getPossibleColumns().length == 0) {
                  result = "Datei besitzt keine Geburtstagsspalte.";
               }
            }
            if (mapping.getPersonenAttribute().equals(PersonenAttribute.AHVNr)) {
               if (mapping.getPossibleColumns().length == 0) {
                  result = "Datei besitzt keine AHV-Nr Spalte.";
               }
            }
            if (mapping.getPersonenAttribute().equals(PersonenAttribute.Grad)) {
               if (mapping.getPossibleColumns().length == 0) {
                  result = "Datei besitzt keine Grad Spalte.";
               }
            }
         }
      }
      return result;
   }

   /**
    * Import die Daten. Darf nur ausgef�hrt werden wenn isValidImportData() true
    * ist.
    */
   public void importData() {

      if (!isValidImportData()) {
         throw new IllegalStateException("Der Import ist nicht gültig.");
      }

      for (final ModificationNewPerson mod : modifcationDto.getModificationNewPersons()) {
         if (mod.isToModify()) {
            final Einheit einheit = getPersistenceUtil().createEinheitKompletterBestand(
                  mod.getPersonDetails().getEinheitText(), isKompletterBestand());
            getPersistenceUtil().createPerson(mod.getPersonDetails(), einheit);
         }
      }

      for (final ModificationUpdatePerson mod : modifcationDto.getModificationUpdatePersons()) {
         if (mod.isToModify()) {
            final Person person = getPersistenceUtil().findPerson(mod.getPersonDetailsOld());
            getPersistenceUtil().updatePerson(person, mod.getPersonDetailsNew(), isKompletterBestand());
         }
      }

      for (final ModificationUpdatePersonAndNewAusweis mod : modifcationDto.getModificationNewAusweise()) {
         if (mod.isToModify()) {
            final Person person = getPersistenceUtil().findPerson(mod.getPersonDetailsOld());
            getPersistenceUtil().updatePerson(person, mod.getPersonDetailsNew(), isKompletterBestand());
            getPersistenceUtil().deactivateAusweis(person.getValidAusweis());
            getQueryHelper().createAusweis(person.getId());
         }
      }

      for (final ModificationArchivePerson mod : modifcationDto.getModificationArchivePersons()) {
         if (mod.isToModify()) {
            final Einheit archivEinheit = getPersistenceUtil().getArchivEinheit();
            final Person person = getPersistenceUtil().findPerson(mod.getPersonDetails());
            person.setEinheit(archivEinheit);
            getPersistenceUtil().removeValidAusweis(person);
         }
      }
   }

   private PersonenImportColumn findColumn(final String pattern, final PersonenAttribute attribute) {
      for (final PersonenImportColumn column : columns) {
         if (column.getName().toLowerCase().matches(pattern)) {
            return column;
         }
      }
      return null;
   }

   private QueryHelper getQueryHelper() {
      return new QueryHelper(entityManager);
   }

   private PersistenceUtil getPersistenceUtil() {
      return new PersistenceUtil(entityManager);
   }

}
