package ch.infbr5.sentinel.client.gui.components;

import java.awt.FileDialog;
import java.awt.Frame;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.filechooser.FileNameExtensionFilter;

import ch.infbr5.sentinel.client.config.ConfigurationLocalHelper;
import ch.infbr5.sentinel.client.util.ServiceHelper;
import ch.infbr5.sentinel.common.gui.util.RequestFocusListener;

public class FileUpAndDownload {

	private Frame frame;

	public FileUpAndDownload(Frame parent) {
		frame = parent;
	}

	public String showImportConfigurationFileDialog() {
		return showFileDialog(frame, "Konfiguration importieren", "\\.", "*.zip", FileDialog.LOAD);
	}

	public void exportConfiguration() {
		String filename = showFileDialog(frame, "Konfiguration exportieren", "\\.", "*.zip", FileDialog.SAVE);
		if (filename != null) {
			String password = promptPassword("Passwort setzen");
			if (!isEmpty(password)) {
				saveFileTo(filename, ServiceHelper.getConfigurationsService().exportConfigData(password));
			}
		}
	}

	public void importPersonData() {
		showHinweis("Hinweis: Bestehende Daten werden gelöscht (Personendaten, Ausweisdaten, Personenbilder).");
		String filename = showFileDialog(frame, "Ausweisdaten importieren", "\\.", "*.zip", FileDialog.LOAD);
		if (filename != null) {
			String password = promptPassword("Passwort eingeben");
			if (!isEmpty(password)) {
				boolean result = ServiceHelper.getConfigurationsService().importPersonData(loadFile(filename), password);
				importSuccess(result);
			}
		}
	}

	public void exportPersonData() {
		String filename = showFileDialog(frame, "Ausweisdaten exportieren", "\\.", "*.zip", FileDialog.SAVE);
		if (filename != null) {
			String password = promptPassword("Passwort setzen");
			if (!isEmpty(password)) {
				saveFileTo(filename, ServiceHelper.getConfigurationsService().exportPersonData(password));
			}
		}
	}

	private void showHinweis(String text) {
		JOptionPane.showMessageDialog(frame, text, "Hinweis", JOptionPane.INFORMATION_MESSAGE);
	}

	private void saveFileTo(String filename, byte[] data) {
		if (saveFile(filename, data)) {
			JOptionPane.showMessageDialog(null, "Die Datei wurde gespeichert.", "Speichern", JOptionPane.INFORMATION_MESSAGE);
		} else {
			JOptionPane.showMessageDialog(null, "Die Datei konnte nicht gespeichert werden.", "Speichern", JOptionPane.ERROR_MESSAGE);
		}
	}

	private String promptPassword(String title) {
		final JPasswordField passwordField = new JPasswordField(10);
		passwordField.setEchoChar('#');
		passwordField.addAncestorListener(new RequestFocusListener());
		JOptionPane.showMessageDialog(frame, passwordField, title, JOptionPane.QUESTION_MESSAGE);
		String password = new String(passwordField.getPassword());
		if (isEmpty(password)) {
			JOptionPane.showMessageDialog(frame, "Passwort darf nicht leer sein.", "Fehler", JOptionPane.ERROR_MESSAGE);
		}
		return password;
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

	public String showFileDialog(Frame f, String title, String defDir, String fileType, int dlgType) {
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

	public File showJFileChooser(String[] extensions) {
		JFileChooser fileChooser = new JFileChooser();
		FileNameExtensionFilter filter = new FileNameExtensionFilter("Datendateien", extensions);
		fileChooser.setFileFilter(filter);

		String currentDir = ConfigurationLocalHelper.getConfig().getFileChooserLastPath();
		if (currentDir == null || "".equals(currentDir)) {
			currentDir = "\\.";
		}

		fileChooser.setCurrentDirectory(new File(currentDir));
		fileChooser.setVisible(true);
		fileChooser.showOpenDialog(frame);
		File selectedFile = fileChooser.getSelectedFile();
		if (selectedFile != null) {
			ConfigurationLocalHelper.getConfig().setFileChooserLastPath(selectedFile.getParent());
		}

		return selectedFile;
	}

	private boolean isEmpty(String s) {
		return s == null || s.isEmpty();
	}

	private void importSuccess(boolean success) {
		if (success) {
			JOptionPane.showMessageDialog(frame, "Die Datei wurde gespeichert.", "Importieren", JOptionPane.INFORMATION_MESSAGE);
		} else {
			JOptionPane.showMessageDialog(frame, "Die Datei konnte nicht gespeichert werden.", "Importieren", JOptionPane.ERROR_MESSAGE);
		}
	}

}
