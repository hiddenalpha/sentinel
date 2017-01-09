package ch.infbr5.sentinel.client.config.checkpoint;

import java.util.List;

import javax.swing.JOptionPane;

import org.apache.log4j.Logger;

import ch.infbr5.sentinel.client.config.ConfigurationLocalHelper;
import ch.infbr5.sentinel.client.util.ServiceHelper;
import ch.infbr5.sentinel.client.wsgen.CheckpointDetails;

public class CheckpointConfigurator {

   private static final Logger log = Logger.getLogger(CheckpointConfigurator.class);

   private boolean isFirstConfiguration;

   private final boolean isConfigurationWhileStartup;

   private List<CheckpointDetails> checkpoints;

   public CheckpointConfigurator(final boolean isFirstConfiguration, final boolean isConfigurationWhileStartup) {
      this.isFirstConfiguration = isFirstConfiguration;
      this.isConfigurationWhileStartup = isConfigurationWhileStartup;
   }

   public void configureCheckpointConfiguration() {
      loadCheckpoints();

      if (!isConfigurationWhileStartup) {
         askForCheckpointConfiguration("Nach dem diese Einstellungen ge�ndert wurden, starten Sie den Sentiel Client neu.");
         return;
      }

      if (!doesCheckpointsExists()) {
         log.error("Keine Checkpoints auf dem Server konfiguriert");
         JOptionPane.showMessageDialog(null,
               "Auf dem Server sind keine Checkpoints konfiguriert. Benachrichtigen Sie den Administrator.",
               "Keine Checkpoints definiert", JOptionPane.ERROR_MESSAGE);
         System.exit(0);
      }

      if (isFirstConfiguration) {
         askForCheckpointConfiguration("Dies ist eine Erstkonfiguration. Wählen Sie den Checkpoint aus und konfigurieren Sie allf�llige Kameras.");
         isFirstConfiguration = false;
      } else {
         if (!hasValidConfiguration()) {
            askForCheckpointConfiguration("Wählen Sie den Checkpoint aus und konfigurieren Sie allfällige Kameras.");
         }
      }

      // Bisher kann man keine Fehl-Konfiguration machen. Man muss einen
      // Checkpoint ausw�hlen und mann kann 0-x Kameras definieren.
   }

   private void loadCheckpoints() {
      this.checkpoints = ServiceHelper.getConfigurationsService().getCheckpoints().getCheckpointDetails();
   }

   private boolean doesCheckpointsExists() {
      return !checkpoints.isEmpty();
   }

   /**
    * Pr�ft ob die Checkpoint ID beim Server bekannt ist.
    *
    * @return True, falls ja, anderenfalls false.
    */
   private boolean hasValidConfiguration() {
      boolean checkpointIdIsKnown = false;

      final Long idCheckpoint = ConfigurationLocalHelper.getConfig().getCheckpointId();
      for (final CheckpointDetails detail : checkpoints) {
         if (detail.getId().equals(idCheckpoint)) {
            checkpointIdIsKnown = true;
         }
      }

      return checkpointIdIsKnown;
   }

   /**
    * Zeigt den Konfigurations Dialog f�r den Checkpoint an.
    */
   private void askForCheckpointConfiguration(final String info) {
      log.debug("Client nach Checkpointkonfiguration fragen.");
      final CheckpointConfigurationDialog dialog = new CheckpointConfigurationDialog(null, this, info,
            isConfigurationWhileStartup);
      dialog.setVisible(true);
   }

}
