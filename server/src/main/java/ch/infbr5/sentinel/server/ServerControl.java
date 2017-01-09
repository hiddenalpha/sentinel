package ch.infbr5.sentinel.server;

import java.net.InetAddress;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.ws.Endpoint;

import org.apache.derby.drda.NetworkServerControl;
import org.apache.log4j.Logger;

import ch.infbr5.sentinel.server.db.EntityManagerHelper;
import ch.infbr5.sentinel.server.ws.SentinelQueryService;
import ch.infbr5.sentinel.server.ws.admin.ConfigurationQueryService;
import ch.infbr5.sentinel.server.ws.importer.PersonenImporterService;
import ch.infbr5.sentinel.server.ws.journal.JournalService;

import com.google.common.collect.Lists;

public class ServerControl {

	private static final Logger log = Logger.getLogger(ServerControl.class);

	private static List<Endpoint> endpoints;

	private static final int DB_PORT = 1527;

	private static final String DB_USER = "sentinel";

	private static final String DB_PW = "PWD";

	private NetworkServerControl databaseServer;

	private boolean running = true;

	public ServerControl(boolean debugMode, boolean inMemoryMode) {
		EntityManagerHelper.setDebugMode(debugMode);
		EntityManagerHelper.setInMemoryMode(inMemoryMode);
		endpoints = Lists.newArrayList();
	}

	public void start(String ip, String port) {
		startDerby();
		startWebServices(ip, port);
	}

	public void stop() {
		stopDerby();
		stopWebServces();
		running = false;
	}

	public boolean isRunning() {
		return running;
	}

	private void startDerby() {
		try {
			InetAddress localhost = InetAddress.getLoopbackAddress();
			databaseServer = new NetworkServerControl(localhost, DB_PORT, DB_USER, DB_PW);
			databaseServer.start(null);
		} catch (Exception e) {
			log.error(e);
		}

	}

   private void startWebServices(String ipAddress, String port) {
      Map<String, Object> services = new HashMap<>();
      services.put("services", new SentinelQueryService());
      services.put("configuration", new ConfigurationQueryService());
      services.put("journal", new JournalService());
      services.put("personenImporter", new PersonenImporterService());

      try {
         for (Entry<String, Object> service : services.entrySet()) {
            publishEndpoint(ipAddress, port, service.getKey(), service.getValue());
         }
      } catch (Exception e) {
         log.error(e);
      }
   }

	private void stopDerby() {
		try {
			EntityManagerHelper.close();
			databaseServer.shutdown();
		} catch (Exception e) {
			log.error(e);
		}
	}

	private void stopWebServces() {
		try {
			for (Endpoint endpoint : endpoints) {
				endpoint.stop();
			}
		} catch (NullPointerException e) {
			// Bug in JAX-WS - nix zu tun.
		}
	}

	private void publishEndpoint(String ipAddress, String port, String endpointName, Object implementator) {
	   String endpointUrl = createEndpointUrl(ipAddress, port, endpointName);
	   log.debug("Publish endpoint " + endpointName + " under " + endpointUrl);
	   try {
	      endpoints.add(Endpoint.publish(endpointUrl, implementator));
	   } catch (RuntimeException e)  {
	      e.printStackTrace();
	      throw e;
	   }
		
	}

	private String createEndpointUrl(String ipAddress, String port, String endpointName) {
		return "http://" + ipAddress + ":" + port + "/" + endpointName;
	}

}
