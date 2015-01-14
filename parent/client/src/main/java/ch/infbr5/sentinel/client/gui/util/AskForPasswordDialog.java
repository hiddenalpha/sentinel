package ch.infbr5.sentinel.client.gui.util;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;

public class AskForPasswordDialog {

   public boolean askForPassword(final String originalPassword) {
      if (originalPassword == null || originalPassword.isEmpty()) {
         JOptionPane.showMessageDialog(null, "Kein Password definiert", "Fehler", JOptionPane.ERROR_MESSAGE);
         return false;
      }

      final JLabel label = new JLabel("Bitte Passwort eingeben");
      final JPasswordField passwordField = new JPasswordField();
      final Object[] fields = { label, passwordField };

      JOptionPane.showConfirmDialog(null, fields, "Passwort", JOptionPane.OK_CANCEL_OPTION,
            JOptionPane.INFORMATION_MESSAGE);

      final String adminPassword = String.valueOf(passwordField.getPassword());

      if (adminPassword != null && adminPassword.equals(originalPassword)) {
         return true;
      } else {
         JOptionPane.showMessageDialog(null, "Passwort nicht korrekt.", "Passwort falsch", JOptionPane.WARNING_MESSAGE);
         return false;
      }
   }

}
