package ch.infbr5.sentinel.client;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import org.apache.log4j.Logger;

import ch.infbr5.sentinel.client.config.checkpoint.CheckpointConfigurator;
import ch.infbr5.sentinel.client.config.connection.ConnectionConfigurator;
import ch.infbr5.sentinel.client.config.server.ServerConfigurationDialog;
import ch.infbr5.sentinel.client.gui.ApplicationFrame;
import ch.infbr5.sentinel.client.gui.components.AppMenuBar;
import ch.infbr5.sentinel.client.gui.components.FileUpAndDownload;
import ch.infbr5.sentinel.client.gui.components.importer.PersonenImportDialog;
import ch.infbr5.sentinel.client.util.ServiceHelper;

public class ApplicationFrameController implements ActionListener {

	private static Logger log = Logger.getLogger(ApplicationFrameController.class);

	private final ApplicationModel applicationFrameModel;

	private final ApplicationFrame appFrame;

	public ApplicationFrameController(ApplicationModel applicationFrameModel, ApplicationFrame parentFrame) {
		this.applicationFrameModel = applicationFrameModel;
		this.appFrame = parentFrame;
	}

	public WindowAdapter getWindowListener() {
		return new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent arg0) {
				closeApplication();
			}
		};
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (!e.getActionCommand().isEmpty()) {

			switch (e.getActionCommand()) {
			case AppMenuBar.CMD_EXPORT_PERSONDATA:
				new FileUpAndDownload(appFrame).exportPersonData();
				break;

			case AppMenuBar.CMD_IMPORT_PERSONDATA:
				new FileUpAndDownload(appFrame).importPersonData();
				break;

			case AppMenuBar.CMD_IMPORT_PISADATA:
				new PersonenImportDialog(appFrame).show();
				break;

			//case AppMenuBar.CMD_IMPORT_FOTO:
			//	new BulkFotoImporter(appFrame).importFotos();
			//	break;

			case AppMenuBar.CMD_EINSTELLUNGEN:
				AdminstrationFrame.getInstance().setVisible(true);
				break;

			case AppMenuBar.CMD_SERVER_EINSTELLUNG:
				ConnectionConfigurator config = new ConnectionConfigurator(false, false);
				config.configureConnectionConfiguration();
				break;

			case AppMenuBar.CMD_CHECKPOINT_EINSTELLUNGEN:
				CheckpointConfigurator confi = new CheckpointConfigurator(false, false);
				confi.configureCheckpointConfiguration();
				break;

			case AppMenuBar.CMD_EXPORT_CONFIG:
				new FileUpAndDownload(appFrame).exportConfiguration();
				break;

			case AppMenuBar.CMD_IMPORT_CONFIG:
				ServerConfigurationDialog dialog = new ServerConfigurationDialog(appFrame, ServiceHelper.getConfigurationsService().getServerSetupInformation(), false);
				dialog.setVisible(true);
				break;

			case AppMenuBar.CMD_DISPLAY_PERSON_SELECTION_DLG:
				appFrame.displayPersonSelectionDialog();
				break;

			case AppMenuBar.CMD_EXIT:
				closeApplication();
				break;

			default:
				log.error("Command not handled by " + this.getClass().getName() + ": " + e.getActionCommand());
				break;
			}

		}

	}

	private void closeApplication() {
		appFrame.dispose();
		applicationFrameModel.saveConfiguration();
		System.exit(0);
	}
}
