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
   private final JPanel wasserzeichenPanel;

   private final JPanel wasserzeichenDefaultPanel;

   private final JPanel wasserzeichenUserPanel;

   private final JRadioButton rbDefaultWasserzeichen;

   private final JRadioButton rbUserWasserzeichen;

   private final JLabel lblStandardWasserzeichen;

   private final JLabel lblStandardWasserzeichenImage;

   private final JLabel lblWasserzeichen;

   private final JLabel lblWasserzeichenImage;

   private final JButton btnOpenWasserzeichen;

   // Logo
   private final JPanel logoPanel;

   private final JPanel logoDefaultPanel;

   private final JPanel logoUserPanel;

   private final JLabel lblLogo;

   private final JLabel lblLogoImage;

   private final JButton btnOpenLogo;

   private final JCheckBox ckbUseLogo;

   // Components
   private final JPanel groupPanel;

   private final ColorChooserLabel chooserColorBackground;

   private final ColorChooserLabel chooserColorBacksideArea;

   private final JLabel lblBackgroundColor;

   private final JLabel lblShowAreaBackside;

   private final JCheckBox chkShowAreaBackside;

   private final JLabel lblColorAreaBackside;

   private final JLabel lblShowQRCode;

   private final JCheckBox chkShowQRCode;

   // Data
   private final AusweisvorlageKonfiguration config;

   public ServerConfigurationAusweisvorlagePanel(final AusweisvorlageKonfiguration config) {
      this.config = config;

      lblBackgroundColor = SwingHelper.createLabel("Hintergrundfarbe");
      chooserColorBackground = new ColorChooserLabel();

      lblShowAreaBackside = SwingHelper.createLabel("Zusätzliche Fläche Rückseite verwenden?");
      chkShowAreaBackside = new JCheckBox();

      lblShowQRCode = SwingHelper.createLabel("QR Code anzeigen?");
      chkShowQRCode = new JCheckBox();

      lblColorAreaBackside = SwingHelper.createLabel("Zusätzliche Fläche Rückseite Farbe");
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
         public void actionPerformed(final ActionEvent e) {
            final String filename = new FileUpAndDownload(null).showFileDialog(null, "Wasserzeichen importieren",
                  "\\.", "*.png", FileDialog.LOAD);

            if (filename != null) {
               final File f = new File(filename);
               if (f.exists()) {
                  try {
                     updateUserWasserzeichen(Files.toByteArray(f));
                  } catch (final IOException e1) {
                     JOptionPane.showMessageDialog(null, "Konnte Bild nicht laden.");
                  }
               }
            }

         }
      });

      btnOpenLogo = new JButton("Logo laden ...");
      btnOpenLogo.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(final ActionEvent e) {
            final String filename = new FileUpAndDownload(null).showFileDialog(null, "Logo importieren", "\\.",
                  "*.png", FileDialog.LOAD);

            if (filename != null) {
               final File f = new File(filename);
               if (f.exists()) {
                  try {
                     updateLogo(Files.toByteArray(f));
                  } catch (final IOException e1) {
                     JOptionPane.showMessageDialog(null, "Konnte Bild nicht laden.");
                  }
               }
            }

         }
      });

      appyInfosFromFile(config);

      setLayout(new MigLayout());

      final ButtonGroup group = new ButtonGroup();
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

   public void appyInfosFromFile(final AusweisvorlageKonfiguration config) {
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

   private void updateUserWasserzeichen(final byte[] array) {
      tmpUserWasserzeichen = array;
      createImageLabel(lblWasserzeichenImage, tmpUserWasserzeichen);
      config.setWasserzeichen(array);
      rbUserWasserzeichen.setEnabled(true);
      rbUserWasserzeichen.setSelected(true);
   }

   private void updateLogo(final byte[] array) {
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

   private void createImageLabel(final JLabel lbl, final byte[] data) {
      if (data != null && data.length > 0) {
         try {
            final BufferedImage image = ImageIO.read(new ByteArrayInputStream(data));
            final Image img = ImageUtil.scaleImage(image, 60, 60);
            final ImageIcon icon = new ImageIcon(img);
            icon.getImage().flush();
            lbl.setIcon(icon);
         } catch (final IOException e) {
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
