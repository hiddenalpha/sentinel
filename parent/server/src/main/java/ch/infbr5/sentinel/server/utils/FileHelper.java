package ch.infbr5.sentinel.server.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class FileHelper {

	public static byte[] getAsByteArray(String filename) throws IOException {
		File file = new File(filename);
		FileInputStream fin = new FileInputStream(file);

		byte fileContent[] = new byte[(int) file.length()];
		fin.read(fileContent);
		fin.close();

		return fileContent;
	}

	public static void removeFile(String filename) {
		File file = new File(filename);
		if (file.exists()) {
			file.delete();
		}
	}

	public static void removeFolderContent(File f) {
		if (f.isDirectory()) {
			for (File c : f.listFiles()) {
				c.delete();
			}
		}
	}

	public static boolean saveAsFile(String filename, byte[] data) {
		try {
			if ((data != null) && (data.length > 0)) {
				createParentDirectoryIfRequired(filename);

				FileOutputStream fos = new FileOutputStream(filename);
				fos.write(data);
				fos.close();
			}
			return true;

		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	private static void createParentDirectoryIfRequired(String filename) {
		File fileHelper = new File(filename);
		String parentDirectory = fileHelper.getParent();
		if (parentDirectory != null) {
			File directoryHelper = new File(parentDirectory);
			boolean directoryExists = directoryHelper.exists();
			if (!directoryExists) {
				directoryHelper.mkdirs();
			}
		}
	}

}
