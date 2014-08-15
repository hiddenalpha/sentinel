package ch.infbr5.sentinel.client.config.connection;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import net.miginfocom.swing.MigLayout;
import ch.infbr5.sentinel.client.config.ConfigurationLocalHelper;

public class ConnectionConfigurationDialog extends JDialog {

	private static final long serialVersionUID = 1L;

	// UI Components
	private JButton btnSave;

	private JButton btnCancel;

	private final ConnectionConfigurationPanel panel;

	public ConnectionConfigurationDialog(final JFrame parent, final ConnectionConfigurator config, String info, String serverName, String serverPort, final boolean isConfigurationWhileStartup) {
		super(parent);

		setModal(true);
		setTitle("Server-Verbindung konfigurieren");
		setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		setResizable(false);
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				dispose();
				if (isConfigurationWhileStartup) {
					System.exit(0);
				}
			}
		});

		panel = new ConnectionConfigurationPanel(info, serverName, serverPort);

		btnSave = new JButton("Speichern");
		btnSave.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (!isConfigurationWhileStartup) {
					if (JOptionPane.YES_OPTION == JOptionPane.showConfirmDialog(parent, "Möchten Sie die Einstellung wirklich speichern? Der Client wird automatisch beendet. Starten Sie diesen dannach neu.", "Konfiguration speichern", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE)) {
						saveConfig();
						System.exit(0);
					}
				} else {
					saveConfig();
					dispose();
				}
			}

			private void saveConfig() {
				ConfigurationLocalHelper.getConfig().setServerHostname(panel.getServerName());
				ConfigurationLocalHelper.getConfig().setServerPortnumber(panel.getServerPort());
			}
		});

		btnCancel = new JButton("Abbrechen");
		btnCancel.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				dispose();
				if (isConfigurationWhileStartup) {
					System.exit(0);
				}
			}
		});

		setLayout(new MigLayout());
		add(panel, "push, span, growx, wrap");
		add(btnSave, "tag ok, span, split");
		add(btnCancel, "tag cancel");
		setSize(430, 340);
		setLocationRelativeTo(null);
	}

}
