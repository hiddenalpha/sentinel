package ch.infbr5.sentinel.client.gui;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;
import java.util.Date;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;

import net.miginfocom.swing.MigLayout;
import ch.infbr5.sentinel.client.ApplicationFrameController;
import ch.infbr5.sentinel.client.StartupHandler;
import ch.infbr5.sentinel.client.Version;
import ch.infbr5.sentinel.client.config.ConfigurationHelper;
import ch.infbr5.sentinel.client.config.ConfigurationLocalHelper;
import ch.infbr5.sentinel.client.gui.components.AppMenuBar;
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
import ch.infbr5.sentinel.common.gui.util.ImageLoader;

public class ApplicationFrame extends JFrame {

   private static final long serialVersionUID = 1L;

   private IpCamaraPane myGlassPane;

   private AppMenuBar menuBar;

   private boolean isInitialized;

   private final StartupHandler startupHandler;

   private final ApplicationFrameController applicationFrameController;

   private CheckInModel checkInModel;

   private CheckInTabbedPanels checkInTabbedPanel;

   public ApplicationFrame() {
      startupHandler = new StartupHandler();

      applicationFrameController = new ApplicationFrameController(this);
      this.addWindowListener(applicationFrameController.getWindowListener());

      this.handleStartupProcess();
      this.initComponents();
   }

   public void displayPersonSelectionDialog() {
      checkInTabbedPanel.displayPersonSelectionDialog();
   }

   private void handleStartupProcess() {
      this.setVisible(false);
      startupHandler.startConfig();
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
      final URL[] cams = ConfigurationHelper.getIPCams();
      if (cams.length > 0) {
         showCams = true;
         this.myGlassPane = new IpCamaraPane(60, 2, cams);
         this.setGlassPane(this.myGlassPane);
         this.myGlassPane.setVisible(true);
      }

      final JTabbedPane tabbedPane = createTabbedPane();

      // Make all Panes fully hidable
      makePanelHideable(tabbedPane);
      makePanelHideable(checkInTabbedPanel);
      makePanelDefaultHeight(tabbedPane, 200);

      // Layout
      this.getContentPane().setLayout(new MigLayout("", "[fill, grow]", "[fill, grow]"));

      if (showCams) {
         final IpCamaraPane ipCamaraPane = new IpCamaraPane(0, 4, cams);
         makePanelDefaultHeight(ipCamaraPane, 400);
         makePanelHideable(ipCamaraPane);

         final JSplitPane innerSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, ipCamaraPane, tabbedPane);
         final JSplitPane outerSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, checkInTabbedPanel,
               innerSplitPane);

         this.add(outerSplitPane, "");
      } else {
         final JSplitPane outerSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, checkInTabbedPanel, tabbedPane);
         this.add(outerSplitPane, "");
      }

      // Menubar
      this.menuBar = new AppMenuBar(applicationFrameController, ConfigurationLocalHelper.getConfig().isAdminMode(),
            ConfigurationLocalHelper.getConfig().isSuperuserMode());
      setJMenuBar(this.menuBar);

      // Finish
      isInitialized = true;
   }

   private void makePanelHideable(final JComponent comp) {
      final Dimension dimHidable = new Dimension(0, 0);
      comp.setMinimumSize(dimHidable);
   }

   private void makePanelDefaultHeight(final JComponent comp, final int size) {
      // comp.setSize(new Dimension(0, 200));
      comp.setPreferredSize(new Dimension(0, size));
   }

   private void run() {
      this.setVisible(true);
      this.setIconImage(ImageLoader.loadSentinelIcon());
   }

   public JTabbedPane createTabbedPane() {
      final JTabbedPane tabbedPane = new JTabbedPane();

      final long currentTimestamp = (new Date()).getTime();
      final long hoursInitialLoadInMillis = ConfigurationLocalHelper.getConfig().getHoursInitialLoadJournal() * 60 * 60 * 1000;
      final long timestampSeit = currentTimestamp - hoursInitialLoadInMillis;

      final List<JournalBewegungsMeldung> bewegungsMeldungen = ServiceHelper.getJournalService()
            .getBewegungsJournalSeit(timestampSeit).getBewegungsMeldungen();
      final List<JournalGefechtsMeldung> gefechtsMeldung = ServiceHelper.getJournalService()
            .getGefechtsJournalSeit(timestampSeit).getGefechtsMeldungen();
      final List<JournalSystemMeldung> systemMeldungen = ServiceHelper.getJournalService()
            .getSystemJournalSeit(timestampSeit).getSystemMeldungen();

      final BewegungsJournalModel modelBewegungsJournal = new BewegungsJournalModel(bewegungsMeldungen);
      final GefechtsJournalModel modelGefechtsJournal = new GefechtsJournalModel(gefechtsMeldung);
      final SystemJournalModel modelSystemJournal = new SystemJournalModel(systemMeldungen);

      final GefechtsJournalTable tableGefecht = new GefechtsJournalTable(modelGefechtsJournal);
      final BewegungsJournalTable tableBewegung = new BewegungsJournalTable(modelBewegungsJournal);
      final SystemJournalTable tableSystem = new SystemJournalTable(modelSystemJournal);

      final JButton additionalButton = new JButton("Neu");
      final JFrame parentframe = this;
      additionalButton.addActionListener(new ActionListener() {

         @Override
         public void actionPerformed(final ActionEvent e) {
            final JournalGefechtsMeldung meldung = new JournalGefechtsMeldung();
            meldung.setCheckpoint(ConfigurationLocalHelper.getConfig().getCheckpointWithName());
            meldung.setZeitpunktMeldungsEingang(DateUtil.getCurrentXMLGregorianCalendar());
            final NewGefechtsMeldungDialog d = new NewGefechtsMeldungDialog(parentframe, meldung);
            d.setVisible(true);
         }
      });

      tabbedPane.add(new FilterTablePanel(tableGefecht, additionalButton), "Gefechtsmeldungen");
      tabbedPane.add(new FilterTablePanel(tableSystem, null), "Systemmeldungen");
      tabbedPane.add(new FilterTablePanel(tableBewegung, null), "Bewegungsmeldungen");

      tableGefecht.adjust();
      tableBewegung.adjust();
      tableSystem.adjust();

      checkInModel.setJournalGefechtsModel(modelGefechtsJournal);

      // Model Polling Updater
      final PollingModelUpdater poller = new PollingModelUpdater(modelGefechtsJournal, modelSystemJournal,
            modelBewegungsJournal);
      poller.startKeepUpdated();

      return tabbedPane;
   }

}
