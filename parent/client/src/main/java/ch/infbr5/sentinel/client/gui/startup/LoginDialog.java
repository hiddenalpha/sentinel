package ch.infbr5.sentinel.client.gui.startup;

import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import net.miginfocom.swing.MigLayout;
import ch.infbr5.sentinel.utils.Login;

public class LoginDialog extends JDialog {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private JTextField usernameTextField;
	private JPasswordField passwordField;
	private boolean succeeded;

	public LoginDialog(Frame parent) {
		super(parent, "Login", true);

		JPanel panel = new JPanel(new MigLayout("wrap 2"));

		panel.add(new JLabel("Benutzername: "));

		this.usernameTextField = new JTextField(20);
		panel.add(this.usernameTextField);

		panel.add(new JLabel("Passwort: "));

		this.passwordField = new JPasswordField(20);
		panel.add(this.passwordField);

		// todo: remove next two lines
		this.usernameTextField.setText("inf");
		this.passwordField.setText("br5");

		JButton btnLogin = new JButton("Login");
		btnLogin.setDefaultCapable(true);

		btnLogin.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				if (Login.authenticate(LoginDialog.this.getUsername(), LoginDialog.this.getPassword())) {
					LoginDialog.this.succeeded = true;
					LoginDialog.this.dispose();
				} else {
					JOptionPane.showMessageDialog(LoginDialog.this,
							"Ungültiger Benutzername oder Passwort",
							"Login",
							JOptionPane.ERROR_MESSAGE);

					// reset username and password
					LoginDialog.this.usernameTextField.setText("");
					LoginDialog.this.passwordField.setText("");
					LoginDialog.this.succeeded = false;
				}
			}
		});

		JButton btnCancel = new JButton("Abbrechen");
		btnLogin.setDefaultCapable(false);
		btnCancel.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				// reset username and password
				LoginDialog.this.usernameTextField.setText("");
				LoginDialog.this.passwordField.setText("");
				LoginDialog.this.succeeded = false;

				LoginDialog.this.dispose();
			}
		});

		panel.add(btnLogin);
		panel.add(btnCancel);

		this.add(panel);

		this.pack();
		this.setResizable(false);
		this.setLocationRelativeTo(parent);
	}

	public String getPassword() {
		return new String(this.passwordField.getPassword());
	}

	public String getUsername() {
		return this.usernameTextField.getText().trim();
	}

	public boolean isSucceeded() {
		return this.succeeded;
	}
}
