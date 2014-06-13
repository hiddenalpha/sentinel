package ch.infbr5.sentinel.server.importer.personen;

import java.util.List;

import ch.infbr5.sentinel.server.utils.FileHelper;
import ch.infbr5.sentinel.server.ws.importer.Column;
import ch.infbr5.sentinel.server.ws.importer.MappingPersonenAttributeToColumn;


public class PersonenImporter {

	private PersonenDataImporter importer;

	// TODO Die unterstützen Dateitypen und das Mapping auf die konkreten Klassen könnte man optimieren
	// Die unterstütztzen Dateitypen werden auch auf Client Side im FileChoser benötigt.
	public PersonenImporter(String filenameData, boolean isKompletterBestand) {
		String extension = FileHelper.getExtension(filenameData);
		if ("csv".equals(extension)) {
			importer = new PersonenDataCsvImporter(filenameData, isKompletterBestand);
		} else if ("xls".equals(extension) || "xlsx".equals(extension)) {
			importer = new PersonenDataExcelImporter(filenameData, isKompletterBestand);
		} else {
			throw new IllegalArgumentException("Datei Extension wird nicht unterstützt.");
		}
	}

	/**
	 * Prüft ob der Import gültig ist.
	 * 
	 * @return True, falls der Import gültig ist, anderenfalls false.
	 */
	public boolean isValidImportData() {
		return importer.isValidImportData();
	}

	/**
	 * Importiert die Daten ins System. Jedoch nur wenn der Import gültig ist.
	 */
	public void importData() {
		if (isValidImportData()) {
			importer.importData();
		}
	}
	
	/**
	 * Versucht ein mögliches Mapping herzustellen. Falls noch kein Mapping vorliegt wird so inital eins gesetzt.
	 * Liegt bereits ein Mapping vor, dann wird versucht dieses zu optimieren.
	 * 
	 * @return Liefert das neue Mapping Konstrukt zurück.
	 */
	public List<MappingPersonenAttributeToColumn> calculateColumnMappings() {
		return importer.calculateColumnMappings();
	}
	
	/**
	 * Gibt die komplette Liste aller Spalten zurück.
	 * @return Liste aller Spalten.
	 */
	public List<Column> getColumns() {
		return importer.getColumns();
	}
	
	public void setMappings(List<MappingPersonenAttributeToColumn> columnMappings) {
		importer.setColumnMappings(columnMappings);
	}
	
}
