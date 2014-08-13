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

import ch.infbr5.sentinel.server.model.Person;
import ch.infbr5.sentinel.server.utils.FileHelper;

import com.google.common.collect.Lists;
import com.thoughtworks.xstream.XStream;

public class AusweisDatenWriter {

	private static final String EXPORT_FILENAME = "pExpData.zip";

	private static final String ZIPED_XML_FILENAME = "personData.xml";

	private static final Logger log = Logger.getLogger(AusweisDatenWriter.class);

	public static byte[] export(String password, List<Person> result) {

		try {
			// Eventuell Datei löschen
			FileHelper.removeFile(EXPORT_FILENAME);

			// XStream vorbereiten
			List<Person> personen = Lists.newArrayList(result);

			XStream xstream = new XStream();
			xstream.alias("person", Person.class);
			xstream.alias("list", personen.getClass());

			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			xstream.toXML(personen, new OutputStreamWriter(bos, "UTF-8"));
			bos.close();

			// Zip Datei erstellen
			ZipFile zipFile = new ZipFile(new File(EXPORT_FILENAME));
			ZipParameters parameters = ZipUtil.createZipParameters(password, ZIPED_XML_FILENAME);
			zipFile.addStream(new ByteArrayInputStream(bos.toByteArray()), parameters);

			// Weitere Dateien hinzfufügen
			parameters.setSourceExternalStream(false);
			zipFile.addFolder("images", parameters);

			// Ausweisvorlage speichern
			File f = new File(FileHelper.FILE_AUSWEISVORLAGE_JPG);
			if (f.exists()){
				zipFile.addFile(f, parameters);
			}

			// Wasserzeichnen speichern
			f = new File(FileHelper.FILE_WASSERZEICHEN_PNG);
			if (f.exists()){
				zipFile.addFile(f, parameters);
			}

			return FileHelper.getAsByteArray(EXPORT_FILENAME);
		} catch (ZipException | IOException e) {
			log.error(e);
		}

		return null;
	}

}
