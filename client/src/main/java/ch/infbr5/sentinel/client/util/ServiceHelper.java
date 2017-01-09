package ch.infbr5.sentinel.client.util;

import java.net.MalformedURLException;
import java.net.URL;

import javax.xml.namespace.QName;

import ch.infbr5.sentinel.client.wsgen.ConfigurationQueryService;
import ch.infbr5.sentinel.client.wsgen.ConfigurationQueryServiceService;
import ch.infbr5.sentinel.client.wsgen.JournalService;
import ch.infbr5.sentinel.client.wsgen.JournalServiceService;
import ch.infbr5.sentinel.client.wsgen.PersonenImporterService;
import ch.infbr5.sentinel.client.wsgen.PersonenImporterServiceService;
import ch.infbr5.sentinel.client.wsgen.SentinelQueryService;
import ch.infbr5.sentinel.client.wsgen.SentinelQueryServiceService;

public class ServiceHelper {

   private static SentinelQueryService sentinelService;
   private static ConfigurationQueryService configurationService;
   private static JournalService journalService;
   private static PersonenImporterService personenImportService;

   public static ConfigurationQueryService getConfigurationsService() {
      return configurationService;
   }

   public static JournalService getJournalService() {
      return journalService;
   }

   public static SentinelQueryService getSentinelService() {
      return sentinelService;
   }

   public static PersonenImporterService getPersonenImporterService() {
      return personenImportService;
   }

   public static void setEndpointAddress(final String address) throws MalformedURLException {
      if (ServiceHelper.sentinelService == null) {
         ServiceHelper.sentinelService = new SentinelQueryServiceService(new URL(address + "/services?wsdl"),
               new QName("http://ws.sentinel.infbr5.ch/", "SentinelQueryServiceService")).getSentinelQueryServicePort();
      }

      if (ServiceHelper.journalService == null) {
         ServiceHelper.journalService = new JournalServiceService(new URL(address + "/journal?wsdl"), new QName(
               "http://ws.sentinel.infbr5.ch/", "JournalServiceService")).getJournalServicePort();
      }

      if (ServiceHelper.configurationService == null) {
         ServiceHelper.configurationService = new ConfigurationQueryServiceService(new URL(address
               + "/configurations?wsdl"),
               new QName("http://ws.sentinel.infbr5.ch/", "ConfigurationQueryServiceService"))
               .getConfigurationQueryServicePort();
      }

      if (personenImportService == null) {
         personenImportService = new PersonenImporterServiceService(new URL(address + "/personenImporter?wsdl"),
               new QName("http://ws.sentinel.infbr5.ch/", "PersonenImporterServiceService"))
               .getPersonenImporterServicePort();
      }
   }
}
