package ch.infbr5.sentinel.server.importer;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;

import org.apache.derby.iapi.services.io.FileUtil;
import org.jfree.util.Log;

import com.google.common.io.Files;
import com.thoughtworks.xstream.XStream;

public abstract class ZipDatenReader {

	private String tmpId;

	private byte[] data;

	private String password;

	private boolean error;

	private ZipFile zipFile;

	protected ZipDatenReader(byte[] data, String password) {
		this.data = data;
		this.password = password;
		this.error = false;
		tmpId = UUID.randomUUID().toString();
	}

	public boolean hasError() {
		return error;
	}

	protected void initiate() {
		saveTempFile();
		zipFile = createZipFile();
		try {
			zipFile.extractAll(getTempDirectory());
		} catch (ZipException e) {
			throw new IllegalStateException("Konnte nicht alle Dateien extrahieren " + e.getMessage());
		}
	}

	protected Object getXStreamObject(String file) {
		return getXStreamObject(file, new XStream());
	}

	protected Object getXStreamObject(String file, XStream xStream) {
		Object xStreamObject = xStream.fromXML(new File(getTempDirectory() + file));
		return xStreamObject;
	}

	protected void close() {
		removeTempFile();
	}

	private String getTempDirectory() {
		return getTmpFolder() + tmpId + "/";
	}

	private String getTmpFolder() {
		return "tmp/";
	}

	private File getTempFile() {
		return new File(getTempDirectory() + "zipdatenreader.zip");
	}

	private void saveTempFile() {
		try {
			File f = new File(getTmpFolder());
			if (!f.exists()) {
				f.mkdir();
			}
			f = new File(getTempDirectory());
			if (!f.exists()) {
				f.mkdir();
			}
			Files.write(data, getTempFile());
		} catch (IOException e) {
			throw new IllegalStateException("Temp Zip konnte nicht erstellt werden." + e.getMessage());
		}
	}

	protected byte[] toByteArray(String filepath) {
		try {
			File f = new File(getTempDirectory() + filepath);
			if (f.exists()) {
				return Files.toByteArray(f);
			} else {
				return null;
			}
		} catch (IOException e) {
			Log.error(e);
			return null;
		}
	}

	protected void copyFiles(String source, String pathDestination) {
		File f = new File(getTempDirectory() + source);
		if (f.exists()) {
			for (File k : f.listFiles()){
				if (k.isFile()) {
					try {
						Files.copy(k, new File(pathDestination + File.separator + k.getName()));
					} catch (IOException e) {
						Log.error(e);
						throw new IllegalStateException("Konnte Datei nicht kopierenn " + e.getMessage());
					}
				}
			}
		}
	}

	protected void copyFile(String source, String pathDestination) {
		File f = new File(getTempDirectory() + source);
		if (f.exists()) {
			try {
				Files.copy(f, new File(pathDestination));
			} catch (IOException e) {
				Log.error(e);
				throw new IllegalStateException("Konnte Datei nicht kopierenn " + e.getMessage());
			}
		}
	}

	private ZipFile createZipFile() {
		ZipFile zipFile;
		try {
			zipFile = new ZipFile(getTempFile());
			if (zipFile.isEncrypted()) {
				zipFile.setPassword(password);
			}
		} catch (ZipException e) {
			throw new IllegalStateException("Konnte kein Zip File Objekt erstellen " + e.getMessage());
		}

		return zipFile;
	}

	private void removeTempFile() {
		FileUtil.removeDirectory(new File(getTempDirectory()));
	}

}
