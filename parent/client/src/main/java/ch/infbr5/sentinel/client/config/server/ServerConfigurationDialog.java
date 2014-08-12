package ch.infbr5.sentinel.client.config.server;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;

import net.miginfocom.swing.MigLayout;
import ch.infbr5.sentinel.client.config.ConfigurationLocalHelper;

public class ServerConfigurationDialog extends JDialog {

	private static final long serialVersionUID = 1L;

	// UI Components
	private JButton btnSave;

	private JButton btnCancel;

	private final ServerConfigurationPanel panel;

	public ServerConfigurationDialog(JFrame parent, final ServerConfiguration config, String info, String serverName, String serverPort) {
		super(parent);

		setModal(true);
		setTitle("Server-Verbindung konfigurieren");
		setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		setResizable(false);
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				dispose();
				System.exit(0);
			}
		});

		panel = new ServerConfigurationPanel(info, serverName, serverPort);

		btnSave = new JButton("Speichern");
		btnSave.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				ConfigurationLocalHelper.getConfig().setServerHostname(panel.getServerName());
				ConfigurationLocalHelper.getConfig().setServerPortnumber(panel.getServerPort());
				dispose();
			}
		});

		btnCancel = new JButton("Abbrechen");
		btnCancel.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				dispose();
				System.exit(0);
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
