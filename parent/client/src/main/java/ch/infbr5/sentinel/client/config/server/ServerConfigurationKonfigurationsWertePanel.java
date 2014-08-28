package ch.infbr5.sentinel.client.config.server;

import java.awt.Color;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import net.miginfocom.swing.MigLayout;
import ch.infbr5.sentinel.client.config.checkpoint.CheckpointConfigurationCamerasPanel;
import ch.infbr5.sentinel.client.util.ServiceHelper;
import ch.infbr5.sentinel.client.wsgen.ServerSetupInformation;
import ch.infbr5.sentinel.common.gui.util.SwingHelper;

public class ServerConfigurationKonfigurationsWertePanel extends JPanel {

	private static final long serialVersionUID = 1L;

	private JPanel groupPanel;

	private JLabel lblCheckpointName;

	private JLabel lblZoneName;

	private JLabel lblSuperUserPw;

	private JLabel lblAdminPw;

	private JLabel lblIdentityCardPw;

	private JTextField txtCheckpointName;

	private JTextField txtZoneName;

	private JTextField txtSuperUserPw;

	private JTextField txtAdminPw;

	private JTextField txtIdentityCardPw;

	private ServerSetupInformation info;

	private CheckpointConfigurationCamerasPanel cameraPanel;

	public ServerConfigurationKonfigurationsWertePanel(ServerSetupInformation info) {
		this.info = info;

		lblCheckpointName = SwingHelper.createLabel("Checkpoint-Name");
		lblZoneName = SwingHelper.createLabel("Zonen-Name");
		lblSuperUserPw = SwingHelper.createLabel("Passwort Superuser");
		lblAdminPw = SwingHelper.createLabel("Passwort Admin");
		lblIdentityCardPw = SwingHelper.createLabel("Passwort Identity-Card");

		txtCheckpointName = SwingHelper.createTextField(info.getCheckpointName());
		txtZoneName = SwingHelper.createTextField(info.getZonenName());
		txtAdminPw = SwingHelper.createTextField(info.getAdminPassword());
		txtSuperUserPw = SwingHelper.createTextField(info.getSuperUserPassword());
		txtIdentityCardPw = SwingHelper.createTextField(info.getIdentityCardPassword());
		txtIdentityCardPw.setEnabled(false); // Momentan deaktiviert, das muss man in einem grösseren Refactoring betrachten.

		cameraPanel = new CheckpointConfigurationCamerasPanel(false, ServiceHelper.getConfigurationsService().getIPCams().getCams());

		groupPanel = new JPanel(new MigLayout());
		SwingHelper.attachLabledBorder("Server Setup", groupPanel);

		setLayout(new MigLayout());

		groupPanel.add(lblCheckpointName, "");
		groupPanel.add(txtCheckpointName, "push, growx, wrap");

		groupPanel.add(lblZoneName, "");
		groupPanel.add(txtZoneName, "growx, wrap");

		groupPanel.add(lblSuperUserPw, "");
		groupPanel.add(txtSuperUserPw, "growx, wrap");

		groupPanel.add(lblAdminPw, "");
		groupPanel.add(txtAdminPw, "growx, wrap");

		groupPanel.add(lblIdentityCardPw, "");
		groupPanel.add(txtIdentityCardPw, "growx, wrap");

		groupPanel.add(cameraPanel, "growx, span, push");

		if (info.isCheckpointConfigured()) {
			txtCheckpointName.setEnabled(false);
		}
		if (info.isZoneConfigured()) {
			txtZoneName.setEnabled(false);
		}

		add(groupPanel, "growx, push");
	}

	public boolean validateInfo() {
		boolean isValid = true;
		if (!validateComponent(txtCheckpointName)) {
			isValid = false;
		}
		if (!validateComponent(txtZoneName)) {
			isValid = false;
		}
		if (!validateComponent(txtSuperUserPw)) {
			isValid = false;
		}
		if (!validateComponent(txtAdminPw)) {
			isValid = false;
		}
		if (!validateComponent(txtIdentityCardPw)) {
			isValid = false;
		}
		return isValid;
	}

	private boolean validateComponent(JTextField txtField) {
		boolean isValid = true;;
		if (txtField.getText() == null || txtField.getText().isEmpty()) {
			isValid = false;
			txtField.setBorder(BorderFactory.createLineBorder(Color.red));
		}
		return isValid;
	}

	public ServerSetupInformation getInfo() {
		info.setAdminPassword(txtAdminPw.getText());
		info.setCheckpointName(txtCheckpointName.getText());
		info.setZonenName(txtZoneName.getText());
		info.setSuperUserPassword(txtSuperUserPw.getText());
		info.setIdentityCardPassword(txtIdentityCardPw.getText());

		info.getIpCamUrls().clear();
		for (String url : cameraPanel.getUrls().getCams()) {
			info.getIpCamUrls().add(url);
		}
		return info;
	}

	public void applyInfosFromFile(ServerSetupInformation infoFromFile) {
		txtAdminPw.setText(infoFromFile.getAdminPassword());
		txtSuperUserPw.setText(infoFromFile.getSuperUserPassword());
		txtIdentityCardPw.setText(infoFromFile.getIdentityCardPassword());
		cameraPanel.setUrls(infoFromFile.getIpCamUrls());
	}

}
