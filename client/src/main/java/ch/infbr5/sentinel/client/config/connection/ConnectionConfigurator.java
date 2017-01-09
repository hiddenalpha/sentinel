package ch.infbr5.sentinel.client.config.connection;

import org.apache.log4j.Logger;

import ch.infbr5.sentinel.client.config.ConfigurationLocalHelper;
import ch.infbr5.sentinel.client.util.ServiceHelper;

public class ConnectionConfigurator {

   private static Logger log = Logger.getLogger(ConnectionConfigurator.class);

   private boolean isFirstConfiguration;

   private final boolean isConfigurationWhileStartup;

   /**
    * Erzeugt eine ServerConfigurator.
    *
    * @param isFirstConfiguration
    *           Gibt an, ob das eine Inital Konfiguration ist.
    * @param isConfigurationWhileStartup
    *           Gibt an, ob das eine Startup Konfiguration ist.
    */
   public ConnectionConfigurator(final boolean isFirstConfiguration, final boolean isConfigurationWhileStartup) {
      this.isFirstConfiguration = isFirstConfiguration;
      this.isConfigurationWhileStartup = isConfigurationWhileStartup;
   }

   public void configureConnectionConfiguration() {
      if (!isConfigurationWhileStartup) {
         askForServerConfiguration("Nach dem diese Einstellungen geändert wurden, starten Sie den Sentiel Client neu.");
         return;
      }

      boolean success = false;
      if (isFirstConfiguration) {
         askForServerConfiguration("Dies ist eine Erstkonfiguration. überprüfen Sie lediglich die Einstellungen. Im Normalfall muss hier keine Anpassung vorgenommen werden.");
         isFirstConfiguration = false;
      }
      while (!success) {
         if (configureEndpointAddress()) {
            if (isServerReachable()) {
               success = true;
            }
         }
         if (!success) {
            askForServerConfiguration("Es konnte keine Verbindung zum eingetragenen Server aufgebaut werden. Möglicherweise läuft der Server nicht oder die Verbindungsdaten sind falsch.");
         }
      }
   }

   /**
    * Konfiguriert die Services. Setzt die Endpoint-Adresse. Falls eine
    * Exception auftritt, sind die EndpointAdressen ungültig.
    *
    * @return true, falls die Konfiguration erfolgreich war, anderenfalls false.
    */
   private boolean configureEndpointAddress() {
      final String endpointAddress = ConfigurationLocalHelper.getConfig().getEndpointAddress();
      try {
         ServiceHelper.setEndpointAddress(endpointAddress);
         log.debug("Endpoint-Addresse konnte konfiguriert werden: " + endpointAddress);
         return true;
      } catch (final Exception e) {
         log.debug("Endpoint-Addresse konnte nicht konfiguriert werden: " + endpointAddress);
         log.error(e);
         return false;
      }
   }

   /**
    * Pr�ft ob die Servers erreichbar sind.
    *
    * @return true, falls ja, anderenfalls false.
    */
   private boolean isServerReachable() {
      boolean isReachable = false;
      try {
         final String ping = ServiceHelper.getSentinelService().ping();
         if ("pong".equals(ping)) {
            log.debug("Client konnte sich erfolgreich mit dem Server verbinden");
            isReachable = true;
         }
      } catch (final Exception e) {
         log.error(e);
      }
      if (!isReachable) {
         log.debug("Client konnte sich nicht mit dem Server verbinden");
      }
      return isReachable;
   }

   /**
    * Fr�gt den Client nach der Serververbindung.
    */
   private void askForServerConfiguration(final String info) {
      log.debug("Client nach Serverkonfiguration fragen.");
      final ConnectionConfigurationDialog dialog = new ConnectionConfigurationDialog(null, this, info,
            ConfigurationLocalHelper.getConfig().getServerHostname(), ConfigurationLocalHelper.getConfig()
                  .getServerPortnumber(), isConfigurationWhileStartup);
      dialog.setVisible(true);
   }

}
