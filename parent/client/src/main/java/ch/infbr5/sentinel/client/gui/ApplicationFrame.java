package ch.infbr5.sentinel.client.gui;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;

import net.miginfocom.swing.MigLayout;
import ch.infbr5.sentinel.client.Version;
import ch.infbr5.sentinel.client.config.ConfigurationHelper;
import ch.infbr5.sentinel.client.config.ConfigurationLocalHelper;
import ch.infbr5.sentinel.client.gui.components.checkin.CheckInModel;
import ch.infbr5.sentinel.client.gui.components.checkin.CheckInTabbedPanels;
import ch.infbr5.sentinel.client.gui.components.ipcam.IpCameraPane;
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

   private ApplicationMenuBar menuBar;

   private final CheckInModel checkInModel;

   private CheckInTabbedPanels checkInTabbedPanel;

   private GefechtsJournalTable tableGefecht;

   private BewegungsJournalTable tableBewegung;

   private SystemJournalTable tableSystem;

   public ApplicationFrame(final String checkpointName, final boolean adminMode, final boolean superUserMode,
         final CheckInModel checkInModel) {
      this.checkInModel = checkInModel;
      initComponents(checkpointName, adminMode, superUserMode);
   }

   public void addActionListenerEinstellungen(final ActionListener listener) {
      menuBar.addActionListenerEinstellungen(listener);
   }

   public void addActionListenerEnableSuperUserMode(final ActionListener listener) {
      menuBar.addActionListenerEnableSuperUserMode(listener);
   }

   public void addActionListenerDisableSuperUserMode(final ActionListener listener) {
      menuBar.addActionListenerDisableSuperUserMode(listener);
   }

   public void addActionListenerEnableAdminMode(final ActionListener listener) {
      menuBar.addActionListenerEnableAdminMode(listener);
   }

   public void addActionListenerDisableAdminMode(final ActionListener listener) {
      menuBar.addActionListenerDisableAdminMode(listener);
   }

   public void addActionListenerClose(final ActionListener listener) {
      menuBar.addActionListenerClose(listener);
   }

   public void addActionListenerManuelleAuswahl(final ActionListener listener) {
      menuBar.addActionListenerManuelleAuswahl(listener);
   }

   public void addActionListenerServerVerbindung(final ActionListener listener) {
      menuBar.addActionListenerServerVerbindung(listener);
   }

   public void addActionListenerCheckpointEinstellung(final ActionListener listener) {
      menuBar.addActionListenerCheckpointEinstellung(listener);
   }

   public void addActionListenerAusweisdatenExportieren(final ActionListener listener) {
      menuBar.addActionListenerAusweisdatenExportieren(listener);
   }

   public void addActionListenerAusweisdatenImportieren(final ActionListener listener) {
      menuBar.addActionListenerAusweisdatenImportieren(listener);
   }

   public void addActionListenerPisaDatenImportieren(final ActionListener listener) {
      menuBar.addActionListenerPisaDatenImportieren(listener);
   }

   public void addActionListenerConfigurationExportieren(final ActionListener listener) {
      menuBar.addActionListenerConfigurationExportieren(listener);
   }

   public void addActionListenerConfigurationImportieren(final ActionListener listener) {
      menuBar.addActionListenerConfigurationImportieren(listener);
   }

   public void showManuelleCheckinAuswahl() {
      checkInTabbedPanel.handleManuellesCheckinEvent();
   }

   public void setSuperUserMode(final boolean mode) {
      menuBar.changeSuperUserMode(mode);
   }

   public void setAdminMode(final boolean mode) {
      menuBar.changeAdminMode(mode);
      tableGefecht.setAdminMode(mode);
      tableBewegung.setAdminMode(mode);
      tableSystem.setAdminMode(mode);
   }

   private void initComponents(final String checkpointName, final boolean adminMode, final boolean superUserMode) {
      setSize(1024, 900);
      setExtendedState(JFrame.MAXIMIZED_BOTH);
      setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      setTitle(createTitle(checkpointName));
      setIconImage(ImageLoader.loadSentinelIcon());

      // Menubar
      menuBar = new ApplicationMenuBar(adminMode, superUserMode);
      setJMenuBar(menuBar);

      checkInTabbedPanel = new CheckInTabbedPanels(checkInModel);

      boolean showCams = false;
      final URL[] cams = ConfigurationHelper.getIPCams();
      if (cams.length > 0) {
         showCams = true;
         final IpCameraPane ipCameraPane = new IpCameraPane(60, 2, cams);
         this.setGlassPane(ipCameraPane);
         ipCameraPane.setVisible(true);
      }

      final JTabbedPane tabbedPane = createTabbedPane(adminMode);

      // Make all Panes fully hidable
      makePanelHideable(tabbedPane);
      makePanelHideable(checkInTabbedPanel);
      makePanelDefaultHeight(tabbedPane, 200);

      // Layout
      this.getContentPane().setLayout(new MigLayout("", "[fill, grow]", "[fill, grow]"));

      if (showCams) {
         final IpCameraPane ipCamaraPane = new IpCameraPane(0, 4, cams);
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
   }

   private void makePanelHideable(final JComponent comp) {
      final Dimension dimHidable = new Dimension(0, 0);
      comp.setMinimumSize(dimHidable);
   }

   private void makePanelDefaultHeight(final JComponent comp, final int size) {
      comp.setPreferredSize(new Dimension(0, size));
   }

   private JTabbedPane createTabbedPane(final boolean adminMode) {
      final JTabbedPane tabbedPane = new JTabbedPane();

      final List<JournalBewegungsMeldung> bewegungsMeldungen = ServiceHelper.getJournalService().getBewegungsJournal()
            .getBewegungsMeldungen();
      final List<JournalGefechtsMeldung> gefechtsMeldung = ServiceHelper.getJournalService().getGefechtsJournal()
            .getGefechtsMeldungen();
      final List<JournalSystemMeldung> systemMeldungen = ServiceHelper.getJournalService().getSystemJournal()
            .getSystemMeldungen();

      final BewegungsJournalModel modelBewegungsJournal = new BewegungsJournalModel(bewegungsMeldungen);
      final GefechtsJournalModel modelGefechtsJournal = new GefechtsJournalModel(gefechtsMeldung);
      final SystemJournalModel modelSystemJournal = new SystemJournalModel(systemMeldungen);

      tableGefecht = new GefechtsJournalTable(modelGefechtsJournal, adminMode);
      tableBewegung = new BewegungsJournalTable(modelBewegungsJournal, adminMode);
      tableSystem = new SystemJournalTable(modelSystemJournal, adminMode);

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

   private String createTitle(final String checkpointName) {
      final StringBuffer buffer = new StringBuffer("Sentinel - ");
      buffer.append(Version.get().getVersion());
      buffer.append(" (");
      buffer.append(Version.get().getBuildTimestamp());
      buffer.append(") - ");
      buffer.append(checkpointName);
      return buffer.toString();
   }

}
