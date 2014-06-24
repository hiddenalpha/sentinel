package ch.infbr5.sentinel.server.importer.personen;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import ch.infbr5.sentinel.server.db.QueryHelper;
import ch.infbr5.sentinel.server.importer.personen.util.DataRow;
import ch.infbr5.sentinel.server.importer.personen.util.DtoMapper;
import ch.infbr5.sentinel.server.importer.personen.util.PersistenceUtil;
import ch.infbr5.sentinel.server.importer.personen.util.ValidationRules;
import ch.infbr5.sentinel.server.model.Ausweis;
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

	private static Logger log = Logger.getLogger(PersonenDataImporter.class.getName());

	private String filenameData;

	private boolean isKompletterBestand;

	private List<PersonenImportColumn> columns = new ArrayList<>();

	private List<PersonenImportColumnMapping> columnMappings = new ArrayList<>();

	private ModificationDto modifcationDto;

	public PersonenDataImporter(String filenameData, boolean isKompletterBestand) {
		this.filenameData = filenameData;
		this.isKompletterBestand = isKompletterBestand;

		// Alle Ãœberschriften ablegen
		String[] headerLine = getHeaderLine();
		for (int i = 0; i < headerLine.length; i++) {
			String columnName = headerLine[i];
			if (columnName != null && !"".equals(columnName)) {
				columns.add(new PersonenImportColumn(i, columnName));
			}
		}
	}

	/**
	 * Gibt die Header Line als String Array zurück. Hinterlässt keine offenen
	 * Ressourcen.
	 *
	 * @return Header Informationen.
	 */
	abstract String[] getHeaderLine();

	/**
	 * Öffnet die Datei und liest die erste Datenzeile ein. Die Datei wird nicht
	 * geschlossen. In Kombination mit getNextDataLine().
	 *
	 * @return Erste Datenzeile.
	 */
	abstract String[] getFirstDataLine();

	/**
	 * Gibt die nächste Datenzeile zurück. Nachdem getFirstDataLine aufgerufen
	 * wurde. Schliesst die Datei erst wieder nachdem die letzte Zeile erreicht
	 * wurde.
	 *
	 * @return NÃ¤chste Datenzeile.
	 */
	abstract String[] getNextDataLine();

	/**
	 * Forciert das Schliessen des Files, wenn man nicht alle Zeilen liest.
	 */
	abstract void forceClose();

	/**
	 * Gibt die Anzahl Daten Zeilen zurück.
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

	public void setColumnMappings(List<PersonenImportColumnMapping> columnMappings) {
		this.columnMappings = columnMappings;
	}

	public void setModifications(ModificationDto modificationDto) {
		this.modifcationDto = modificationDto;
	}

	public ModificationDto getModifications() {
		return modifcationDto;
	}

	/**
	 * Evaluiert die Spaltenüberschriften zu den Personenattributen.
	 *
	 * @return Column Mapping.
	 */
	public List<PersonenImportColumnMapping> calculateColumnMappings() {
		getColumnMappings().clear();

		// Mappings Personenattribute zu Pattern von Headernamen
		Map<PersonenAttribute, String[]> map = new HashMap<>();
		map.put(PersonenAttribute.Name, new String[] { ".*name.*" });
		map.put(PersonenAttribute.Vorname, new String[] { ".*vorname.*" });
		map.put(PersonenAttribute.Funktion, new String[] { ".*funktion.*" });
		map.put(PersonenAttribute.Grad, new String[] { ".*grad.*" });
		map.put(PersonenAttribute.AHVNr, new String[] { ".*ahv.*", ".*versicherung.*" });
		map.put(PersonenAttribute.Einheit, new String[] { ".*einheit.*", ".*einteilung.*" });
		map.put(PersonenAttribute.Geburtstag, new String[] {".*geburtsdatum.*", ".*geb.*" });

		// Alle Attribute durchlaufen
		for (PersonenAttribute attribute : map.keySet()) {

			// MÃ¶gliche Kolonen fÃ¼r Attribut auf Basis der Daten
			List<PersonenImportColumn> possibleColumns = calculatePossibleColumns(attribute);

			// Auf Basis des Headers die Kolone suchen
			String[] patterns = map.get(attribute);
			PersonenImportColumn columnToMap = null;
			for (String pattern : patterns) {
				if (columnToMap == null) {
					columnToMap = findColumn(pattern, attribute);
				}
			}

			// PrÃ¼fen ob die Header Kolone in der mÃ¶gliche Kolone enthalten ist
			if (!possibleColumns.contains(columnToMap)) {
				columnToMap = null;
			}

			// Mapping erstellen
			PersonenImportColumnMapping	mappingColumn = new PersonenImportColumnMapping(attribute, columnToMap);
			mappingColumn.setPossibleColumns(possibleColumns.toArray(new PersonenImportColumn[possibleColumns.size()]));
			getColumnMappings().add(mappingColumn);
		}

		return getColumnMappings();
	}

	/**
	 * Berechnet die möglichen Spalten für das entsprechende Attribute. Es wertet die ersten 10 Datensätze aus.
	 *
	 * @param attribute Attribut
	 * @return Mögliche Spalten.
	 */
	private List<PersonenImportColumn> calculatePossibleColumns(PersonenAttribute attribute) {
		List<PersonenImportColumn> columnProduct = new ArrayList<>();

		int countDataLines = getCountDataLines();
		int dataLinesToAnalyze = 10;
		int countOfMatchingLines = 5;

		if (dataLinesToAnalyze >= countDataLines) {
			dataLinesToAnalyze = countDataLines;
			countOfMatchingLines = countDataLines / 2; // rundet ab
		}

		String[] dataline = getFirstDataLine();

		for (int r = 0; r < dataLinesToAnalyze; r++) {
			for (int s = 0; s < dataline.length; s++) {
				// Zur nächsten Spalte springen, falls es keine Kolone dafür gibt
				if (this.columns.size() <= s) {
					continue;
				}
				String data = dataline[s];
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
						if (attribute.equals(PersonenAttribute.Name) || attribute.equals(PersonenAttribute.Vorname) || attribute.equals(PersonenAttribute.Einheit) || attribute.equals(PersonenAttribute.Funktion)) {
							columnProduct.add(this.columns.get(s));
						}
					}
				}
			}
			dataline = getNextDataLine();
		}
		forceClose();

		// Prüft ob die Kolone mind. ein vordefiniertes Mal vorhanden ist.
		List<PersonenImportColumn> effectiveListe = new ArrayList<>();
		for (PersonenImportColumn c : this.columns) {
			if (Collections.frequency(columnProduct, c) >= countOfMatchingLines) {
				effectiveListe.add(c);
			}
		}

		return effectiveListe;
	}

	/**
	 * Prüft ob der Import gültig ist. Er ist gültig wenn fürr jedes
	 * Personenattribut ein Spaltenmapping existiert.
	 *
	 * @return True falls gültig, anderenfalls false.
	 */
	public boolean isValidImportData() {
		if (fileHasMinimalRequirements() != null) {
			return false;
		}
		for (PersonenAttribute attribute : PersonenAttribute.values()) {
			boolean found = false;
			for (PersonenImportColumnMapping mapping : columnMappings) {
				if (mapping.getPersonenAttribute().equals(attribute)) {
					if (mapping.getColumn() != null) {
						found = true;
					}
				}
			}
			if (!found) {
				log.warning("Import ungültig: Kein Mapping für das Attribute " + attribute + ".");
				return false;
			}
		}
		return true;
	}

	public ModificationDto calculateModifications() {

		if (!isValidImportData()) {
			throw new IllegalStateException("Der Import ist nicht gültig daher können die Modifikatione nicht berechnet werden.");
		}

		Set<String> personenAHVNrs = new HashSet<>();
		Set<String> einheitenNamen = new HashSet<>();

		List<ModificationNewPerson> modsNewPerson = new ArrayList<>();
		List<ModificationUpdatePerson> modsUpdatePerson = new ArrayList<>();
		List<ModificationUpdatePersonAndNewAusweis> modsNewAusweis = new ArrayList<>();
		List<ModificationArchivePerson> modsArchivePerson = new ArrayList<>();
		List<ModificationError> modsError = new ArrayList<>();

		for (String[] dataLine = getFirstDataLine(); dataLine != null; dataLine = getNextDataLine()) {

			// Datensatz in Objekt kapseln
			DataRow dataRow = new DataRow(dataLine, columnMappings);

			// Person in Datenbank suchen. Dann erzeugen oder aktualisieren.
			String ahvNr = "";
			String einheitsName = "";

			if (dataRow.isValid()) {
				Person person = PersistenceUtil.findPerson(dataRow);
				if (person == null) {
					ModificationNewPerson mod = new ModificationNewPerson();
					mod.setPersonDetails(dataRow.createPersonDetails());
					modsNewPerson.add(mod);

					ahvNr = mod.getPersonDetails().getAhvNr();
					einheitsName = mod.getPersonDetails().getEinheitText();
				} else {
					if (dataRow.hasDiffs(person)) {

						// Neuen Ausweis erstellen
						// Es wird nur ein neuer Ausweis ausgestellt, wenn sich Attribute unterscheiden
						// und wenn die Person bereits einen gültigen Ausweis hatte.
						boolean modUpdatePersonAndNewAusweis = false;
						if (dataRow.wirdNeuerAusweisBenötigt(person)) {
							Ausweis oldAusweis = person.getValidAusweis();
							if (oldAusweis != null) {
								ModificationUpdatePersonAndNewAusweis mod = new ModificationUpdatePersonAndNewAusweis();
								mod.setPersonDetailsOld(DtoMapper.toPersonDetails(person));
								mod.setPersonDetailsNew(dataRow.createPersonDetails());
								List<UpdatePersonAttributeDiff> diffs = dataRow.getDiffs(person);
								mod.setUpdatePersonenDiffs(diffs.toArray(new UpdatePersonAttributeDiff[diffs.size()]));
								modsNewAusweis.add(mod);
								modUpdatePersonAndNewAusweis = true;
							}
						}

						if (!modUpdatePersonAndNewAusweis) {
							ModificationUpdatePerson mod = new ModificationUpdatePerson();
							mod.setPersonDetailsOld(DtoMapper.toPersonDetails(person));
							mod.setPersonDetailsNew(dataRow.createPersonDetails());
							List<UpdatePersonAttributeDiff> diffs = dataRow.getDiffs(person);
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
				ModificationError mod = new ModificationError();
				mod.setPersonDetails(dataRow.createPersonDetails());
				mod.setErrorMessage(dataRow.validationErrorMessage());
				modsError.add(mod);
			}

		}

		// Alte Personen archivieren
		if (isKompletterBestand()) {
			List<Person> personen = QueryHelper.getPersonen();
			for (Person p : personen) {
				// Falls die Person bei einem kompletten Bestandes import nicht daher gekommen ist und
				// falls die Einheit bereits bekannt ist, dann wird sie archiviert.
				if (!personenAHVNrs.contains(p.getAhvNr())) {
					if ((p.getEinheit() != null) && (einheitenNamen.contains(p.getEinheit().getName()))) {
						ModificationArchivePerson mod = new ModificationArchivePerson();
						mod.setPersonDetails(DtoMapper.toPersonDetails(p));
						modsArchivePerson.add(mod);
					}
				}
			}
		}

		modifcationDto = new ModificationDto();
		modifcationDto.setModificationNewPersons(modsNewPerson.toArray(new ModificationNewPerson[modsNewPerson.size()]));
		modifcationDto.setModificationUpdatePersons(modsUpdatePerson.toArray(new ModificationUpdatePerson[modsUpdatePerson.size()]));
		modifcationDto.setModificationNewAusweise(modsNewAusweis.toArray(new ModificationUpdatePersonAndNewAusweis[modsNewAusweis.size()]));
		modifcationDto.setModificationArchivePersons(modsArchivePerson.toArray(new ModificationArchivePerson[modsArchivePerson.size()]));
		modifcationDto.setModificationErrors(modsError.toArray(new ModificationError[modsError.size()]));

		return modifcationDto;
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
			for (PersonenImportColumnMapping mapping : columnMappings) {
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
	 * Import die Daten. Darf nur ausgeführt werden wenn isValidImportData()
	 * true ist.
	 */
	public void importData() {

		if (!isValidImportData()) {
			throw new IllegalStateException("Der Import ist nicht gültig.");
		}

		for (ModificationNewPerson mod : modifcationDto.getModificationNewPersons()) {
			if (mod.isToModify()) {
				Einheit einheit = PersistenceUtil.createEinheitKompletterBestand(mod.getPersonDetails().getEinheitText(), isKompletterBestand());
				PersistenceUtil.createPerson(mod.getPersonDetails(), einheit);
			}
		}

		for (ModificationUpdatePerson mod : modifcationDto.getModificationUpdatePersons()) {
			if (mod.isToModify()) {
				Person person = PersistenceUtil.findPerson(mod.getPersonDetailsOld());
				System.out.println(mod.getPersonDetailsNew().getName()); // TODO REMOVE
				PersistenceUtil.updatePerson(person, mod.getPersonDetailsNew(), isKompletterBestand());
			}
		}

		for (ModificationUpdatePersonAndNewAusweis mod : modifcationDto.getModificationNewAusweise()) {
			if (mod.isToModify()) {
				Person person = PersistenceUtil.findPerson(mod.getPersonDetailsOld());
				PersistenceUtil.updatePerson(person, mod.getPersonDetailsNew(), isKompletterBestand());
				PersistenceUtil.deactivateAusweis(person.getValidAusweis());
				QueryHelper.createAusweis(person.getId());
			}
		}

		for (ModificationArchivePerson mod : modifcationDto.getModificationArchivePersons()) {
			if (mod.isToModify()) {
				Einheit archivEinheit = PersistenceUtil.getArchivEinheit();
				Person person = PersistenceUtil.findPerson(mod.getPersonDetails());
				person.setEinheit(archivEinheit);
				PersistenceUtil.removeValidAusweis(person);
			}
		}
	}

	private PersonenImportColumn findColumn(String pattern, PersonenAttribute attribute) {
		for (PersonenImportColumn column : columns) {
			if (column.getName().toLowerCase().matches(pattern)) {
				return column;
			}
		}
		return null;
	}

}
