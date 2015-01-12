package ch.infbr5.sentinel.client;

import java.awt.Component;
import java.awt.Frame;

import javax.swing.JFrame;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import ch.infbr5.sentinel.client.gui.components.configuration.AbstractAdminOverviewPanel;
import ch.infbr5.sentinel.client.gui.components.configuration.CheckpointConfigPanel;
import ch.infbr5.sentinel.client.gui.components.configuration.ConfigurationValuePanel;
import ch.infbr5.sentinel.client.gui.components.configuration.EinheitenConfigPanel;
import ch.infbr5.sentinel.client.gui.components.configuration.PersonenConfigPanel;
import ch.infbr5.sentinel.client.gui.components.configuration.PrintConfigPanel;
import ch.infbr5.sentinel.client.gui.components.configuration.ZoneConfigPanel;
import ch.infbr5.sentinel.common.gui.util.ImageLoader;

public class AdminstrationFrame extends JFrame {

   public static final String FRAME_NAME = "AdministraionFrameTabbedPane";

   public static final String TABBED_PANE_NAME = "AdministraionFrameTabbedPane";

   private static final long serialVersionUID = 1L;

   private static AdminstrationFrame instance;

   private JTabbedPane tabbedPane;

   private AdminstrationFrame() {
      initComponents();
   }

   public static AdminstrationFrame getInstance() {
      if (instance == null) {
         instance = new AdminstrationFrame();
         instance.setVisible(false);
      }
      return instance;
   }

   private void initComponents() {
      setTitle("Einstellungen");

      this.setIconImage(ImageLoader.loadSentinelIcon());
      setName(FRAME_NAME);
      setExtendedState(Frame.MAXIMIZED_BOTH);

      tabbedPane = new JTabbedPane();
      tabbedPane.setName(TABBED_PANE_NAME);
      tabbedPane.addChangeListener(new ChangeListener() {
         @Override
         public void stateChanged(final ChangeEvent arg0) {
            final JTabbedPane source = (JTabbedPane) arg0.getSource();
            final Component c = source.getSelectedComponent();
            if (c instanceof AbstractAdminOverviewPanel) {
               final AbstractAdminOverviewPanel<?> a = (AbstractAdminOverviewPanel<?>) c;
               a.updateModel();
            }
         }
      });

      tabbedPane.addTab("Personen", new PersonenConfigPanel(this));
      tabbedPane.addTab("Einheiten", new EinheitenConfigPanel());
      tabbedPane.addTab("Druckaufträge", new PrintConfigPanel());
      tabbedPane.addTab("Checkpoints", new CheckpointConfigPanel());
      tabbedPane.addTab("Zonen", new ZoneConfigPanel());
      tabbedPane.addTab("Konfiguration", new ConfigurationValuePanel());

      this.setContentPane(tabbedPane);
      pack();
   }
}
