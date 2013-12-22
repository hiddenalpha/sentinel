package ch.infbr5.sentinel.server.exporter;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.util.Zip4jConstants;
import ch.infbr5.sentinel.server.db.QueryHelper;
import ch.infbr5.sentinel.server.model.Person;
import ch.infbr5.sentinel.server.utils.FileHelper;

import com.thoughtworks.xstream.XStream;

public class AusweisDatenWriter {

	private static final String EXPORT_FILENAME = "pExpData.zip";
	private static final String ZIPED_XML_FILENAME = "personData.xml";

	public static byte[] export(String password) {

		try {
			
			FileHelper.removeFile(EXPORT_FILENAME);
			
			ZipFile zipFile = new ZipFile(new File(EXPORT_FILENAME));

			ZipParameters parameters = new ZipParameters();
			parameters.setCompressionMethod(Zip4jConstants.COMP_DEFLATE);
			parameters.setFileNameInZip(ZIPED_XML_FILENAME);
			parameters.setCompressionLevel(Zip4jConstants.DEFLATE_LEVEL_NORMAL);
			parameters.setEncryptFiles(true);
			parameters.setEncryptionMethod(Zip4jConstants.ENC_METHOD_STANDARD);
			parameters.setPassword(password);
			parameters.setSourceExternalStream(true);
			ByteArrayOutputStream bos = new ByteArrayOutputStream();

			List<Person> result = QueryHelper.getPersonen();
			List<Person> personen = new ArrayList<Person>();
			personen.addAll(result);

			XStream xstream = new XStream();
			xstream.alias("person", Person.class);
			xstream.alias("list", personen.getClass());

			Writer writer = new OutputStreamWriter(bos, "UTF-8");
			xstream.toXML(personen, writer);
			bos.close();

			zipFile.addStream(new ByteArrayInputStream(bos.toByteArray()), parameters);
			
			parameters.setSourceExternalStream(false);
			zipFile.addFolder("images", parameters);
						
			return FileHelper.getAsByteArray(EXPORT_FILENAME);

		} catch (ZipException | IOException e) {
			e.printStackTrace();
		}
		
		return null;
	}

}
