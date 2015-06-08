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
      if (originalPassword == null || originalPassword.isEmpty()) {
         JOptionPane.showMessageDialog(null, "Kein Password definiert", "Fehler", JOptionPane.ERROR_MESSAGE);
         return false;
      }

      final JLabel label = new JLabel("Bitte Passwort eingeben");
      final JPasswordField passwordField = new JPasswordField();
      passwordField.addAncestorListener(new RequestFocusListener());
      final Object[] fields = { label, passwordField };

      JOptionPane.showConfirmDialog(null, fields, "Passwort", JOptionPane.OK_CANCEL_OPTION,
            JOptionPane.INFORMATION_MESSAGE);

      final String password = String.valueOf(passwordField.getPassword());

      if (!originalPassword.equals(password)) {
         JOptionPane.showMessageDialog(null, "Passwort nicht korrekt.", "Passwort falsch", JOptionPane.WARNING_MESSAGE);
         return false;
      }
      return false;
   }

}
