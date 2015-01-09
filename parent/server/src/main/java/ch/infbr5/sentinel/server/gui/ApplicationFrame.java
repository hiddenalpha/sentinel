package ch.infbr5.sentinel.server.gui;

import java.awt.AWTException;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.TrayIcon.MessageType;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

import net.miginfocom.swing.MigLayout;

import org.apache.log4j.Logger;
import org.apache.log4j.spi.LoggingEvent;

import ch.infbr5.sentinel.common.gui.table.FilterTablePanel;
import ch.infbr5.sentinel.common.gui.util.ImageLoader;

public class ApplicationFrame {

   private static Logger log = Logger.getLogger(ApplicationFrame.class);

   public static ApplicationFrame app;

   private final JFrame frame;

   private final LoggerModel loggerModel;

   private final FilterTablePanel filterTablePanel;

   public ApplicationFrame() {
      frame = new JFrame("Sentinel-Server");
      frame.setSize(600, 400);
      frame.setIconImage(loadSentinelIcon());
      frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

      if (isSystemtraySupported()) {
         frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(final WindowEvent e) {
               frame.setVisible(false);
            }
         });
      } else {
         frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(final WindowEvent e) {
               if (closeServerReally()) {
                  frame.dispose();
                  System.exit(0);
               }
            }
         });
      }

      createMenubar();

      loggerModel = new LoggerModel(new ArrayList<LoggingEvent>());
      filterTablePanel = new FilterTablePanel(new LoggerTable(loggerModel), null);

      frame.setLayout(new MigLayout());
      frame.add(filterTablePanel, "push, grow");

      app = this;
   }

   public void show() {
      createSystemtray();
      if (!isSystemtraySupported()) {
         frame.setVisible(true);
      }
   }

   public void addText(final LoggingEvent event) {
      loggerModel.add(event);
   }

   private boolean isSystemtraySupported() {
      // return false;
      return SystemTray.isSupported();
   }

   private void createSystemtray() {
      if (isSystemtraySupported()) {
         final TrayIcon trayIcon = new TrayIcon(loadSentinelIcon());
         trayIcon.displayMessage("Sentinel-Server", "Sentinel Server gestartet.", MessageType.INFO);
         trayIcon.setToolTip("Sentinel-Server");
         trayIcon.setImageAutoSize(true);

         final MenuItem menuCloseServer = new MenuItem("Server beenden");
         menuCloseServer.addActionListener(createCloseListener());

         final MenuItem menuShowServer = new MenuItem("Server anzeigen");
         menuShowServer.addActionListener(createShowListener());

         final PopupMenu popup = new PopupMenu();
         popup.add(menuShowServer);
         popup.add(menuCloseServer);
         trayIcon.setPopupMenu(popup);
         try {
            SystemTray.getSystemTray().add(trayIcon);
         } catch (final AWTException e) {
            log.error(e);
         }
      }
   }

   private void createMenubar() {
      final JMenuItem menuItem = new JMenuItem("Server beenden");
      menuItem.addActionListener(createCloseListener());

      final JMenu menu = new JMenu("Server");
      menu.add(menuItem);

      final JMenuBar menuBar = new JMenuBar();
      menuBar.add(menu);

      frame.setJMenuBar(menuBar);
   }

   private ActionListener createCloseListener() {
      return (new ActionListener() {
         @Override
         public void actionPerformed(final ActionEvent e) {
            if (closeServerReally()) {
               frame.dispose();
               System.exit(0);
            }
         }
      });
   }

   private boolean closeServerReally() {
      final int answer = JOptionPane.showConfirmDialog(frame, "Möchten Sie den Server wirklich ganz beenden?",
            "Server beenden", JOptionPane.YES_NO_OPTION);
      return answer == JOptionPane.YES_OPTION;
   }

   private ActionListener createShowListener() {
      return (new ActionListener() {
         @Override
         public void actionPerformed(final ActionEvent e) {
            frame.setVisible(true);
         }
      });
   }

   private BufferedImage loadSentinelIcon() {
      return ImageLoader.loadSentinelIcon();
   }

}
