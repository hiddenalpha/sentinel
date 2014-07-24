package ch.infbr5.sentinel.client.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JTabbedPane;

import net.miginfocom.swing.MigLayout;
import ch.infbr5.sentinel.client.ApplicationFrameController;
import ch.infbr5.sentinel.client.ApplicationModel;
import ch.infbr5.sentinel.client.ApplicationModelImpl;
import ch.infbr5.sentinel.client.StartupHandler;
import ch.infbr5.sentinel.client.Version;
import ch.infbr5.sentinel.client.gui.components.AppMenuBar;
import ch.infbr5.sentinel.client.gui.components.checkin.AusweisInfoPanel;
import ch.infbr5.sentinel.client.gui.components.checkin.CheckInModel;
import ch.infbr5.sentinel.client.gui.components.checkin.CheckInModelImpl;
import ch.infbr5.sentinel.client.gui.components.checkin.CheckInTabbedPanels;
import ch.infbr5.sentinel.client.gui.components.ipcam.IpCamaraPane;
import ch.infbr5.sentinel.client.gui.components.journal.create.JournalNewMessagePanel;
import ch.infbr5.sentinel.client.gui.components.journal.list.JournalBewegungsMeldungsPanel;
import ch.infbr5.sentinel.client.gui.components.journal.list.JournalGefechtsMeldungsPanel;
import ch.infbr5.sentinel.client.polling.UpdateBewegungsJournal;
import ch.infbr5.sentinel.client.polling.UpdateGefechtsJournal;
import ch.infbr5.sentinel.client.util.ConfigurationHelper;
import ch.infbr5.sentinel.client.util.ConfigurationLocalHelper;
import ch.infbr5.sentinel.client.util.ServiceHelper;
import ch.infbr5.sentinel.client.wsgen.JournalBewegungsMeldung;
import ch.infbr5.sentinel.client.wsgen.JournalGefechtsMeldung;
import ch.infbr5.sentinel.client.wsgen.JournalSystemMeldung;

public class ApplicationFrame extends JFrame implements ActionListener {

	private static final long serialVersionUID = 1L;

	private IpCamaraPane myGlassPane;

	private ApplicationModel applicationFrameModel;

	private AppMenuBar menuBar;

	private boolean isInitialized;

	private StartupHandler startupHandler;

	private ApplicationFrameController windowListener;

	private CheckInModel checkInModel;

	private CheckInTabbedPanels checkInTabbedPanel;

	public ApplicationFrame() {
		applicationFrameModel = new ApplicationModelImpl();

		windowListener = new ApplicationFrameController(applicationFrameModel,
				this);
		this.addWindowListener(windowListener);

		this.startupHandler = new StartupHandler(applicationFrameModel, this);

		this.handleStartupProcess();

		this.initComponents();
	}

	public void displayPersonSelectionDialog() {
		checkInTabbedPanel.displayPersonSelectionDialog();
	}

	@Override
	public void actionPerformed(ActionEvent e) {

	}

	private void handleStartupProcess() {
		this.setVisible(false);

		startupHandler.showServerInputIfNeeded();
		startupHandler.showCheckpointChooserIfNeeded();
		startupHandler.setAdminPasswordIfNeeded();

		// do {
		//
		// if (!this.startupHandler.showLoginDialogAndSetOperatorName()) {
		// this.applicationFrameModel.setOperatorName("");
		// continue;
		// }
		//
		// } while (this.applicationFrameModel.getOperatorName() == null ||
		// this.applicationFrameModel.getOperatorName().equals(""));

		this.run();
	}

	private void initComponents() {
		if (this.isInitialized) {
			return;
		}

		this.setSize(1024, 900);

		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setTitle("Sentinel - " + Version.get().getVersion() + " ("
				+ Version.get().getBuildTimestamp() + ") - "
				+ ConfigurationHelper.getCheckpointName());

		this.getContentPane().setLayout(
				new MigLayout("", "[fill, grow][fill, grow]",
						"[50%, fill][50%, grow]"));

		checkInModel = new CheckInModelImpl(ConfigurationLocalHelper
				.getConfig().getCheckpointId(), this);
		checkInTabbedPanel = new CheckInTabbedPanels(checkInModel);

		this.add(checkInTabbedPanel, "cell 0 0 1 2 growy");

		URL[] cams = ConfigurationHelper.getIPCams();
		if (cams.length > 0) {

			IpCamaraPane ipCamaraPane = new IpCamaraPane(0, 4, cams);
			ipCamaraPane.setSize(200, 400);
			this.add(ipCamaraPane, "cell 1 0");

			this.myGlassPane = new IpCamaraPane(60, 2, cams);
			this.setGlassPane(this.myGlassPane);
			this.myGlassPane.setVisible(true);
		}

		JTabbedPane tabbedPane = new JTabbedPane();

		Long checkpointId = ConfigurationLocalHelper.getConfig().getCheckpointId();

		List<JournalSystemMeldung> systemMeldungen = ServiceHelper.getJournalService().getSystemJournal(checkpointId).getSystemMeldungen();
		List<JournalBewegungsMeldung> bewegungsMeldungen = ServiceHelper.getJournalService().getBewegungsJournal(checkpointId).getBewegungsMeldungen();
		List<JournalGefechtsMeldung> gefechtsMeldung = ServiceHelper.getJournalService().getGefechtsJournal(checkpointId).getGefechtsMeldungen();

		DefaultListModel<JournalSystemMeldung> model1 = new DefaultListModel<>();
		for (JournalSystemMeldung m : systemMeldungen) {
			model1.addElement(m);
		}

		final DefaultListModel<JournalBewegungsMeldung> model2 = new DefaultListModel<>();
		for (JournalBewegungsMeldung m : bewegungsMeldungen) {
			model2.addElement(m);
		}

		DefaultListModel<JournalGefechtsMeldung> model3 = new DefaultListModel<>();
		for (JournalGefechtsMeldung m : gefechtsMeldung) {
			model3.addElement(m);
		}

		new UpdateBewegungsJournal(model2);
		new UpdateGefechtsJournal(model3);

		// tabbedPane.add(new JournalPanel<JournalSystemMeldung>(model1), "Systemmeldungen");
		tabbedPane.add(new JournalBewegungsMeldungsPanel<JournalBewegungsMeldung>(model2), "Bewegungsmeldungen");
		tabbedPane.add(new JournalGefechtsMeldungsPanel<JournalGefechtsMeldung>(model3), "Gefechtsmeldungen");
		tabbedPane.add(new JournalNewMessagePanel(), "Neue Meldung erfassen");

		checkInModel.setJournalGefechtsModel(model3);

		this.add(tabbedPane, "cell 1 1");

		this.menuBar = new AppMenuBar(windowListener, ConfigurationLocalHelper
				.getConfig().isAdminMode(), ConfigurationLocalHelper
				.getConfig().isSuperuserMode());

		setJMenuBar(this.menuBar);

		isInitialized = true;
	}

	private void run() {
		this.setVisible(true);
		this.setIcon();
	}

	private void setIcon() {
		BufferedImage defaultImage = null;
		URL imageURL = AusweisInfoPanel.class.getResource("/images/icon.gif");
		try {
			defaultImage = ImageIO.read(imageURL);
		} catch (IOException e) {
			e.printStackTrace();
		}
		this.setIconImage(defaultImage);
	}
}
