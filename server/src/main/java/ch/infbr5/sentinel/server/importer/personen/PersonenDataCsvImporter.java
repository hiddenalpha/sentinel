package ch.infbr5.sentinel.server.importer.personen;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import javax.persistence.EntityManager;

import au.com.bytecode.opencsv.CSVReader;

class PersonenDataCsvImporter extends PersonenDataImporter {

	private CSVReader reader;

	public PersonenDataCsvImporter(EntityManager em, String filenameData, boolean isKompletteEinheit) {
		super(em, filenameData, isKompletteEinheit);
	}

	@Override
	public String[] getHeaderLine() {
		String[] headerline = { };
		try {
			openReader();
			headerline = reader.readNext();
			closeReader();
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException(e.getMessage());
		}
		return headerline;
	}

	@Override
	public String[] getFirstDataLine() {
		try {
			openReader();
			reader.readNext(); // Header Line
			return reader.readNext();
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException(e.getMessage());
		}
	}

	@Override
	public String[] getNextDataLine() {
		try {
			String[] dataLine = reader.readNext();
			if (dataLine == null) {
				closeReader();
			}
			return dataLine;
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException(e.getMessage());
		}
	}

	private void openReader() throws UnsupportedEncodingException, FileNotFoundException {
		reader = new CSVReader(new InputStreamReader(new FileInputStream(
				getFilenameData()), "ISO-8859-1"), ';', '"');
	}

	private void closeReader() throws IOException {
		if (reader != null) {
			reader.close();
		}
	}

	@Override
	public int getCountDataLines() {
		int size = 0;
		try {
			openReader();
			size = reader.readAll().size() - 1;
			closeReader();
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException(e.getMessage());
		}

		return size;
	}

	@Override
	void forceClose() {
		try {
			closeReader();
		} catch (IOException e) {
			e.printStackTrace(); // doesnt matter
		}
	}

}
