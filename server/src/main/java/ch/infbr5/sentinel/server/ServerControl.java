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
		this.startDerby();
		this.startWebServices(ip, port);
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
			this.databaseServer = new NetworkServerControl(localhost, DB_PORT, DB_USER, DB_PW);
			this.databaseServer.start(null);
		} catch (Exception e) {
			log.error(e);
		}

	}

	private void startWebServices(String ipAddress, String port) {
		try {
			publishEndpoint(ipAddress, port, "services", new SentinelQueryService());
			publishEndpoint(ipAddress, port, "configuration", new ConfigurationQueryService());
			publishEndpoint(ipAddress, port, "journal", new JournalService());
			publishEndpoint(ipAddress, port, "personenImporter", new PersonenImporterService());
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

	private void publishEndpoint(String ipAddress, String port, String endpointName, Object implementator) {
		endpoints.add(Endpoint.publish(createEndpointUrl(ipAddress, port, endpointName), implementator));
	}

	private String createEndpointUrl(String ipAddress, String port, String endpointName) {
		return "http://" + ipAddress + ":" + port + "/" + endpointName;
	}

}
