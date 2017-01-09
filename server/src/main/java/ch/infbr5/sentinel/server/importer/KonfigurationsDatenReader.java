package ch.infbr5.sentinel.server.importer;

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
		copyFile(FileHelper.FILE_AUSWEISVORLAGE_WASSERZEICHEN, FileHelper.FILE_AUSWEISVORLAGE_WASSERZEICHEN);
		copyFile(FileHelper.FILE_AUSWEISVORLAGE_LOGO, FileHelper.FILE_AUSWEISVORLAGE_LOGO);
		close();
	}

	public byte[] getWasserzeichen() {
		initiate();
		byte[] data = toByteArray(FileHelper.FILE_AUSWEISVORLAGE_WASSERZEICHEN);
		close();
		return data;
	}

	public byte[] getLogo() {
		initiate();
		byte[] data = toByteArray(FileHelper.FILE_AUSWEISVORLAGE_LOGO);
		close();
		return data;
	}


}
