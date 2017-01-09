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

   private final Frame frame;

   public FileUpAndDownload(final Frame parent) {
      frame = parent;
   }

   public String showImportConfigurationFileDialog() {
      return showFileDialog(frame, "Konfiguration importieren", "\\.", "*.zip", FileDialog.LOAD);
   }

   public void exportConfiguration() {
      final String filename = showFileDialog(frame, "Konfiguration exportieren", "\\.", "*.zip", FileDialog.SAVE);
      if (filename != null) {
         final String password = promptPassword("Passwort setzen");
         if (!isEmpty(password)) {
            saveFileTo(filename, ServiceHelper.getConfigurationsService().exportConfigData(password));
         }
      }
   }

   public void importPersonData() {
      showHinweis("Hinweis: Bestehende Daten werden gelöscht (Personendaten, Ausweisdaten, Personenbilder).");
      final String filename = showFileDialog(frame, "Ausweisdaten importieren", "\\.", "*.zip", FileDialog.LOAD);
      if (filename != null) {
         final String password = promptPassword("Passwort eingeben");
         if (!isEmpty(password)) {
            final boolean result = ServiceHelper.getConfigurationsService().importPersonData(loadFile(filename),
                  password);
            importSuccess(result);
         }
      }
   }

   public void exportPersonData() {
      final String filename = showFileDialog(frame, "Ausweisdaten exportieren", "\\.", "*.zip", FileDialog.SAVE);
      if (filename != null) {
         final String password = promptPassword("Passwort setzen");
         if (!isEmpty(password)) {
            saveFileTo(filename, ServiceHelper.getConfigurationsService().exportPersonData(password));
         }
      }
   }

   private void showHinweis(final String text) {
      JOptionPane.showMessageDialog(frame, text, "Hinweis", JOptionPane.INFORMATION_MESSAGE);
   }

   private void saveFileTo(final String filename, final byte[] data) {
      if (saveFile(filename, data)) {
         JOptionPane.showMessageDialog(null, "Die Datei wurde gespeichert.", "Speichern",
               JOptionPane.INFORMATION_MESSAGE);
      } else {
         JOptionPane.showMessageDialog(null, "Die Datei konnte nicht gespeichert werden.", "Speichern",
               JOptionPane.ERROR_MESSAGE);
      }
   }

   private String promptPassword(final String title) {
      final JPasswordField passwordField = new JPasswordField(10);
      passwordField.setEchoChar('#');
      passwordField.addAncestorListener(new RequestFocusListener());
      JOptionPane.showMessageDialog(frame, passwordField, title, JOptionPane.QUESTION_MESSAGE);
      final String password = new String(passwordField.getPassword());
      if (isEmpty(password)) {
         JOptionPane.showMessageDialog(frame, "Passwort darf nicht leer sein.", "Fehler", JOptionPane.ERROR_MESSAGE);
      }
      return password;
   }

   private byte[] loadFile(final String filename) {
      try {
         final File file = new File(filename);
         final FileInputStream fin = new FileInputStream(file);
         final byte fileContent[] = new byte[(int) file.length()];

         fin.read(fileContent);
         fin.close();
         return fileContent;
      } catch (final IOException e) {
         // TODO Auto-generated catch block
         e.printStackTrace();
      }

      return null;
   }

   private boolean saveFile(final String filename, final byte[] data) {
      try {
         if ((data != null) && (data.length > 0)) {
            final FileOutputStream fos = new FileOutputStream(filename);
            fos.write(data);
            fos.close();
         }
         return true;
      } catch (final Exception e) {
         e.printStackTrace();
         return false;
      }
   }

   public String showFileDialog(final Frame f, final String title, final String defDir, final String fileType,
         final int dlgType) {
      final FileDialog fd = new FileDialog(f, title, dlgType);
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

   public File showJFileChooser(final String[] extensions) {
      final JFileChooser fileChooser = new JFileChooser();
      final FileNameExtensionFilter filter = new FileNameExtensionFilter("Datendateien", extensions);
      fileChooser.setFileFilter(filter);

      String currentDir = ConfigurationLocalHelper.getConfig().getFileChooserLastPath();
      if (currentDir == null || "".equals(currentDir)) {
         currentDir = "\\.";
      }

      fileChooser.setCurrentDirectory(new File(currentDir));
      fileChooser.setVisible(true);
      fileChooser.showOpenDialog(frame);
      final File selectedFile = fileChooser.getSelectedFile();
      if (selectedFile != null) {
         ConfigurationLocalHelper.getConfig().setFileChooserLastPath(selectedFile.getParent());
      }

      return selectedFile;
   }

   private boolean isEmpty(final String s) {
      return s == null || s.isEmpty();
   }

   private void importSuccess(final boolean success) {
      if (success) {
         JOptionPane.showMessageDialog(frame, "Die Datei wurde gespeichert.", "Importieren",
               JOptionPane.INFORMATION_MESSAGE);
      } else {
         JOptionPane.showMessageDialog(frame, "Die Datei konnte nicht gespeichert werden.", "Importieren",
               JOptionPane.ERROR_MESSAGE);
      }
   }

}
