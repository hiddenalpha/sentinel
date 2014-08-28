package ch.infbr5.sentinel.server.exporter;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.List;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.ZipParameters;

import org.apache.log4j.Logger;

import ch.infbr5.sentinel.server.model.ConfigurationValue;
import ch.infbr5.sentinel.server.utils.FileHelper;

import com.google.common.collect.Lists;
import com.google.common.io.Files;
import com.thoughtworks.xstream.XStream;

public class KonfigurationsDatenWriter {

	public static final String EXPORT_FILENAME_XML = "sentinel-configuration.xml";

	public static final String EXPORT_FILENAME_ZIP = "sentinel-configuration-export.zip";

	private static final Logger log = Logger.getLogger(KonfigurationsDatenWriter.class);

	public static byte[] export(String password, List<ConfigurationValue> values) {
		// Zip File
		File fileZip = new File(EXPORT_FILENAME_ZIP);

		try {
			// Lösche eventuell bestehendes File
			FileHelper.removeFile(fileZip);

			// Objekte mittels XStream in ByteStream schreiben
			XStream xStream = new XStream();
			ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
			xStream.toXML(Lists.newArrayList(values), new OutputStreamWriter(byteStream, "UTF-8"));
			byteStream.close();

			// Zip Datei erstellen
			ZipFile zipFile = new ZipFile(fileZip);
			ZipParameters parameters = ZipUtil.createZipParameters(password, EXPORT_FILENAME_XML);
			zipFile.addStream(new ByteArrayInputStream(byteStream.toByteArray()), parameters);

			// Ausweisvorlage speichern
			parameters.setSourceExternalStream(false);
			File f = new File(FileHelper.FILE_AUSWEISVORLAGE_LOGO);
			if (f.exists()) {
				zipFile.addFile(f, parameters);
			}

			// Wasserzeichnen speichern
			f = new File(FileHelper.FILE_AUSWEISVORLAGE_WASSERZEICHEN);
			if (f.exists()) {
				zipFile.addFile(f, parameters);
			}

			return Files.toByteArray(fileZip);
		} catch (ZipException | IOException e) {
			log.error(e);
		}
		return null;
	}

}
