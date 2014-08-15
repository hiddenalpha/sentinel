package ch.infbr5.sentinel.server.importer;

import java.io.File;
import java.util.List;

import ch.infbr5.sentinel.server.model.Person;
import ch.infbr5.sentinel.server.utils.FileHelper;

import com.thoughtworks.xstream.XStream;

public class AusweisDatenReader extends ZipDatenReader {

	private static final String XML_FILENAME = "personData.xml";

	public AusweisDatenReader(byte[] data, String password) {
		super(data, password);
	}

	public List<Person> readData() {
		initiate();

		XStream xStream = new XStream();
		xStream.alias("person", Person.class);
		Object xStreamObject = getXStreamObject(XML_FILENAME, xStream);

		List<Person> values  = (List<Person>) xStreamObject;

		close();
		return values;
	}

	public void importBilder() {
		initiate();
		FileHelper.removeFolderContent(new File("images"));
		copyFiles("images", "images");
		copyFile(FileHelper.FILE_WASSERZEICHEN_PNG, FileHelper.FILE_WASSERZEICHEN_PNG);
		copyFile(FileHelper.FILE_AUSWEISVORLAGE_JPG, FileHelper.FILE_AUSWEISVORLAGE_JPG);
		close();
	}

	public byte[] readAusweisVorlage() {
		initiate();
		byte[] data = toByteArray(FileHelper.FILE_AUSWEISVORLAGE_JPG);
		close();
		return data;
	}

	public byte[] readWasserzeichen() {
		initiate();
		byte[] data = toByteArray(FileHelper.FILE_WASSERZEICHEN_PNG);
		close();
		return data;
	}
}
