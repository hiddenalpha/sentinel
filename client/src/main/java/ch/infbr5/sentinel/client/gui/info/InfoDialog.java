package ch.infbr5.sentinel.client.gui.info;

import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import net.miginfocom.swing.MigLayout;
import ch.infbr5.sentinel.client.wsgen.SystemInfo;
import ch.infbr5.sentinel.common.gui.util.ImageLoader;

public class InfoDialog extends JDialog {

   private static final long serialVersionUID = 1L;

   private final SystemInfo infoClient;

   private final SystemInfo infoServer;

   public InfoDialog(final JFrame parentFrame, final SystemInfo infoClient, final SystemInfo infoServer) {
      super(parentFrame);
      this.infoClient = infoClient;
      this.infoServer = infoServer;
   }

   public void showDialog() {
      init();
      setVisible(true);
   }

   private void init() {
      setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
      setSize(500, 360);
      setTitle("Info");
      setIconImage(ImageLoader.loadSentinelIcon());
      setLocationRelativeTo(null);
      setModal(true);
      setLayout(new MigLayout());
      setResizable(false);

      add(new JLabel(new ImageIcon(ImageLoader.loadSentinelIcon())), "w 25%");
      final JLabel lbl = new JLabel(
            "<html><b>Sentinel</b> wird von der Infanteriebrigade 5 (InfBr5) entwickelt. <br /><br />Informationen sind verfügbar unter <a href=\"https://github.com/hiddenalpha/sentinel/blob/master/README.md\">http://github.com/hiddenalpha/sentinel/</a></html>");
      lbl.setCursor(new Cursor(Cursor.HAND_CURSOR));
      lbl.addMouseListener(new MouseAdapter() {
         @Override
         public void mouseClicked(final MouseEvent e) {
            try {
               Desktop.getDesktop().browse(new URI("https://github.com/hiddenalpha/sentinel/blob/master/README.md"));
            } catch (URISyntaxException | IOException ex) {
               ex.printStackTrace();
            }
         }
      });
      add(lbl, "wrap,w 75%");

      final JTabbedPane tabbedPane = new JTabbedPane();
      tabbedPane.add("Sentinel-Client", createPanel(infoClient));
      tabbedPane.add("Sentinel-Server", createPanel(infoServer));
      add(tabbedPane, "push, span");
   }

   private JPanel createPanel(final SystemInfo info) {
      final JPanel panel = new JPanel(new MigLayout());

      addLabelKey(panel, "Sentinel-Version");
      addLabelValue(panel, info.getSentinelVersion());

      addLabelKey(panel, "Sentinel-Build");
      addLabelValue(panel, info.getSentinelBuild());

      addLabelKey(panel, "Java-Hersteller");
      addLabelValue(panel, info.getJavaVendor());

      addLabelKey(panel, "Java-Version");
      addLabelValue(panel, info.getJavaVersion());

      addLabelKey(panel, "Java-Home");
      addLabelValue(panel, info.getJavaHome());

      addLabelKey(panel, "OS-Arch");
      addLabelValue(panel, info.getOsArch());

      addLabelKey(panel, "OS-Name");
      addLabelValue(panel, info.getOsName());

      addLabelKey(panel, "OS-Version");
      addLabelValue(panel, info.getOsVersion());

      addLabelKey(panel, "Datenverzeichnis");
      addLabelValue(panel, info.getUserDir());

      return panel;
   }

   private void addLabelKey(final JPanel panel, final String text) {
      panel.add(createLabel(text), "w 25%, growx");
   }

   private void addLabelValue(final JPanel panel, final String text) {
      panel.add(createLabel(text), "w 75%, growx, wrap");
   }

   private JLabel createLabel(final String text) {
      final JLabel lbl = new JLabel(text);
      return lbl;
   }

}
