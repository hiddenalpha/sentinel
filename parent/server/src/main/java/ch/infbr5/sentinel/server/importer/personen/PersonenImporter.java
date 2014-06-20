package ch.infbr5.sentinel.server.importer.personen;

import java.util.List;

import ch.infbr5.sentinel.server.utils.FileHelper;
import ch.infbr5.sentinel.server.ws.importer.mapping.PersonenImportColumn;
import ch.infbr5.sentinel.server.ws.importer.mapping.PersonenImportColumnMapping;
import ch.infbr5.sentinel.server.ws.importer.modification.ModificationDto;

public class PersonenImporter {

	public static final String[] SUPPORTED_EXTENSIONS = {"csv", "xls", "xlsx"};
	
	private PersonenDataImporter importer;

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
	
	public String fileHasMinimalRequirements() {
		return importer.fileHasMinimalRequirements();
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
	public List<PersonenImportColumnMapping> calculateColumnMappings() {
		return importer.calculateColumnMappings();
	}
	
	public List<PersonenImportColumnMapping> getColumnMappings() {
		return importer.getColumnMappings();
	}
	
	public void setColumnMappings(List<PersonenImportColumnMapping> columnMappings) {
		importer.setColumnMappings(columnMappings);
	}
	
	/**
	 * Gibt die komplette Liste aller Spalten zurück.
	 * @return Liste aller Spalten.
	 */
	public List<PersonenImportColumn> getColumns() {
		return importer.getColumns();
	}
	
	public boolean isKompletterBestand() {
		return importer.isKompletterBestand();
	}
	
	public String getFilenameData() {
		return importer.getFilenameData();
	}

	public ModificationDto calculateModifications() {
		return importer.calculateModifications();
	}
	
	public ModificationDto getModifications() {
		return importer.getModifications();
	}

	public void setModifications(ModificationDto dto) {
		importer.setModifications(dto);
	}
	
}
