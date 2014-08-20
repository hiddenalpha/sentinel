package ch.infbr5.sentinel.server.importer;

import java.io.File;
import java.util.List;

import ch.infbr5.sentinel.server.exporter.KonfigurationsDatenWriter;
import ch.infbr5.sentinel.server.model.ConfigurationValue;
import ch.infbr5.sentinel.server.utils.FileHelper;

public class KonfigurationsDatenReader extends ZipDatenReader {

	public KonfigurationsDatenReader(byte[] data, String password) {
		super(data, password);
	}

	public List<ConfigurationValue> readData() {
		initiate();
		Object xStreamObject = getXStreamObject(KonfigurationsDatenWriter.EXPORT_FILENAME_XML);
		List<ConfigurationValue> values  = (List<ConfigurationValue>) xStreamObject;
		close();
		return values;
	}

	public void importVorlagen() {
		initiate();
		FileHelper.removeFolderContent(new File("images"));
		copyFile(FileHelper.FILE_WASSERZEICHEN_PNG, FileHelper.FILE_WASSERZEICHEN_PNG);
		copyFile(FileHelper.FILE_AUSWEISVORLAGE_JPG, FileHelper.FILE_AUSWEISVORLAGE_JPG);
		close();
	}


}
