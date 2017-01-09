package ch.infbr5.sentinel.client.gui.util;

import java.awt.Desktop;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.swing.JOptionPane;

public class DesktopHelper {

	public static void openPdfFile(String filename, byte[] data) {

		if (data == null) {
			JOptionPane.showMessageDialog(null,
					"Datei nicht vorhanden (leer).",
					"Datei kann nicht geöffnet werden.", JOptionPane.WARNING_MESSAGE);
		} else {
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

}
