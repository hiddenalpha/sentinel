package ch.infbr5.sentinel.client;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import ch.infbr5.sentinel.client.config.ConfigurationLocalHelper;
import ch.infbr5.sentinel.client.config.server.ServerConfiguration;
import ch.infbr5.sentinel.client.gui.startup.CheckpointSelectionValue;
import ch.infbr5.sentinel.client.util.ServiceHelper;
import ch.infbr5.sentinel.client.wsgen.CheckpointDetails;
import ch.infbr5.sentinel.client.wsgen.ConfigurationResponse;

public class StartupHandler {

	private final JFrame parent;

	public StartupHandler(JFrame parent) {
		this.parent = parent;
	}

	public void startConfig() {
		boolean isFirstConfiguration = ConfigurationLocalHelper.isFirstConfiguration();

		ServerConfiguration srvConfig = new ServerConfiguration(isFirstConfiguration);
		srvConfig.configureServerConfiguration();
	}

	/**
	 * Überprüft das ein Checkpoint configuriert ist. Ansonsten wird ein Auswahl
	 * Dialog angezeigt.
	 */
	private void showCheckpointChooserIfNeeded() {
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
}
