package ch.infbr5.sentinel.client.config.server;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import net.miginfocom.swing.MigLayout;
import ch.infbr5.sentinel.client.config.checkpoint.CheckpointConfigurationCamerasPanel;
import ch.infbr5.sentinel.client.gui.components.FileUpAndDownload;
import ch.infbr5.sentinel.client.util.ServiceHelper;
import ch.infbr5.sentinel.client.wsgen.ServerSetupInformation;
import ch.infbr5.sentinel.common.gui.util.SwingHelper;

import com.google.common.io.Files;

public class ServerConfigurationPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	private JPanel groupPanel;

	private JLabel lblConfigFile;

	private JLabel lblConfigFilePassword;

	private JButton btnConfigFile;

	private JTextField txtConfigFilePassword;

	private JButton btnLoadConfig;

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

	private String currentSelectedFilePath;

	public ServerConfigurationPanel(ServerSetupInformation info) {
		this.info = info;

		lblConfigFile = new JLabel();
		lblConfigFilePassword = SwingHelper.createLabel("Passwort");
		btnLoadConfig = new JButton("Konfiguration laden");
		btnLoadConfig.setEnabled(false);
		btnLoadConfig.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (txtConfigFilePassword.getText() == null || txtConfigFilePassword.getText().isEmpty()) {
					txtConfigFilePassword.setBorder(BorderFactory.createLineBorder(Color.red));
					return;
				} else {
					txtConfigFilePassword.setBorder(BorderFactory.createLineBorder(Color.black));
				}
				File f = new File(currentSelectedFilePath);
				byte[] data;
				try {
					data = Files.toByteArray(f);
					ServerSetupInformation infoFromFile = ServiceHelper.getConfigurationsService().getServerSetupInformationFromConfigFile(data, txtConfigFilePassword.getText());
					txtAdminPw.setText(infoFromFile.getAdminPassword());
					txtSuperUserPw.setText(infoFromFile.getSuperUserPassword());
					txtIdentityCardPw.setText(infoFromFile.getIdentityCardPassword());
					cameraPanel.setUrls(infoFromFile.getIpCamUrls());
				} catch (Exception ex) {
					JOptionPane.showMessageDialog(null, "Fehler beim laden der Konfigurationsdatei. Eventuell Passwort falsch: " + ex.getMessage(), "Fehler", JOptionPane.ERROR_MESSAGE);
				}
			}
		});
		btnConfigFile = new JButton("Datei wählen");
		btnConfigFile.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String filepath = new FileUpAndDownload(null).showImportConfigurationFileDialog();
				if (filepath != null && (new File(filepath)).exists()) {
					currentSelectedFilePath = filepath;
					lblConfigFile.setText(new File(filepath).getName());
					btnLoadConfig.setEnabled(true);
				} else {
					btnLoadConfig.setEnabled(false);
				}
			}
		});
		txtConfigFilePassword = new JTextField();

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

		JPanel filePanel = new JPanel(new MigLayout());
		filePanel.add(btnConfigFile, "");
		filePanel.add(lblConfigFile, "wrap");
		filePanel.add(lblConfigFilePassword, "");
		filePanel.add(txtConfigFilePassword, "growx, push, wrap");
		filePanel.add(btnLoadConfig, "spanx, align right");
		SwingHelper.attachLabledBorder("Konfigurationsdatei laden", filePanel);

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

		add(filePanel, "growx, push, wrap");
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

}
