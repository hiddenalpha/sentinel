package ch.infbr5.sentinel.client.util;

import java.net.MalformedURLException;
import java.net.URL;

import javax.xml.namespace.QName;

import ch.infbr5.sentinel.client.wsgen.ConfigurationQueryService;
import ch.infbr5.sentinel.client.wsgen.ConfigurationQueryServiceService;
import ch.infbr5.sentinel.client.wsgen.JournalService;
import ch.infbr5.sentinel.client.wsgen.JournalServiceService;
import ch.infbr5.sentinel.client.wsgen.SentinelQueryService;
import ch.infbr5.sentinel.client.wsgen.SentinelQueryServiceService;

public class ServiceHelper {

	private static SentinelQueryService service;
	private static ConfigurationQueryService configuration;
	private static JournalService journal;

	public static ConfigurationQueryService getConfigurationsService() {
		// not working when queryservice is not initialized with endpoint address$

		return ServiceHelper.configuration;
	}

	public static JournalService getJournalService() {
		// not working when queryservice is not initialized with endpoint address

		return ServiceHelper.journal;
	}

	public static SentinelQueryService getSentinelService() {
		// not working when queryservice is not initialized with endpoint address

		return ServiceHelper.service;
	}

	public static void setEndpointAddress(String address) throws MalformedURLException{
		if(ServiceHelper.service==null) {
			ServiceHelper.service = new SentinelQueryServiceService(new URL(address + "/services?wsdl"), new QName("http://ws.sentinel.infbr5.ch/", "SentinelQueryServiceService")).getSentinelQueryServicePort();
		}

		if(ServiceHelper.journal==null) {
			ServiceHelper.journal = new JournalServiceService(new URL(address + "/journal?wsdl"),new QName("http://ws.sentinel.infbr5.ch/", "JournalServiceService")).getJournalServicePort();
		}

		if(ServiceHelper.configuration==null) {
			ServiceHelper.configuration = new ConfigurationQueryServiceService(new URL(address + "/configurations?wsdl"),new QName("http://ws.sentinel.infbr5.ch/", "ConfigurationQueryServiceService")).getConfigurationQueryServicePort();
		}
	}
}
