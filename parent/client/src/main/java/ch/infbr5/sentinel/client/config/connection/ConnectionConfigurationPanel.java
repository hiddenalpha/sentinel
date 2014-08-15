package ch.infbr5.sentinel.client.config.connection;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import net.miginfocom.swing.MigLayout;
import ch.infbr5.sentinel.common.gui.util.SwingHelper;

public class ConnectionConfigurationPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	// UI Components
	private JPanel groupPanel;

	private JLabel lblInfo;

	private JLabel lblServerName;

	private JLabel lblServerPort;

	private JTextField txtServerName;

	private JTextField txtServerPort;

	public ConnectionConfigurationPanel(String info, String serverName, String serverPort) {
		lblInfo = SwingHelper.createLabel("<html>" + "Nachfolgend können Sie die Server-Verbindungsdaten eintragen.<br /><br />" + info + "</html>");
		lblServerName = SwingHelper.createLabel("Server IP-Adresse");
		lblServerPort = SwingHelper.createLabel("Server Port");
		groupPanel = new JPanel(new MigLayout());

		txtServerName = SwingHelper.createTextField(serverName);
		txtServerPort = SwingHelper.createTextField(serverPort);

		SwingHelper.attachLabledBorder("Server-Verbindung", groupPanel);
		SwingHelper.attachLabledBorder("Infos", lblInfo);

		setLayout(new MigLayout());

		groupPanel.add(lblServerName, "");
		groupPanel.add(txtServerName, "push, growx, wrap");

		groupPanel.add(lblServerPort, "");
		groupPanel.add(txtServerPort, "growx, wrap");

		add(lblInfo, "growx, wrap");
		add(groupPanel, "growx, push");

	}

	public String getServerName() {
		return txtServerName.getText().trim();
	}

	public String getServerPort() {
		return txtServerPort.getText().trim();
	}

}
