package ch.infbr5.sentinel.client.gui.util;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;

import ch.infbr5.sentinel.common.gui.util.RequestFocusListener;

public class AskForPasswordDialog {

   private final String originalPassword;

   public AskForPasswordDialog(final String originalPassword) {
      this.originalPassword = originalPassword;
   }

   public boolean askForPassword() {
      if (checkRequirements()) {
         final String password = promptPassword();
         if (originalPassword.equals(password)) {
            return true;
         }
         JOptionPane.showMessageDialog(null, "Passwort nicht korrekt.", "Passwort falsch", JOptionPane.WARNING_MESSAGE);
      }
      return false;
   }

   private String promptPassword() {
      final JLabel label = new JLabel("Bitte Passwort eingeben");
      final JPasswordField passwordField = new JPasswordField();
      passwordField.addAncestorListener(new RequestFocusListener());
      final Object[] fields = { label, passwordField };

      JOptionPane.showConfirmDialog(null, fields, "Passwort", JOptionPane.OK_CANCEL_OPTION,
            JOptionPane.INFORMATION_MESSAGE);

      return String.valueOf(passwordField.getPassword());
   }

   private boolean checkRequirements() {
      if (originalPassword == null || originalPassword.isEmpty()) {
         JOptionPane.showMessageDialog(null, "Kein Password definiert", "Fehler", JOptionPane.ERROR_MESSAGE);
         return false;
      }
      return true;
   }

}
