package ch.infbr5.sentinel.client.config;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;

import ch.infbr5.sentinel.client.util.ServiceHelper;
import ch.infbr5.sentinel.client.wsgen.CheckpointDetails;
import ch.infbr5.sentinel.client.wsgen.ConfigurationDetails;
import ch.infbr5.sentinel.client.wsgen.ConfigurationResponse;
import ch.infbr5.sentinel.common.config.ConfigConstants;

public class ConfigurationHelper {

   private static final Logger log = Logger.getLogger(ConfigurationHelper.class);

   public static List<ConfigurationDetails> loadConfigurationIPCams() {
      final ConfigurationResponse response = ServiceHelper.getConfigurationsService().getGlobalConfigurationValues(
            ConfigConstants.URL_IPCAM_ALL);
      return response.getConfigurationDetails();
   }

   public static URL[] getIPCams() {
      final List<ConfigurationDetails> details = loadConfigurationIPCams();
      final List<URL> urls = new ArrayList<URL>();
      for (final ConfigurationDetails detail : details) {
         try {
            final URL camURL = new URL(detail.getStringValue());
            urls.add(camURL);
         } catch (final MalformedURLException e) {
            log.warn("Keine gültige URL für IP-Cam: " + detail.getStringValue());
         }
      }
      return urls.toArray(new URL[0]);
   }

   public static String getAdminPassword() {
      final Long idCheckpoint = ConfigurationLocalHelper.getConfig().getCheckpointId();
      final ConfigurationResponse response = ServiceHelper.getConfigurationsService().getConfigurationValue(
            idCheckpoint, ConfigConstants.PASSWORD_ADMIN);
      if (response.getConfigurationDetails().isEmpty()) {
         return null;
      } else {
         return response.getConfigurationDetails().get(0).getStringValue();
      }
   }

   public static String getCheckpointName() {
      final ConfigurationResponse response = ServiceHelper.getConfigurationsService().getCheckpoints();
      final List<CheckpointDetails> res = response.getCheckpointDetails();
      for (final Iterator<CheckpointDetails> iterator = res.iterator(); iterator.hasNext();) {
         final CheckpointDetails cd = iterator.next();
         if (cd.getId() == ConfigurationLocalHelper.getConfig().getCheckpointId()) {
            return cd.getName();
         }
      }
      return ""; // TODO Darf nie eintreten, müsste eigentlich Exception werfen?
   }

   public static String getSuperUserPassword() {
      final Long idCheckpoint = ConfigurationLocalHelper.getConfig().getCheckpointId();
      final ConfigurationResponse response = ServiceHelper.getConfigurationsService().getConfigurationValue(
            idCheckpoint, ConfigConstants.PASSWORD_SUPERUSER);
      if (response.getConfigurationDetails().isEmpty()) {
         return null;
      } else {
         return response.getConfigurationDetails().get(0).getStringValue();
      }
   }

}
