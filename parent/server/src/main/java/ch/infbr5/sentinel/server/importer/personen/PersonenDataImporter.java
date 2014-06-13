package ch.infbr5.sentinel.server.importer.personen;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import ch.infbr5.sentinel.server.db.QueryHelper;
import ch.infbr5.sentinel.server.model.Ausweis;
import ch.infbr5.sentinel.server.model.Einheit;
import ch.infbr5.sentinel.server.model.Grad;
import ch.infbr5.sentinel.server.model.Person;
import ch.infbr5.sentinel.server.ws.importer.Column;
import ch.infbr5.sentinel.server.ws.importer.MappingPersonenAttributeToColumn;
import ch.infbr5.sentinel.server.ws.importer.PersonenAttribute;

abstract class PersonenDataImporter {

	private static Logger log = Logger.getLogger(PersonenDataImporter.class.getName());
	
	private String filenameData;
	
	private boolean isKompletterBestand;

	// Vollständige Liste aller verfügbaren Spaltenbezeichnungen
	private List<Column> columns = new ArrayList<>();
	
	// Aktuelle Mappings
	private List<MappingPersonenAttributeToColumn> columnMappings = new ArrayList<>();

	// Alle importierten Einheiten
	private Set<Einheit> importedEinheiten = new HashSet<>();

	// Alle importierte Personen
	private Set<Person> importedPersonen = new HashSet<>();

	public PersonenDataImporter(String filenameData, boolean isKompletterBestand) {
		this.filenameData = filenameData;
		this.isKompletterBestand = isKompletterBestand;
		
		String[] headerLine = getHeaderLine();
		
		// Alle Überschriften ablegen.
		columns.clear();
		for (int i = 0; i < headerLine.length; i++) {
			String columnName = headerLine[i];
			if (columnName != null && !"".equals(columnName)) {
				columns.add(new Column(i, columnName));
			}
		}
	}
	
	/**
	 * Gibt die Header Line als String Array zurück. Hinterlässt keine offenen Ressourcen.
	 * 
	 * @return Header Informationen.
	 */
	abstract String[] getHeaderLine();

	/**
	 * Öffnet die Datei und liest die erste Datenzeile ein. Die Datei wird nicht geschlossen.
	 * In Kombination mit getNextDataLine().
	 * @return Erste Datenzeile.
	 */
	abstract String[] getFirstDataLine();
	
	/**
	 * Gibt die nächste Datenzeile zurück. Nachdem getFirstDataLine aufgerufen wurde. Schliesst
	 * die Datei erst wieder nachdem die letzte Zeile erreicht wurde.
	 * 
	 * @return Nächste Datenzeile.
	 */
	abstract String[] getNextDataLine();
	
	public List<Column> getColumns() {
		return columns;
	}
	
	public List<MappingPersonenAttributeToColumn> getColumnMappings() {
		return columnMappings;
	}

	public void setColumnMappings(List<MappingPersonenAttributeToColumn> columnMappings) {
		this.columnMappings = columnMappings;
	}

	protected boolean isKompletterBestand() {
		return isKompletterBestand;
	}
	
	protected String getFilenameData() {
		return filenameData;
	}
	
	/**
	 * Evaluiert die Spaltenüberschriften zu den Personenattributen.
	 * 
	 * @return Column Mapping.
	 */
	public List<MappingPersonenAttributeToColumn> calculateColumnMappings() {
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
			String[] patterns = map.get(attribute);

			// Die MappingColumn suchen und entsprechend ablegen.
			MappingPersonenAttributeToColumn mappingColumn = null;
			for (String pattern : patterns) {
				if (mappingColumn == null) {
					mappingColumn = findMappingColumn(pattern, attribute);
				}
			}
			
			if (mappingColumn != null) {
				getColumnMappings().add(mappingColumn);
			}
		}
		
		return getColumnMappings();
	}
	
	/**
	 * Prüft ob der Import gültig ist. Er ist gültig wenn für jedes Personenattribut ein Spaltenmapping existiert.
	 * 
	 * @return True falls gültig, anderenfalls false.
	 */
	public boolean isValidImportData() {
		for (PersonenAttribute attribute : PersonenAttribute.values()) {
			boolean found = false;
			for (MappingPersonenAttributeToColumn mapping : columnMappings) {
				if (mapping.getPersonenAttribute().equals(attribute)) {
					found = true;
				}
			}
			if (!found) {
				log.warning("Für das Attribute " + attribute + " gibt es kein Mapping und darum ist der Import nicht valid.");
				return false;
			}
		}
		return true;
	}

	/**
	 * Import die Daten. Darf nur ausgeführt werden wenn isValidImportData() true ist.
	 */
	public void importData() {
		
		if (!isValidImportData()) {
			throw new IllegalStateException("Der Import ist nicht gültig.");
		}
		
		for (String[] dataLine = getFirstDataLine(); dataLine != null; dataLine = getNextDataLine()) {
				
			// Datensatz in Objekt kapseln
			DataRow dataRow = new DataRow(dataLine, columnMappings);
				
			// Person in Datenbank suchen. Dann erzeugen oder aktualisieren.
			// TODO Stimmt das wirklich bezüglich neuer ausweis?
			Person person = PersistenceUtil.findPerson(dataRow);
			if (person == null) {
				Einheit einheit = PersistenceUtil.createEinheitKompletterBestand(dataRow.getValue(PersonenAttribute.Einheit), isKompletterBestand());
				person = PersistenceUtil.createPerson(dataRow, einheit);
			} else {
				String logOld = person.toString();
				
				// Ermitteln ob es einen neuen Ausweis braucht
				boolean createNewAusweis = dataRow.wirdNeuerAusweisBenötigt(person);
				
				// Person aktualisieren
				person.setAhvNr(dataRow.getValue(PersonenAttribute.AHVNr));
				person.setName(dataRow.getValue(PersonenAttribute.Name));
				person.setVorname(dataRow.getValue(PersonenAttribute.Vorname));
				person.setFunktion(dataRow.getValue(PersonenAttribute.Funktion));
				person.setGrad(Grad.getGrad(dataRow.getValue(PersonenAttribute.Grad)));
				person.setGeburtsdatum(dataRow.getGeburtstag());
				Einheit einheit = PersistenceUtil.createEinheitKompletterBestand(dataRow.getValue(PersonenAttribute.Einheit), isKompletterBestand());
				person.setEinheit(einheit);
				
				// Neuen Ausweis erstellen
				if (createNewAusweis) {
					Ausweis oldAusweis = person.getValidAusweis();
					if (oldAusweis != null) {
						PersistenceUtil.deactivateAusweis(oldAusweis);
					}
					QueryHelper.createAusweis(person.getId());
					log.info("[" + logOld + "] to [" + person.toString() + "]");
				}
			}
			
			// Person und Einheit hinzufügen
			importedPersonen.add(person);
			importedEinheiten.add(person.getEinheit());
		}
		
		if (isKompletterBestand()) {
			PersistenceUtil.archiveOldPersonsAndDeactivateAusweis(importedPersonen, importedEinheiten);
		}
	}

	private MappingPersonenAttributeToColumn findMappingColumn(String pattern, PersonenAttribute attribute) {
		for (Column column : columns) {
			if (column.getName().toLowerCase().matches(pattern)) {
				return new MappingPersonenAttributeToColumn(attribute, column);
			}
		}
		return null;
	}

}
