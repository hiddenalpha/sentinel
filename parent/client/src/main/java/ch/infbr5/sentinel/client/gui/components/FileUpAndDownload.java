package ch.infbr5.sentinel.client.gui.components;

import java.awt.FileDialog;
import java.awt.Frame;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;

import ch.infbr5.sentinel.client.gui.components.importer.PersonenImportMappingDialog;
import ch.infbr5.sentinel.client.util.ServiceHelper;
import ch.infbr5.sentinel.client.wsgen.ColumnMappingResponse;

public class FileUpAndDownload {

	private Frame frame;

	public FileUpAndDownload(Frame parent) {
		frame = parent;
	}

	public void exportPersonData() {
		String filename = showFileDialog(frame, "Ausweisdaten exportieren",
				"\\.", "*.zip", FileDialog.SAVE);

		if (filename != null) {

			String password = promptPassword("Passwort setzen...");
			byte[] data = ServiceHelper.getConfigurationsService()
					.exportPersonData(password);
			if (saveFile(filename, data)) {
				JOptionPane.showMessageDialog(null,
						"Die Datei wurde gespeichert.", "Test Titel",
						JOptionPane.OK_OPTION);
			} else {
				JOptionPane
						.showMessageDialog(
								null,
								"Die Datei konnte nicht erfolgreich gespeichert werden.",
								"Ausweisdaten exportieren",
								JOptionPane.CANCEL_OPTION);
			}
		}

	}

	public void importPersonData() {
		String filename = showFileDialog(frame, "Ausweisdaten importieren",
				"\\.", "*.zip", FileDialog.LOAD);

		if (filename != null) {

			String password = promptPassword("Passwort setzen...");
			byte[] data = loadFile(filename);

			boolean result = ServiceHelper.getConfigurationsService()
					.importPersonData(data, password);
			if (result) {
				JOptionPane.showMessageDialog(null,
						"Die Datei wurde gespeichert.",
						"Ausweisdaten importieren", JOptionPane.OK_OPTION);
			} else {
				JOptionPane
						.showMessageDialog(
								null,
								"Die Datei konnte nicht erfolgreich gespeichert werden.",
								"Import", JOptionPane.CANCEL_OPTION);
			}
		}

	}

	public void importPisaData(boolean isKompletterBestand) {
		String filename = showFileDialog(frame, "Pisadaten importieren", "\\.",
				"*.csv", FileDialog.LOAD);

		if (filename != null) {

			byte[] data = loadFile(filename);

			String sessionKey = ServiceHelper.getPersonenImporterService().initiatImport((new File(filename)).getName(), data, isKompletterBestand);
			ColumnMappingResponse response = ServiceHelper.getPersonenImporterService().getColumnMappings(sessionKey);

			PersonenImportMappingDialog dialog = new PersonenImportMappingDialog(frame, sessionKey, response);
			dialog.show();
		}

	}

	public void importAusweisvorlage() {
		String filename = showFileDialog(frame,
				"Ausweisvorlage (1000 x 743 px) importieren", "\\.", "*.jpg",
				FileDialog.LOAD);

		if (filename != "") {
			File imageFile = new File(filename);

			BufferedImage image;
			try {
				image = ImageIO.read(imageFile);
			} catch (IOException e) {
				image = null;
			}

			if (image != null) {

				if ((image.getWidth() == 1000) && (image.getHeight() == 743)) {

					byte[] data = loadFile(filename);
					boolean result = ServiceHelper.getConfigurationsService()
					.importAusweisVorlage(data);
					
					if (result) {
						JOptionPane.showMessageDialog(null,
								"Die Auseisvorlage wurde gespeichert.",
								"Ausweisvorlage importieren", JOptionPane.OK_OPTION);
					} else {
						JOptionPane
								.showMessageDialog(
										null,
										"Die Auseisvorlage konnte nicht gespeichert werden.",
										"Ausweisvorlage importieren",
										JOptionPane.CANCEL_OPTION);
					}

				} else {
					JOptionPane
							.showMessageDialog(
									null,
									"Das Bild hat nicht die geforderte Grösse von 1000 x 743 Pixel!",
									"Ausweisvorlage importieren",
									JOptionPane.CANCEL_OPTION);
				}
			} else {
				JOptionPane
						.showMessageDialog(null,
								"Bild konnte nicht dekodiert werden!",
								"Ausweisvorlage importieren",
								JOptionPane.CANCEL_OPTION);

			}
		}

	}
	
	public void importWasserzeichen() {
		String filename = showFileDialog(frame,
				"Ausweisvorlage (116 x dd 125) importieren", "\\.", "*.png",
				FileDialog.LOAD);

		if (filename != "") {
			File imageFile = new File(filename);

			BufferedImage image;
			try {
				image = ImageIO.read(imageFile);
			} catch (IOException e) {
				image = null;
			}

			if (image != null) {

				if ((image.getWidth() == 116) && (image.getHeight() == 125)) {

					byte[] data = loadFile(filename);
					boolean result = ServiceHelper.getConfigurationsService()
					.importWasserzeichen(data);
					
					if (result) {
						JOptionPane.showMessageDialog(null,
								"Das Wasserzeichen wurde gespeichert.",
								"Wasserzeichen importieren", JOptionPane.OK_OPTION);
					} else {
						JOptionPane
								.showMessageDialog(
										null,
										"Das Wasserzeichen konnte nicht gespeichert werden.",
										"Wasserzeichen importieren",
										JOptionPane.CANCEL_OPTION);
					}

				} else {
					JOptionPane
							.showMessageDialog(
									null,
									"Das Wasserzeichen hat nicht die geforderte Grösse von 116 x 125 Pixel!",
									"Wasserzeichen importieren",
									JOptionPane.CANCEL_OPTION);
				}
			} else {
				JOptionPane
						.showMessageDialog(null,
								"Bild konnte nicht dekodiert werden!",
								"Wasserzeichen importieren",
								JOptionPane.CANCEL_OPTION);

			}
		}

	}

	private String promptPassword(String title) {
		JPasswordField passwordField = new JPasswordField(10);
		passwordField.setEchoChar('#');
		JOptionPane.showMessageDialog(frame, passwordField, title,
				JOptionPane.OK_OPTION);
		return new String(passwordField.getPassword());

	}

	private byte[] loadFile(String filename) {
		try {
			File file = new File(filename);
			FileInputStream fin = new FileInputStream(file);
			byte fileContent[] = new byte[(int) file.length()];

			fin.read(fileContent);
			fin.close();
			return fileContent;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;
	}

	private boolean saveFile(String filename, byte[] data) {
		try {
			if ((data != null) && (data.length > 0)) {
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

	public String showFileDialog(Frame f, String title, String defDir,
			String fileType, int dlgType) {
		FileDialog fd = new FileDialog(f, title, dlgType);
		fd.setFile(fileType);
		fd.setDirectory(defDir);
		fd.setLocation(50, 50);
		fd.setVisible(true);

		if ((fd.getDirectory() != null) && (fd.getFile() != null)) {
			return fd.getDirectory().concat(fd.getFile());
		} else {
			return null;
		}
	}

}
