package ch.infbr5.sentinel.server;

import java.net.InetAddress;

import javax.xml.ws.Endpoint;

import org.apache.derby.drda.NetworkServerControl;

import ch.infbr5.sentinel.server.ws.JournalService;
import ch.infbr5.sentinel.server.ws.SentinelQueryService;
import ch.infbr5.sentinel.server.ws.admin.ConfigurationQueryService;

import com.sun.net.httpserver.HttpsServer;

public class ServerControl {

	HttpsServer httpsServer;

	private Endpoint servicesEndpoint;
	private Endpoint configurationEndpoint;
	private Endpoint journalEndpoint;

	private boolean running = true;

	private NetworkServerControl databaseServer;

	public ServerControl(boolean debugMode) {
	}

	public void start(String ip) {

		this.startDerby();

		if (ServerSetup.databaseIsEmpty()) {
			ServerSetup.setupDatabase();
		}

		this.startWebServices(ip);

	}

	public void stop() {
		this.stopWebServces();
		this.stopDerby();
		running = false;
	}

	public boolean isRunning() {
		return running;
	}

	private void startDerby() {

		try {
			InetAddress localhost = InetAddress.getLoopbackAddress();
			this.databaseServer = new NetworkServerControl(localhost, 1527,
					"sentinel", "pwd");
			this.databaseServer.start(null);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private void startWebServices(String thisIpAddress) {

		try {

			this.servicesEndpoint = Endpoint.publish("http://" + thisIpAddress
					+ ":8080/services", new SentinelQueryService());
			this.configurationEndpoint = Endpoint.publish("http://" + thisIpAddress
					+ ":8080/configuration", new ConfigurationQueryService());
			this.journalEndpoint = Endpoint.publish("http://" + thisIpAddress
					+ ":8080/journal", new JournalService());

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void stopDerby() {
		try {
			this.databaseServer.shutdown();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void stopWebServces() {
		this.servicesEndpoint.stop();
		this.configurationEndpoint.stop();
		this.journalEndpoint.stop();

		if (this.httpsServer != null) {
			this.httpsServer.stop(0);
		}

	}

}
