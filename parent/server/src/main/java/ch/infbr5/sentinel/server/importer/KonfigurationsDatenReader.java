package ch.infbr5.sentinel.server.importer;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;

import org.apache.log4j.Logger;

import ch.infbr5.sentinel.server.exporter.KonfigurationsDatenWriter;
import ch.infbr5.sentinel.server.model.ConfigurationValue;
import ch.infbr5.sentinel.server.utils.FileHelper;

import com.google.common.collect.Lists;
import com.thoughtworks.xstream.XStream;

public class KonfigurationsDatenReader {

	private static final String IMPORT_FILENAME = "sentinel-configuraiton-import.zip";

	private static final Logger log = Logger.getLogger(KonfigurationsDatenReader.class);

	private byte[] data;

	private String password;

	private boolean error;

	public KonfigurationsDatenReader(byte[] data, String password) {
		this.data = data;
		this.password = password;
		this.error = false;
	}

	public List<ConfigurationValue> importData() {
		FileHelper.removeFile(IMPORT_FILENAME);
		FileHelper.saveAsFile(IMPORT_FILENAME, data);

		BufferedReader in = null;
		try {
			ZipFile zipFile = new ZipFile(IMPORT_FILENAME);

			if (zipFile.isEncrypted()) {
				zipFile.setPassword(password);
			}
			zipFile.extractAll(".");

			XStream xstream = new XStream();
			Path path = FileSystems.getDefault().getPath(KonfigurationsDatenWriter.EXPORT_FILENAME_XML);
			in = Files.newBufferedReader(path, StandardCharsets.UTF_8);
			return  (List<ConfigurationValue>) xstream.fromXML(in);
		} catch (IOException | ZipException e) {
			log.error(e);
			error = true;
		} finally {
			try {
				in.close();
			} catch (IOException e) {
				log.debug(e);
			}
		}

		return Lists.newArrayList();
	}

	public boolean hasError() {
		return error;
	}

}
