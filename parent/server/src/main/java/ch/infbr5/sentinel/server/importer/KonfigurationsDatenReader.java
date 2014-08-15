package ch.infbr5.sentinel.server.importer;

import java.util.List;

import ch.infbr5.sentinel.server.exporter.KonfigurationsDatenWriter;
import ch.infbr5.sentinel.server.model.ConfigurationValue;

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


}
