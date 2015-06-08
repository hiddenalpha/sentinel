package ch.infbr5.sentinel.client;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import ch.infbr5.sentinel.client.config.ConfigurationHelper;
import ch.infbr5.sentinel.client.config.ConfigurationLocalHelper;
import ch.infbr5.sentinel.client.config.checkpoint.CheckpointConfigurator;
import ch.infbr5.sentinel.client.config.connection.ConnectionConfigurator;
import ch.infbr5.sentinel.client.config.server.ServerConfigurationDialog;
import ch.infbr5.sentinel.client.gui.ApplicationFrame;
import ch.infbr5.sentinel.client.gui.components.FileUpAndDownload;
import ch.infbr5.sentinel.client.gui.components.PersonenBilderImporter;
import ch.infbr5.sentinel.client.gui.components.checkin.CheckInModelImpl;
import ch.infbr5.sentinel.client.gui.components.configuration.AdminstrationFrame;
import ch.infbr5.sentinel.client.gui.components.importer.PersonenImportDialog;
import ch.infbr5.sentinel.client.gui.info.InfoDialog;
import ch.infbr5.sentinel.client.gui.util.AskForPasswordDialog;
import ch.infbr5.sentinel.client.util.ServiceHelper;
import ch.infbr5.sentinel.client.wsgen.SystemInfo;
import ch.infbr5.sentinel.common.system.SystemInformation;

public class ApplicationController {

   private final ApplicationFrame appFrame;

   public ApplicationController(final String checkpointName, final Long checkpointId, final boolean adminMode,
         final boolean superUserMode) {
      appFrame = new ApplicationFrame(checkpointName, adminMode, superUserMode, new CheckInModelImpl(checkpointId));
      installActionListeners();
   }

   public void show() {
      appFrame.setVisible(true);
   }

   public ApplicationFrame getFrame() {
      return appFrame;
   }

   private void installActionListeners() {
      appFrame.addActionListenerEinstellungen(new ActionListener() {
         @Override
         public void actionPerformed(final ActionEvent e) {
            AdminstrationFrame.getInstance().setVisible(true);
         }
      });

      appFrame.addActionListenerEnableSuperUserMode(new ActionListener() {
         @Override
         public void actionPerformed(final ActionEvent e) {
            if (new AskForPasswordDialog(ConfigurationHelper.getSuperUserPassword()).askForPassword()) {
               changeSuperUserMode(true);
            }
         }
      });

      appFrame.addActionListenerDisableSuperUserMode(new ActionListener() {
         @Override
         public void actionPerformed(final ActionEvent e) {
            changeSuperUserMode(false);
         }
      });

      appFrame.addActionListenerEnableAdminMode(new ActionListener() {
         @Override
         public void actionPerformed(final ActionEvent e) {
            if (new AskForPasswordDialog(ConfigurationHelper.getAdminPassword()).askForPassword()) {
               changeAdminMode(true);
            }
         }
      });

      appFrame.addActionListenerDisableAdminMode(new ActionListener() {
         @Override
         public void actionPerformed(final ActionEvent e) {
            changeAdminMode(false);
         }
      });

      appFrame.addActionListenerClose(new ActionListener() {
         @Override
         public void actionPerformed(final ActionEvent e) {
            appFrame.dispose();
            System.exit(0);
         }
      });

      appFrame.addActionListenerCheckpointEinstellung(new ActionListener() {
         @Override
         public void actionPerformed(final ActionEvent e) {
            final CheckpointConfigurator confi = new CheckpointConfigurator(false, false);
            confi.configureCheckpointConfiguration();
         }
      });

      appFrame.addActionListenerServerVerbindung(new ActionListener() {
         @Override
         public void actionPerformed(final ActionEvent e) {
            final ConnectionConfigurator config = new ConnectionConfigurator(false, false);
            config.configureConnectionConfiguration();
         }
      });

      appFrame.addActionListenerManuelleAuswahl(new ActionListener() {
         @Override
         public void actionPerformed(final ActionEvent e) {
            appFrame.showManuelleCheckinAuswahl();
         }
      });

      appFrame.addActionListenerAusweisdatenExportieren(new ActionListener() {
         @Override
         public void actionPerformed(final ActionEvent e) {
            new FileUpAndDownload(appFrame).exportPersonData();
         }
      });

      appFrame.addActionListenerAusweisdatenImportieren(new ActionListener() {
         @Override
         public void actionPerformed(final ActionEvent e) {
            new FileUpAndDownload(appFrame).importPersonData();
         }
      });

      appFrame.addActionListenerConfigurationExportieren(new ActionListener() {
         @Override
         public void actionPerformed(final ActionEvent e) {
            new FileUpAndDownload(appFrame).exportConfiguration();
         }
      });

      appFrame.addActionListenerConfigurationImportieren(new ActionListener() {
         @Override
         public void actionPerformed(final ActionEvent e) {
            final ServerConfigurationDialog dialog = new ServerConfigurationDialog(appFrame, ServiceHelper
                  .getConfigurationsService().getServerSetupInformation(), false);
            dialog.setVisible(true);
         }
      });

      appFrame.addActionListenerPisaDatenImportieren(new ActionListener() {
         @Override
         public void actionPerformed(final ActionEvent e) {
            new PersonenImportDialog(appFrame).show();
         }
      });

      appFrame.addActionListenerPersonenbilderImport(new ActionListener() {
         @Override
         public void actionPerformed(final ActionEvent e) {
            new PersonenBilderImporter(appFrame).showDialog();
         }
      });

      appFrame.addActionListenerInfo(new ActionListener() {
         @Override
         public void actionPerformed(final ActionEvent e) {
            final SystemInfo infoServer = ServiceHelper.getSentinelService().getSystemInfo();
            final SystemInfo infoClient = new SystemInfo();
            final SystemInformation system = new SystemInformation();
            infoClient.setJavaHome(system.getJavaHome());
            infoClient.setJavaVendor(system.getJavaVendor());
            infoClient.setJavaVersion(system.getJavaVersion());
            infoClient.setSentinelVersion(Version.get().getVersion());
            infoClient.setSentinelBuild(Version.get().getBuildTimestamp());
            infoClient.setOsArch(system.getOsArch());
            infoClient.setOsName(system.getOsName());
            infoClient.setOsVersion(system.getOsVersion());
            infoClient.setUserDir(system.getUserDir());
            new InfoDialog(appFrame, infoClient, infoServer).showDialog();
         }
      });
   }

   private void changeSuperUserMode(final boolean mode) {
      ConfigurationLocalHelper.getConfig().setSuperuserMode(mode);
      appFrame.setSuperUserMode(mode);
   }

   private void changeAdminMode(final boolean mode) {
      ConfigurationLocalHelper.getConfig().setAdminMode(mode);
      appFrame.setAdminMode(mode);
   }

}
