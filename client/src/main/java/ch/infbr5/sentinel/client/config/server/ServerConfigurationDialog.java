package ch.infbr5.sentinel.client.config.server;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;

import net.miginfocom.swing.MigLayout;
import ch.infbr5.sentinel.client.util.ServiceHelper;
import ch.infbr5.sentinel.client.wsgen.ServerSetupInformation;

public class ServerConfigurationDialog extends JDialog {

   private static final long serialVersionUID = 1L;

   private final JButton btnSave;

   private final JButton btnCancel;

   private final JTabbedPane tabbedPane;

   private final ServerConfigurationKonfigurationsWertePanel configWertePanel;

   private final ServerConfigurationAusweisvorlagePanel configAusweisvorlagePanel;

   private final ServerConfigurationKonfigurationsFilePanel configFilePanel;

   public ServerConfigurationDialog(final JFrame parent, final ServerSetupInformation info, final boolean closeAppOnExit) {
      super(parent);

      tabbedPane = new JTabbedPane(JTabbedPane.TOP);

      setModal(true);
      setTitle("Server Konfiguration");
      setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
      setResizable(true);
      addWindowListener(new WindowAdapter() {
         @Override
         public void windowClosing(final WindowEvent e) {
            dispose();
            if (closeAppOnExit) {
               System.exit(0);
            }
         }
      });

      configWertePanel = new ServerConfigurationKonfigurationsWertePanel(info);
      configAusweisvorlagePanel = new ServerConfigurationAusweisvorlagePanel(info.getAusweisvorlageConfig());
      configFilePanel = new ServerConfigurationKonfigurationsFilePanel(configWertePanel, configAusweisvorlagePanel);

      btnSave = new JButton("Speichern");
      btnSave.addActionListener(new ActionListener() {

         @Override
         public void actionPerformed(final ActionEvent e) {
            if (configWertePanel.validateInfo() && configAusweisvorlagePanel.validateInfo()) {
               if (JOptionPane.YES_OPTION == JOptionPane.showConfirmDialog(parent,
                     "M??chten Sie die Einstellung wirklich speichern?", "Konfiguration speichern",
                     JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE)) {
                  final ServerSetupInformation newInfo = configWertePanel.getInfo();
                  newInfo.setAusweisvorlageConfig(configAusweisvorlagePanel.readConfig());
                  ServiceHelper.getConfigurationsService().applyServerSetupInformation(newInfo);
                  dispose();
               }
            } else {
               JOptionPane.showMessageDialog(parent, "Ung??ltige Eingaben. Bitte ??berpr??fen Sie alle Tabs.",
                     "Ung??ltige Eingaben", JOptionPane.ERROR_MESSAGE);
            }
         }

      });

      btnCancel = new JButton("Abbrechen");
      btnCancel.addActionListener(new ActionListener() {

         @Override
         public void actionPerformed(final ActionEvent e) {
            dispose();
            if (closeAppOnExit) {
               System.exit(0);
            }
         }
      });

      // setLayout(new MigLayout());

      tabbedPane.addTab("Konfigurationswerte", configWertePanel);
      tabbedPane.addTab("Ausweisvorlage", configAusweisvorlagePanel);
      tabbedPane.addTab("Datei laden", configFilePanel);

      final JPanel pane = new JPanel(new MigLayout());
      pane.add(tabbedPane, "push, span, growx, wrap, growy");
      pane.add(btnSave, "tag ok, span, split");
      pane.add(btnCancel, "tag cancel");

      final JScrollPane paneScroll = new JScrollPane();
      paneScroll.add(pane);
      paneScroll.setViewportView(pane);

      add(paneScroll);
      setSize(500, 700);
      // pack();
      setLocationRelativeTo(null);
   }
}
