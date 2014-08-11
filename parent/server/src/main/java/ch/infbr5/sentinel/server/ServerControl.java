package ch.infbr5.sentinel.server;

import java.net.InetAddress;
import java.util.List;

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

	private static final String PORT = "8080";

	private static final Logger log = Logger.getLogger(ServerControl.class);

	private static List<Endpoint> endpoints;

	private NetworkServerControl databaseServer;

	private boolean running = true;

	public ServerControl(boolean debugMode, boolean inMemoryMode) {
		EntityManagerHelper.setDebugMode(debugMode);
		EntityManagerHelper.setInMemoryMode(inMemoryMode);
		endpoints = Lists.newArrayList();
	}

	public void start(String ip) {
		this.startDerby();
		if (ServerSetup.databaseIsEmpty()) {
			ServerSetup.setupDatabase();
		}
		this.startWebServices(ip);
	}

	public void stop() {
		this.stopDerby();
		this.stopWebServces();
		running = false;
	}

	public boolean isRunning() {
		return running;
	}

	private void startDerby() {
		try {
			InetAddress localhost = InetAddress.getLoopbackAddress();
			this.databaseServer = new NetworkServerControl(localhost, 1527, "sentinel", "pwd");
			this.databaseServer.start(null);
		} catch (Exception e) {
			log.error(e);
		}

	}

	private void startWebServices(String ipAddress) {
		try {
			publishEndpoint(ipAddress, "services", new SentinelQueryService());
			publishEndpoint(ipAddress, "configuration", new ConfigurationQueryService());
			publishEndpoint(ipAddress, "journal", new JournalService());
			publishEndpoint(ipAddress, "personenImporter", new PersonenImporterService());
		} catch (Exception e) {
			log.error(e);
		}
	}

	private void stopDerby() {
		try {
			EntityManagerHelper.close();
			this.databaseServer.shutdown();
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

	private void publishEndpoint(String ipAddress, String endpointName, Object implementator) {
		endpoints.add(Endpoint.publish(createEndpointUrl(ipAddress, endpointName), implementator));
	}

	private String createEndpointUrl(String ipAddress, String endpointName) {
		return "http://" + ipAddress + ":" + PORT + "/" + endpointName;
	}

}
