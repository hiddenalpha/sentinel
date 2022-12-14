package ch.infbr5.sentinel.server.importer.personen;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.persistence.EntityManager;

import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

class PersonenDataExcelImporter extends PersonenDataImporter {

	private FileInputStream file;

	private Sheet sheet;

	private int currentRow = 1;

	private int firstColumnIndex = 0;

	private int lastColumnIndex;

	public PersonenDataExcelImporter(EntityManager em, String filenameData, boolean isKompletteEinheit) {
		super(em, filenameData, isKompletteEinheit);
	}

	@Override
	public String[] getHeaderLine() {
		Sheet sheet = getSheet();
		String[] headerLine = createArrayHeader(sheet.getRow(0));
		if (headerLine != null) {
			lastColumnIndex = headerLine.length - 1;
		}
		close();
		return headerLine;
	}

	private String[] createArrayHeader(Row row) {
		// Celliterator überspringt empty cells!!! Darum zwei Implementationen! Aber die Erste Zeil muss einfach bündig und komplett sein.
		List<String> headers = new ArrayList<>();
		if (row == null) {
			return null;
		}
		Iterator<Cell> cellIterator = row.cellIterator();
		while (cellIterator.hasNext()) {
			Cell cell = cellIterator.next();
			if (cell.getCellTypeEnum() == CellType.NUMERIC) {
				if (HSSFDateUtil.isCellDateFormatted(cell)) {
					Date date = cell.getDateCellValue();
					headers.add(new SimpleDateFormat("dd.MM.yyyy").format(date));
				} else {
					cell.setCellType(Cell.CELL_TYPE_STRING);
					headers.add(cell.getStringCellValue());
				}
			} else {
				cell.setCellType(Cell.CELL_TYPE_STRING);
				headers.add(cell.getStringCellValue());
			}
		}

		return headers.toArray(new String[headers.size()]);
	}

	private String[] createArray(Row row) {
		List<String> headers = new ArrayList<>();
		if (row == null) {
			return null;
		}

		for (int i = firstColumnIndex; i < lastColumnIndex; i++) {
			Cell cell = row.getCell(i, Row.RETURN_BLANK_AS_NULL);
			if (cell == null) {
				headers.add("");
			} else {
				if (cell.getCellType() == Cell.CELL_TYPE_NUMERIC) {
					if (HSSFDateUtil.isCellDateFormatted(cell)) {
						Date date = cell.getDateCellValue();
						headers.add(new SimpleDateFormat("dd.MM.yyyy").format(date));
					} else {
						cell.setCellType(Cell.CELL_TYPE_STRING);
						headers.add(cell.getStringCellValue());
					}
				} else {
					cell.setCellType(Cell.CELL_TYPE_STRING);
					headers.add(cell.getStringCellValue());
				}
			}

		}

		return headers.toArray(new String[headers.size()]);
	}

	@Override
	String[] getFirstDataLine() {
		sheet = getSheet();
		currentRow = 1;
		String[] line = createArray(sheet.getRow(currentRow));
		return line;
	}

	@Override
	int getCountDataLines() {
		Sheet sheet = getSheet();
		int rowNum = sheet.getLastRowNum();
		close();
		return rowNum;
	}

	@Override
	String[] getNextDataLine() {
		currentRow++;
		if (sheet.getLastRowNum() < currentRow) {
			close();
			return null;
		} else {
			return createArray(sheet.getRow(currentRow));
		}
	}

	@Override
	void forceClose() {
		close();
	}

	private Sheet getSheet() {
		try {
			file = new FileInputStream(new File(getFilenameData()));
			Workbook workbook = WorkbookFactory.create(file);
			return workbook.getSheetAt(0);
		} catch (IOException | InvalidFormatException e) {
			e.printStackTrace();
			throw new RuntimeException(e.getMessage());
		}
	}

	private void close() {
		if (file != null) {
			try {
				file.close();
			} catch (IOException e) {
				e.printStackTrace(); // doesn't matter
			}
		}
	}

}
