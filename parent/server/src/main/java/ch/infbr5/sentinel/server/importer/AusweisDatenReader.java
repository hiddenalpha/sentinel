package ch.infbr5.sentinel.server.importer;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import javax.persistence.EntityManager;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import ch.infbr5.sentinel.server.db.QueryHelper;
import ch.infbr5.sentinel.server.model.Person;
import ch.infbr5.sentinel.server.utils.FileHelper;

import com.thoughtworks.xstream.XStream;

public class AusweisDatenReader {

	private static final String IMPORT_FILENAME = "pImpData.zip";
	private static final String XML_FILENAME = "personData.xml";
	private String password;

	private EntityManager em;

	public AusweisDatenReader(byte[] data, String password, EntityManager entityManager) {
		FileHelper.removeFile(XML_FILENAME);
		FileHelper.saveAsFile(IMPORT_FILENAME,data);
		this.password = password;
		this.em = entityManager;
	}

	public boolean isValidPassword(){
		try {
			ZipFile zipFile = new ZipFile(IMPORT_FILENAME);
			if (zipFile.isEncrypted()) {
				zipFile.setPassword(password);
			}
			zipFile.extractFile(XML_FILENAME,".");
		} catch (ZipException e) {
			return false;
		}

		return new File(XML_FILENAME).exists();

	}

	public void read() {

		new QueryHelper(em).removeAllPersonData();
		FileHelper.removeFolderContent(new File("images"));

		try {
			ZipFile zipFile = new ZipFile(IMPORT_FILENAME);

			if (zipFile.isEncrypted()) {
				zipFile.setPassword(password);
			}
			zipFile.extractAll(".");

			XStream xstream = new XStream();
			xstream.alias("person", Person.class);

			Path path = FileSystems.getDefault().getPath(XML_FILENAME);
			BufferedReader in = Files.newBufferedReader(path, StandardCharsets.UTF_8);
			List<Person> personen = (List<Person>) xstream.fromXML(in);
			in.close();

			new QueryHelper(em).persistAllPersonData(personen);

		} catch (IOException | ZipException e) {
			e.printStackTrace();
		}
	}



}
