package ch.infbr5.sentinel.client.config.server;

import java.awt.Color;
import java.awt.FileDialog;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import net.miginfocom.swing.MigLayout;
import ch.infbr5.sentinel.client.gui.components.FileUpAndDownload;
import ch.infbr5.sentinel.client.gui.util.ColorChooserLabel;
import ch.infbr5.sentinel.client.wsgen.AusweisvorlageKonfiguration;
import ch.infbr5.sentinel.common.gui.util.SwingHelper;
import ch.infbr5.sentinel.common.util.ImageUtil;

import com.google.common.io.Files;

public class ServerConfigurationAusweisvorlagePanel extends JPanel {

	private static final long serialVersionUID = 1L;

	// Wasserzeichen
	private JPanel wasserzeichenPanel;

	private JPanel wasserzeichenDefaultPanel;

	private JPanel wasserzeichenUserPanel;

	private JRadioButton rbDefaultWasserzeichen;

	private JRadioButton rbUserWasserzeichen;

	private JLabel lblStandardWasserzeichen;

	private JLabel lblStandardWasserzeichenImage;

	private JLabel lblWasserzeichen;

	private JLabel lblWasserzeichenImage;

	private JButton btnOpenWasserzeichen;

	// Logo
	private JPanel logoPanel;

	private JPanel logoDefaultPanel;

	private JPanel logoUserPanel;

	private JLabel lblLogo;

	private JLabel lblLogoImage;

	private JButton btnOpenLogo;

	private JCheckBox ckbUseLogo;

	// Components
	private JPanel groupPanel;

	private ColorChooserLabel chooserColorBackground;

	private ColorChooserLabel chooserColorBacksideArea;

	private JLabel lblBackgroundColor;

	private JLabel lblShowAreaBackside;

	private JCheckBox chkShowAreaBackside;

	private JLabel lblColorAreaBackside;

	private JLabel lblShowQRCode;

	private JCheckBox chkShowQRCode;

	// Data
	private AusweisvorlageKonfiguration config;

	public ServerConfigurationAusweisvorlagePanel(AusweisvorlageKonfiguration config) {
		this.config = config;

		lblBackgroundColor = SwingHelper.createLabel("Hintergrundfarbe");
		chooserColorBackground = new ColorChooserLabel();

		lblShowAreaBackside = SwingHelper.createLabel("Zus�tzliche Fl�che R�ckseite verwenden?");
		chkShowAreaBackside = new JCheckBox();

		lblShowQRCode = SwingHelper.createLabel("QR Code anzeigen?");
		chkShowQRCode = new JCheckBox();

		lblColorAreaBackside = SwingHelper.createLabel("Zus�tzliche Fl�che R�ckseite Farbe");
		chooserColorBacksideArea = new ColorChooserLabel();

		lblStandardWasserzeichen = SwingHelper.createLabel("Standard");
		rbDefaultWasserzeichen = new JRadioButton();
		lblStandardWasserzeichenImage = new JLabel();

		lblWasserzeichen = SwingHelper.createLabel("Benutzerdefiniert");
		rbUserWasserzeichen = new JRadioButton();
		lblWasserzeichenImage = new JLabel();

		lblLogo = SwingHelper.createLabel("Benutzerdefiniert");
		ckbUseLogo = new JCheckBox();
		lblLogoImage = new JLabel();

		btnOpenWasserzeichen = new JButton("Wasserzeichen laden ...");
		btnOpenWasserzeichen.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String filename = new FileUpAndDownload(null).showFileDialog(null, "Wasserzeichen importieren", "\\.",
						"*.png", FileDialog.LOAD);

				if (filename != null) {
					File f = new File(filename);
					if (f.exists()) {
						try {
							updateUserWasserzeichen(Files.toByteArray(f));
						} catch (IOException e1) {
							JOptionPane.showMessageDialog(null, "Konnte Bild nicht laden.");
						}
					}
				}

			}
		});

		btnOpenLogo = new JButton("Logo laden ...");
		btnOpenLogo.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String filename = new FileUpAndDownload(null).showFileDialog(null, "Logo importieren", "\\.", "*.png",
						FileDialog.LOAD);

				if (filename != null) {
					File f = new File(filename);
					if (f.exists()) {
						try {
							updateLogo(Files.toByteArray(f));
						} catch (IOException e1) {
							JOptionPane.showMessageDialog(null, "Konnte Bild nicht laden.");
						}
					}
				}

			}
		});

		appyInfosFromFile(config);

		setLayout(new MigLayout());

		ButtonGroup group = new ButtonGroup();
		group.add(rbDefaultWasserzeichen);
		group.add(rbUserWasserzeichen);

		groupPanel = new JPanel(new MigLayout());
		SwingHelper.attachLabledBorder("Einstellungen", groupPanel);

		wasserzeichenPanel = new JPanel(new MigLayout());
		SwingHelper.attachLabledBorder("Wasserzeichen (Optimal: Transparent, Quadratisch)", wasserzeichenPanel);

		logoPanel = new JPanel(new MigLayout());
		SwingHelper.attachLabledBorder("Logo (Optimal: Transparent, Quadratisch)", logoPanel);

		wasserzeichenDefaultPanel = new JPanel(new MigLayout());
		wasserzeichenUserPanel = new JPanel(new MigLayout());
		logoDefaultPanel = new JPanel(new MigLayout());
		logoUserPanel = new JPanel(new MigLayout());

		groupPanel.add(lblBackgroundColor, "");
		groupPanel.add(chooserColorBackground, "gapx 10, wrap");

		groupPanel.add(lblShowQRCode, "");
		groupPanel.add(chkShowQRCode, "gapx 10, growx, wrap");

		groupPanel.add(lblShowAreaBackside, "");
		groupPanel.add(chkShowAreaBackside, "gapx 10, growx, wrap");

		groupPanel.add(lblColorAreaBackside, "");
		groupPanel.add(chooserColorBacksideArea, "gapx 10, wrap");

		wasserzeichenDefaultPanel.add(rbDefaultWasserzeichen, "split");
		wasserzeichenDefaultPanel.add(lblStandardWasserzeichen, "wrap");
		wasserzeichenDefaultPanel.add(lblStandardWasserzeichenImage, "alignx center");

		wasserzeichenUserPanel.add(rbUserWasserzeichen, "split");
		wasserzeichenUserPanel.add(lblWasserzeichen, "wrap");
		wasserzeichenUserPanel.add(lblWasserzeichenImage, "alignx center, wrap");
		wasserzeichenUserPanel.add(btnOpenWasserzeichen, "alignx center, spanx, push, growx");

		wasserzeichenPanel.add(wasserzeichenDefaultPanel, "width 50%, aligny top");
		wasserzeichenPanel.add(wasserzeichenUserPanel, "aligny top, wrap");

		logoUserPanel.add(ckbUseLogo, "split");
		logoUserPanel.add(lblLogo, "wrap");
		logoUserPanel.add(lblLogoImage, "alignx center, wrap");
		logoUserPanel.add(btnOpenLogo, "alignx center, spanx, push, growx");

		logoPanel.add(logoDefaultPanel, "width 50%, aligny top");
		logoPanel.add(logoUserPanel, "aligny top, wrap");

		add(groupPanel, "growx, push, wrap");
		add(wasserzeichenPanel, "growx, push, wrap");
		add(logoPanel, "growx, push");
	}

	public void appyInfosFromFile(AusweisvorlageKonfiguration config) {
		chooserColorBackground.setBackgroundHtmlColor(config.getColorBackground());

		chkShowAreaBackside.setSelected(config.isShowAreaBackside());
		chooserColorBacksideArea.setBackgroundHtmlColor(config.getColorAreaBackside());
		chkShowQRCode.setSelected(config.isShowQRCode());

		rbDefaultWasserzeichen.setSelected(!config.isUseUserWasserzeichen());
		tmpDefaultWasserzeichen = config.getDefaultWasserzeichen();
		createImageLabel(lblStandardWasserzeichenImage, tmpDefaultWasserzeichen);

		rbUserWasserzeichen.setSelected(config.isUseUserWasserzeichen());
		updateUserWasserzeichen(config.getWasserzeichen());

		ckbUseLogo.setSelected(config.isUseUserLogo());
		updateLogo(config.getLogo());

		if (tmpUserWasserzeichen == null || tmpUserWasserzeichen.length == 0) {
			rbDefaultWasserzeichen.setSelected(true);
			rbUserWasserzeichen.setEnabled(false);
		} else {
			rbUserWasserzeichen.setEnabled(true);
		}
	}

	private void updateUserWasserzeichen(byte[] array) {
		tmpUserWasserzeichen = array;
		createImageLabel(lblWasserzeichenImage, tmpUserWasserzeichen);
		config.setWasserzeichen(array);
		rbUserWasserzeichen.setEnabled(true);
		rbUserWasserzeichen.setSelected(true);
	}

	private void updateLogo(byte[] array) {
		tmpUserLogo = array;
		createImageLabel(lblLogoImage, tmpUserLogo);
		config.setLogo(array);
		ckbUseLogo.setEnabled(true);
		ckbUseLogo.setSelected(true);
	}

	public AusweisvorlageKonfiguration readConfig() {
		config.setColorBackground(chooserColorBackground.getBackgroundHtmlColor());
		config.setColorAreaBackside(chooserColorBacksideArea.getBackgroundHtmlColor());
		config.setShowAreaBackside(chkShowAreaBackside.isSelected());

		config.setShowQRCode(chkShowQRCode.isSelected());
		config.setDefaultWasserzeichen(tmpDefaultWasserzeichen);
		config.setLogo(tmpUserLogo);
		config.setWasserzeichen(tmpUserWasserzeichen);
		config.setUseUserLogo(ckbUseLogo.isSelected());
		config.setUseUserWasserzeichen(rbUserWasserzeichen.isSelected());
		return config;
	}

	private byte[] tmpDefaultWasserzeichen;

	private byte[] tmpUserWasserzeichen;

	private byte[] tmpUserLogo;

	private void createImageLabel(JLabel lbl, byte[] data) {
		if (data != null && data.length > 0) {
			try {
				BufferedImage image = ImageIO.read(new ByteArrayInputStream(data));
				Image img = ImageUtil.scaleImage(image, 60, 60);
				ImageIcon icon = new ImageIcon(img);
				icon.getImage().flush();
				lbl.setIcon(icon);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public boolean validateInfo() {
		boolean isValid = true;
		if (chooserColorBackground.getBackground() == null) {
			isValid = false;
			chooserColorBackground.setBorder(BorderFactory.createLineBorder(Color.red));
		}
		if (chooserColorBacksideArea.getBackground() == null) {
			isValid = false;
			chooserColorBacksideArea.setBorder(BorderFactory.createLineBorder(Color.red));
		}
		return isValid;
	}

}
