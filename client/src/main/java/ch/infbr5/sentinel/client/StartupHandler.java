package ch.infbr5.sentinel.client;

import ch.infbr5.sentinel.client.config.ConfigurationLocalHelper;
import ch.infbr5.sentinel.client.config.checkpoint.CheckpointConfigurator;
import ch.infbr5.sentinel.client.config.connection.ConnectionConfigurator;
import ch.infbr5.sentinel.client.config.server.ServerConfigurator;

public class StartupHandler {

   public void startConfig() {
      final boolean isFirstConfiguration = ConfigurationLocalHelper.isFirstConfiguration();

      // Server Connection -> Inital + Keine Verbindung
      final ConnectionConfigurator srvConfig = new ConnectionConfigurator(isFirstConfiguration, true);
      srvConfig.configureConnectionConfiguration();

      // Server Setup -> Falls Server sagt er sei nicht setted up!
      final ServerConfigurator serverConfig = new ServerConfigurator();
      serverConfig.configureServerConfiguration();

      // Checkpoint -> Initial + kein gültiger Checkpoint lokal konfiguriert
      final CheckpointConfigurator ckpConfig = new CheckpointConfigurator(isFirstConfiguration, true);
      ckpConfig.configureCheckpointConfiguration();
   }

}
