package ch.infbr5.sentinel.client.gui;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.Date;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;

import net.miginfocom.swing.MigLayout;
import ch.infbr5.sentinel.client.ApplicationFrameController;
import ch.infbr5.sentinel.client.ApplicationModel;
import ch.infbr5.sentinel.client.ApplicationModelImpl;
import ch.infbr5.sentinel.client.StartupHandler;
import ch.infbr5.sentinel.client.Version;
import ch.infbr5.sentinel.client.config.ConfigurationHelper;
import ch.infbr5.sentinel.client.config.ConfigurationLocalHelper;
import ch.infbr5.sentinel.client.gui.components.AppMenuBar;
import ch.infbr5.sentinel.client.gui.components.checkin.AusweisInfoPanel;
import ch.infbr5.sentinel.client.gui.components.checkin.CheckInModel;
import ch.infbr5.sentinel.client.gui.components.checkin.CheckInModelImpl;
import ch.infbr5.sentinel.client.gui.components.checkin.CheckInTabbedPanels;
import ch.infbr5.sentinel.client.gui.components.ipcam.IpCamaraPane;
import ch.infbr5.sentinel.client.gui.components.journal.dialog.NewGefechtsMeldungDialog;
import ch.infbr5.sentinel.client.gui.components.journal.panel.BewegungsJournalModel;
import ch.infbr5.sentinel.client.gui.components.journal.panel.BewegungsJournalTable;
import ch.infbr5.sentinel.client.gui.components.journal.panel.GefechtsJournalModel;
import ch.infbr5.sentinel.client.gui.components.journal.panel.GefechtsJournalTable;
import ch.infbr5.sentinel.client.gui.components.journal.panel.SystemJournalModel;
import ch.infbr5.sentinel.client.gui.components.journal.panel.SystemJournalTable;
import ch.infbr5.sentinel.client.polling.PollingModelUpdater;
import ch.infbr5.sentinel.client.util.DateUtil;
import ch.infbr5.sentinel.client.util.ServiceHelper;
import ch.infbr5.sentinel.client.wsgen.JournalBewegungsMeldung;
import ch.infbr5.sentinel.client.wsgen.JournalGefechtsMeldung;
import ch.infbr5.sentinel.client.wsgen.JournalSystemMeldung;
import ch.infbr5.sentinel.common.gui.table.FilterTablePanel;

public class ApplicationFrame extends JFrame {

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

		windowListener = new ApplicationFrameController(applicationFrameModel, this);
		this.addWindowListener(windowListener);

		this.startupHandler = new StartupHandler(applicationFrameModel, this);

		this.handleStartupProcess();

		this.initComponents();
	}

	public void displayPersonSelectionDialog() {
		checkInTabbedPanel.displayPersonSelectionDialog();
	}

	private void handleStartupProcess() {
		this.setVisible(false);

		startupHandler.showServerInputIfNeeded();
		startupHandler.showCheckpointChooserIfNeeded();
		startupHandler.setAdminPasswordIfNeeded();

		/*
		 * do { if (!this.startupHandler.showLoginDialogAndSetOperatorName()) {
		 * this.applicationFrameModel.setOperatorName(""); continue; }
		 *
		 * } while (this.applicationFrameModel.getOperatorName() == null ||
		 * this.applicationFrameModel.getOperatorName().equals(""));
		 */

		this.run();
	}

	private String createTitle() {
		return "Sentinel - " + Version.get().getVersion() + " (" + Version.get().getBuildTimestamp() + ") - "
				+ ConfigurationHelper.getCheckpointName();
	}

	private void initComponents() {

		if (this.isInitialized) {
			return;
		}

		// Default
		this.setSize(1024, 900); // Bei Klick auf kleines Fenster
		this.setExtendedState(JFrame.MAXIMIZED_BOTH); // Start in Max-Mode
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setTitle(createTitle());

		checkInModel = new CheckInModelImpl(ConfigurationLocalHelper.getConfig().getCheckpointId(), this);
		checkInTabbedPanel = new CheckInTabbedPanels(checkInModel);

		boolean showCams = false;
		URL[] cams = ConfigurationHelper.getIPCams();
		if (cams.length > 0) {
			showCams = true;
			this.myGlassPane = new IpCamaraPane(60, 2, cams);
			this.setGlassPane(this.myGlassPane);
			this.myGlassPane.setVisible(true);
		}

		JTabbedPane tabbedPane = createTabbedPane();

		// Make all Panes fully hidable
		makePanelHideable(tabbedPane);
		makePanelHideable(checkInTabbedPanel);
		makePanelDefaultHeight(tabbedPane, 200);

		// Layout
		this.getContentPane().setLayout(new MigLayout("", "[fill, grow]", "[fill, grow]"));

		if (showCams) {
			IpCamaraPane ipCamaraPane = new IpCamaraPane(0, 4, cams);
			makePanelDefaultHeight(ipCamaraPane, 400);
			makePanelHideable(ipCamaraPane);

			JSplitPane innerSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, ipCamaraPane, tabbedPane);
			JSplitPane outerSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, checkInTabbedPanel, innerSplitPane);

			this.add(outerSplitPane, "");
		} else {
			JSplitPane outerSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, checkInTabbedPanel, tabbedPane);
			this.add(outerSplitPane, "");
		}

		// Menubar
		this.menuBar = new AppMenuBar(windowListener, ConfigurationLocalHelper.getConfig().isAdminMode(),
				ConfigurationLocalHelper.getConfig().isSuperuserMode());
		setJMenuBar(this.menuBar);

		// Finish
		isInitialized = true;
	}

	private void makePanelHideable(JComponent comp) {
		Dimension dimHidable = new Dimension(0, 0);
		comp.setMinimumSize(dimHidable);
	}

	private void makePanelDefaultHeight(JComponent comp, int size) {
		//comp.setSize(new Dimension(0, 200));
		comp.setPreferredSize(new Dimension(0, size));
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

	public JTabbedPane createTabbedPane() {
		JTabbedPane tabbedPane = new JTabbedPane();

		long currentTimestamp = (new Date()).getTime();
		long hoursInitialLoadInMillis = ConfigurationLocalHelper.getConfig().getHoursInitialLoadJournal() * 60 * 60 * 1000;
		long timestampSeit = currentTimestamp - hoursInitialLoadInMillis;

		List<JournalBewegungsMeldung> bewegungsMeldungen = ServiceHelper.getJournalService()
				.getBewegungsJournalSeit(timestampSeit).getBewegungsMeldungen();
		List<JournalGefechtsMeldung> gefechtsMeldung = ServiceHelper.getJournalService()
				.getGefechtsJournalSeit(timestampSeit).getGefechtsMeldungen();
		List<JournalSystemMeldung> systemMeldungen = ServiceHelper.getJournalService()
				.getSystemJournalSeit(timestampSeit).getSystemMeldungen();

		BewegungsJournalModel modelBewegungsJournal = new BewegungsJournalModel(bewegungsMeldungen);
		GefechtsJournalModel modelGefechtsJournal = new GefechtsJournalModel(gefechtsMeldung);
		SystemJournalModel modelSystemJournal = new SystemJournalModel(systemMeldungen);

		final JTable tableGefecht = new GefechtsJournalTable(modelGefechtsJournal);
		JTable tableBewegung = new BewegungsJournalTable(modelBewegungsJournal);
		JTable tableSystem = new SystemJournalTable(modelSystemJournal);

		JButton additionalButton = new JButton("Neu");
		final JFrame parentframe = this;
		additionalButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				JournalGefechtsMeldung meldung = new JournalGefechtsMeldung();
				meldung.setCheckpoint(ConfigurationLocalHelper.getConfig().getCheckpointWithName());
				meldung.setZeitpunktMeldungsEingang(DateUtil.getCurrentXMLGregorianCalendar());
				NewGefechtsMeldungDialog d = new NewGefechtsMeldungDialog(parentframe, meldung);
				d.setVisible(true);
			}
		});

		tabbedPane.add(new FilterTablePanel(tableGefecht, additionalButton), "Gefechtsmeldungen");
		tabbedPane.add(new FilterTablePanel(tableSystem, null), "Systemmeldungen");
		tabbedPane.add(new FilterTablePanel(tableBewegung, null), "Bewegungsmeldungen");

		checkInModel.setJournalGefechtsModel(modelGefechtsJournal);

		// Model Polling Updater
		PollingModelUpdater poller = new PollingModelUpdater(modelGefechtsJournal, modelSystemJournal, modelBewegungsJournal);
		poller.startKeepUpdated();

		return tabbedPane;
	}

}
