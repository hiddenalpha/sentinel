package ch.infbr5.sentinel.client.util;

import java.awt.Desktop;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class DesktopHelper {

	public static void openPdfFile(String filename, byte[] data) {

		try {

			// Create temp file.
			File temp = File.createTempFile("sentinel_", ".pdf");

			// Delete temp file when program exits.
			temp.deleteOnExit();

			// Write to temp file

			FileOutputStream fos = new FileOutputStream(temp);
			fos.write(data);
			fos.close();

			if (Desktop.isDesktopSupported()) {
				Desktop.getDesktop().open(temp);
			} else {
				// Open Document Viewer in Lubuntu
				Runtime.getRuntime().exec("evince " + temp.getAbsolutePath());
			}

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}
