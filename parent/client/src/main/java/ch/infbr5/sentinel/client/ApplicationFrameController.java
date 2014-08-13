package ch.infbr5.sentinel.client;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import org.apache.log4j.Logger;

import ch.infbr5.sentinel.client.config.checkpoint.CheckpointConfigurator;
import ch.infbr5.sentinel.client.config.server.ServerConnectionConfigurator;
import ch.infbr5.sentinel.client.gui.ApplicationFrame;
import ch.infbr5.sentinel.client.gui.components.AppMenuBar;
import ch.infbr5.sentinel.client.gui.components.BulkFotoImporter;
import ch.infbr5.sentinel.client.gui.components.FileUpAndDownload;
import ch.infbr5.sentinel.client.gui.components.importer.PersonenImportDialog;

public class ApplicationFrameController implements WindowListener, ActionListener {
	private static Logger log = Logger.getLogger(ApplicationFrameController.class);

	private final ApplicationModel applicationFrameModel;
	private final ApplicationFrame appFrame;

	public ApplicationFrameController(ApplicationModel applicationFrameModel, ApplicationFrame parentFrame) {
		this.applicationFrameModel = applicationFrameModel;
		this.appFrame = parentFrame;
	}

	// ---- Windows Event behandeln ----

	@Override
	public void windowActivated(WindowEvent arg0) {
	}

	@Override
	public void windowClosed(WindowEvent arg0) {
	}

	@Override
	public void windowClosing(WindowEvent arg0) {
		closeApplication();
	}

	@Override
	public void windowDeactivated(WindowEvent arg0) {
	}

	@Override
	public void windowDeiconified(WindowEvent arg0) {
	}

	@Override
	public void windowIconified(WindowEvent arg0) {
	}

	@Override
	public void windowOpened(WindowEvent arg0) {
	}

	// ---- Menubar Event behandeln ----

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

			case AppMenuBar.CMD_IMPORT_PISADATA_BESTAND:
				new PersonenImportDialog(appFrame, true).show();
				break;

			case AppMenuBar.CMD_IMPORT_PISADATA_EINR:
				new PersonenImportDialog(appFrame, false).show();
				break;

			case AppMenuBar.CMD_IMPORT_AUSWEISVORLAGE:
				new FileUpAndDownload(appFrame).importAusweisvorlage();
				break;

			case AppMenuBar.CMD_IMPORT_WASSERZEICHEN:
				new FileUpAndDownload(appFrame).importWasserzeichen();
				break;

			case AppMenuBar.CMD_IMPORT_FOTO:
				new BulkFotoImporter(appFrame).importFotos();
				break;

			case AppMenuBar.CMD_EINSTELLUNGEN:
				AdminstrationFrame.getInstance().setVisible(true);
				break;

			case AppMenuBar.CMD_SERVER_EINSTELLUNG:
				ServerConnectionConfigurator config = new ServerConnectionConfigurator(false, false);
				config.configureServerConfiguration();
				break;

			case AppMenuBar.CMD_CHECKPOINT_EINSTELLUNGEN:
				CheckpointConfigurator confi = new CheckpointConfigurator(false, false);
				confi.configureCheckpointConfiguration();
				break;

			case AppMenuBar.CMD_EXPORT_CONFIG:
				new FileUpAndDownload(appFrame).exportConfiguration();
				break;

			case AppMenuBar.CMD_IMPORT_CONFIG:
				new FileUpAndDownload(appFrame).importConfiguration();
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
