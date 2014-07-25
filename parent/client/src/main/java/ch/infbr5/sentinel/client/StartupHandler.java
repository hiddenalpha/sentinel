package ch.infbr5.sentinel.client;

import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.xml.ws.WebServiceException;

import ch.infbr5.sentinel.client.config.ConfigurationHelper;
import ch.infbr5.sentinel.client.config.ConfigurationLocalHelper;
import ch.infbr5.sentinel.client.gui.startup.CheckpointSelectionValue;
import ch.infbr5.sentinel.client.gui.startup.LoginDialog;
import ch.infbr5.sentinel.client.util.ServiceHelper;
import ch.infbr5.sentinel.client.wsgen.CheckpointDetails;
import ch.infbr5.sentinel.client.wsgen.ConfigurationResponse;

public class StartupHandler {

	private static Logger logger = Logger.getLogger(StartupHandler.class.getName());

	private final ApplicationModel applicationFrameModel;
	private final JFrame parent;

	public StartupHandler(ApplicationModel applicationFrameModel, JFrame parent) {
		this.applicationFrameModel = applicationFrameModel;
		this.parent = parent;
	}

	private CheckpointSelectionValue[] convertCheckpoints(Object[] checkpoints) {
		CheckpointSelectionValue[] response = new CheckpointSelectionValue[checkpoints.length];

		for (int i = 0; i < checkpoints.length; i++) {
			CheckpointDetails pd = (CheckpointDetails) checkpoints[i];
			CheckpointSelectionValue selectionValue = new CheckpointSelectionValue();
			selectionValue.setId(pd.getId());
			selectionValue.setName(pd.getName());

			response[i] = selectionValue;
		}

		return response;
	}

	public void setAdminPasswordIfNeeded() {
		boolean incomplete = true;

		do {
			String password = ConfigurationHelper.getPassword("admin");
			if ((password != null) && !password.equals("")) {
				incomplete = false;
			}

			if (incomplete) {
				password = JOptionPane.showInputDialog(this.parent, "Bitte Adminpasswort eingeben", "Adminpasswort",
						JOptionPane.INFORMATION_MESSAGE);
				if (password == null || password.isEmpty()) {
					JOptionPane.showMessageDialog(this.parent,
							"Adminpasswort darf nicht leer sein und muss gesetzt werden. Bitte erneut eingeben!",
							"Adminpasswort falsch", JOptionPane.WARNING_MESSAGE);

				} else {
					ConfigurationHelper.setPassword("admin", password);
				}
			}
		} while (incomplete);
	}

	/**
	 * Überprüft das ein Checkpoint configuriert ist. Ansonsten wird ein Auswahl
	 * Dialog angezeigt.
	 */
	public void showCheckpointChooserIfNeeded() {
		boolean incomplete = true;

		do {
			if (ConfigurationLocalHelper.getConfig().getCheckpointId() > -1) {
				incomplete = false;
			}

			if (incomplete) {
				ConfigurationResponse configurationResponse = ServiceHelper.getConfigurationsService().getCheckpoints();
				Object[] checkpoints = configurationResponse.getCheckpointDetails().toArray();
				CheckpointSelectionValue[] values = this.convertCheckpoints(checkpoints);
				CheckpointSelectionValue selected = null;

				if (values.length > 0) {
					selected = (CheckpointSelectionValue) JOptionPane.showInputDialog(this.parent, "Wähle Checkpoint",
							"Checkpoint Auswahl", JOptionPane.WARNING_MESSAGE, null, values, values[0]);
				} else {
					JOptionPane.showMessageDialog(this.parent,
							"Keine Checkpoints auf diesem Server eingerichtet. Bitte Administrator benachrichtigen.", "Warnung",
							JOptionPane.WARNING_MESSAGE);
				}

				if (selected != null) {
					ConfigurationLocalHelper.getConfig().setCheckpointId(selected.getId());
				}
			}

		} while (incomplete);
	}

	public boolean showLoginDialogAndSetOperatorName() {
		LoginDialog loginDialog = new LoginDialog(this.parent);
		loginDialog.setVisible(true);

		boolean succeeded = loginDialog.isSucceeded();
		if (!succeeded) {
			return false;
		}

		this.applicationFrameModel.setOperatorName(loginDialog.getUsername());

		return true;
	}

	/**
	 * Überprüft die Verbindung zum Server (default localhost) und fragt bei
	 * einem Fehlschlagen nach dem Hostname.
	 */
	public void showServerInputIfNeeded() {
		boolean incomplete = true;
		String serverAdress = ConfigurationLocalHelper.getConfig().getEndpointAddress();

		do {

			try {

				ServiceHelper.setEndpointAddress(serverAdress);
				ServiceHelper.getConfigurationsService().getCheckpoints();
				incomplete = false;
				logger.config("Client Startup: Server sucessful connected at "
						+ ConfigurationLocalHelper.getConfig().getEndpointAddress());

			} catch (WebServiceException | MalformedURLException e) {

				logger.log(Level.CONFIG, e.getMessage(), e.getCause());

				JOptionPane.showMessageDialog(this.parent, "Servername nicht erreichbar. Server: "
						+ ConfigurationLocalHelper.getConfig().getEndpointAddress() + ". Bitte Admin benachrichtigen.",
						"Servername nicht erreichbar", JOptionPane.WARNING_MESSAGE);

				String serverName = JOptionPane.showInputDialog(this.parent, "Bitte Servernamen eingeben", "Server",
						JOptionPane.INFORMATION_MESSAGE);
				if ((serverName != null) && !serverName.equals("")) {
					ConfigurationLocalHelper.getConfig().setServerHostname(serverName);
					logger.config("new Server Hostname " + serverName);

				}
			}

			if (isLocalAdress(ConfigurationLocalHelper.getConfig().getServerHostname())) {
				ConfigurationLocalHelper.getConfig().setLocalMode(true);
			} else {
				ConfigurationLocalHelper.getConfig().setLocalMode(false);
			}

		} while (incomplete);

		if (ConfigurationLocalHelper.getConfig().isLocalMode()) {
			String localImagePaht = ServiceHelper.getConfigurationsService().getLocalImagePath();
			ConfigurationLocalHelper.getConfig().setLocalImagePath(localImagePaht);
		}
	}

	private boolean isLocalAdress(String ip) {
		try {
			InetAddress[] thisIp = InetAddress.getAllByName(InetAddress.getLocalHost().getHostName());
			for (int i = 0; i < thisIp.length; i++) {
				if (thisIp[i].getHostAddress().toString().equals(ip)) {
					return true;
				}
			}
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		return false;
	}
}
