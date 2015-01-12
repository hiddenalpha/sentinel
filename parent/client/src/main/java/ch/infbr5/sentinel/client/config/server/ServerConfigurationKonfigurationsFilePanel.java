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
import ch.infbr5.sentinel.client.gui.components.FileUpAndDownload;
import ch.infbr5.sentinel.client.util.ServiceHelper;
import ch.infbr5.sentinel.client.wsgen.ServerSetupInformation;
import ch.infbr5.sentinel.common.gui.util.SwingHelper;

import com.google.common.io.Files;

public class ServerConfigurationKonfigurationsFilePanel extends JPanel {

   private static final long serialVersionUID = 1L;

   private JLabel lblConfigFile;

   private JLabel lblConfigFilePassword;

   private JButton btnConfigFile;

   private JTextField txtConfigFilePassword;

   private JButton btnLoadConfig;

   private String currentSelectedFilePath;

   public ServerConfigurationKonfigurationsFilePanel(final ServerConfigurationKonfigurationsWertePanel valuePanel,
         final ServerConfigurationAusweisvorlagePanel ausweisvorlagePanel) {
      lblConfigFile = new JLabel();
      lblConfigFilePassword = SwingHelper.createLabel("Passwort");
      btnLoadConfig = new JButton("Konfiguration laden");
      btnLoadConfig.setEnabled(false);
      btnLoadConfig.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(final ActionEvent e) {
            if (txtConfigFilePassword.getText() == null || txtConfigFilePassword.getText().isEmpty()) {
               txtConfigFilePassword.setBorder(BorderFactory.createLineBorder(Color.red));
               return;
            } else {
               txtConfigFilePassword.setBorder(BorderFactory.createLineBorder(Color.black));
            }
            final File f = new File(currentSelectedFilePath);
            byte[] data;
            try {
               data = Files.toByteArray(f);
               final ServerSetupInformation infoFromFile = ServiceHelper.getConfigurationsService()
                     .getServerSetupInformationFromConfigFile(data, txtConfigFilePassword.getText());

               valuePanel.applyInfosFromFile(infoFromFile);
               ausweisvorlagePanel.appyInfosFromFile(infoFromFile.getAusweisvorlageConfig());
            } catch (final Exception ex) {
               JOptionPane.showMessageDialog(null,
                     "Fehler beim laden der Konfigurationsdatei. Eventuell Passwort falsch: " + ex.getMessage(),
                     "Fehler", JOptionPane.ERROR_MESSAGE);
            }
         }
      });
      btnConfigFile = new JButton("Datei wählen");
      btnConfigFile.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(final ActionEvent e) {
            final String filepath = new FileUpAndDownload(null).showImportConfigurationFileDialog();
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

      setLayout(new MigLayout());

      final JPanel filePanel = new JPanel(new MigLayout());
      filePanel.add(btnConfigFile, "");
      filePanel.add(lblConfigFile, "wrap");
      filePanel.add(lblConfigFilePassword, "");
      filePanel.add(txtConfigFilePassword, "growx, push, wrap");
      filePanel.add(btnLoadConfig, "spanx, align right");
      SwingHelper.attachLabledBorder("Konfigurationsdatei laden", filePanel);

      add(filePanel, "growx, push, wrap");
   }

}
