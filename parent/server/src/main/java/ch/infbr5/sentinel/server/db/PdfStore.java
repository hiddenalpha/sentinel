package ch.infbr5.sentinel.server.db;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class PdfStore {

	public static byte[] loadPdf(String name) {
		// create file object
		File file = new File(PdfStore.createFilename(name));

		try {
			// create FileInputStream object
			FileInputStream fin = new FileInputStream(file);

			/*
			 * Create byte array large enough to hold the content of the file.
			 * Use File.length to determine size of the file in bytes.
			 */

			byte fileContent[] = new byte[(int) file.length()];

			/*
			 * To read content of the file in byte array, use int read(byte[]
			 * byteArray) method of java FileInputStream class.
			 */
			fin.read(fileContent);
			fin.close();
			
			return fileContent;

		} catch (FileNotFoundException e) {
			System.out.println("File not found" + e);
		} catch (IOException ioe) {
			System.out.println("Exception while reading the file " + ioe);
		}

		return null;
	}

	public static boolean savaPdfFile(String name, byte[] data) {
		try {
			if ((data != null) && (data.length > 0)) {

				String filename = createFilename(name);
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
		File directoryHelper = new File(parentDirectory);
		boolean directoryExists = directoryHelper.exists();
		if (!directoryExists) {
			directoryHelper.mkdirs();
		}
	}

	private static String createFilename(String filename) {
		String folder = "." + File.separator + "pdfs" + File.separator; // TODO
		return folder + filename + ".pdf";
	}

}
