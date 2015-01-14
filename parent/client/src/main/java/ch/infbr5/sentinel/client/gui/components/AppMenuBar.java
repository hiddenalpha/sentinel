package ch.infbr5.sentinel.client.gui.components;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;

public class AppMenuBar extends JMenuBar {

   private static final long serialVersionUID = 1L;

   private boolean superUserMode;
   private boolean adminMode;

   private JMenu menuStart;
   private JMenuItem itemEinstellungen;
   private JMenuItem itemDisableAdmin;
   private JMenuItem itemEnableAdmin;
   private JMenuItem itemDisableSuperuser;
   private JMenuItem itemEnableSuperuser;
   private JMenuItem itemBeenden;

   private JMenu menuCheckpoint;
   private JMenuItem itemManuelleAuswahl;
   private JMenuItem itemServerVerbindung;
   private JMenuItem itemCheckpointEinstellungen;

   private JMenu menuAdmin;
   private JMenuItem itemAusweisdatenExportieren;
   private JMenuItem itemAusweisdatenImportieren;
   private JMenuItem itemPisaDatenImport;
   private JMenuItem itemConfiguractionImport;
   private JMenuItem itemConfigurationExport;

   public AppMenuBar(final boolean adminMode, final boolean superuserMode) {
      createStartMenu();
      createCheckpointMenu();
      createAdminMenu();

      changeModes(adminMode, superuserMode);
   }

   public void addActionListenerEinstellungen(final ActionListener listener) {
      itemEinstellungen.addActionListener(listener);
   }

   public void addActionListenerEnableSuperUseMode(final ActionListener listener) {
      itemEnableSuperuser.addActionListener(listener);
   }

   public void addActionListenerDisableSuperUseMode(final ActionListener listener) {
      itemDisableSuperuser.addActionListener(listener);
   }

   public void addActionListenerEnableAdminMode(final ActionListener listener) {
      itemEnableAdmin.addActionListener(listener);
   }

   public void addActionListenerDisableAdminMode(final ActionListener listener) {
      itemDisableAdmin.addActionListener(listener);
   }

   public void addActionListenerClose(final ActionListener listener) {
      itemBeenden.addActionListener(listener);
   }

   public void addActionListenerManuelleAuswahl(final ActionListener listener) {
      itemManuelleAuswahl.addActionListener(listener);
   }

   public void addActionListenerServerVerbindung(final ActionListener listener) {
      itemServerVerbindung.addActionListener(listener);
   }

   public void addActionListenerCheckpointEinstellung(final ActionListener listener) {
      itemCheckpointEinstellungen.addActionListener(listener);
   }

   public void addActionListenerAusweisdatenExportieren(final ActionListener listener) {
      itemAusweisdatenExportieren.addActionListener(listener);
   }

   public void addActionListenerAusweisdatenImportieren(final ActionListener listener) {
      itemAusweisdatenImportieren.addActionListener(listener);
   }

   public void addActionListenerPisaDatenImportieren(final ActionListener listener) {
      itemPisaDatenImport.addActionListener(listener);
   }

   public void addActionListenerConfigurationExportieren(final ActionListener listener) {
      itemConfigurationExport.addActionListener(listener);
   }

   public void addActionListenerConfigurationImportieren(final ActionListener listener) {
      itemConfiguractionImport.addActionListener(listener);
   }

   private void createStartMenu() {
      menuStart = createMenu("Start", KeyEvent.VK_S);
      add(menuStart);

      itemEinstellungen = createItem("Einstellungen", KeyEvent.VK_E);
      menuStart.add(itemEinstellungen);

      menuStart.addSeparator();

      itemEnableSuperuser = createItem("Superuser Modus starten");
      menuStart.add(itemEnableSuperuser);

      itemDisableSuperuser = createItem("Superuser Modus beenden");
      menuStart.add(itemDisableSuperuser);

      itemEnableAdmin = createItem("Admin Modus starten");
      menuStart.add(itemEnableAdmin);

      itemDisableAdmin = createItem("Admin Modus beenden");
      menuStart.add(itemDisableAdmin);

      menuStart.addSeparator();

      itemBeenden = createItem("Beenden", KeyEvent.VK_B);
      menuStart.add(itemBeenden);
   }

   private void createCheckpointMenu() {
      menuCheckpoint = createMenu("Checkpoint", KeyEvent.VK_C);
      add(menuCheckpoint);

      itemManuelleAuswahl = createItem("Manuelle Auswahl", KeyEvent.VK_F6);
      menuCheckpoint.add(itemManuelleAuswahl);

      menuCheckpoint.addSeparator();

      itemServerVerbindung = createItem("Server-Verbindung");
      menuCheckpoint.add(itemServerVerbindung);

      itemCheckpointEinstellungen = createItem("Checkpoint-Einstellungen");
      menuCheckpoint.add(itemCheckpointEinstellungen);
   }

   private void createAdminMenu() {
      menuAdmin = createMenu("Datenaustausch", KeyEvent.VK_D);
      add(menuAdmin);

      itemAusweisdatenExportieren = createItem("Ausweisdaten exportieren");
      menuAdmin.add(itemAusweisdatenExportieren);

      itemAusweisdatenImportieren = createItem("Ausweisdaten importieren");
      menuAdmin.add(itemAusweisdatenImportieren);

      menuAdmin.addSeparator();

      itemPisaDatenImport = createItem("Pisadaten importieren");
      menuAdmin.add(itemAusweisdatenImportieren);

      this.menuAdmin.addSeparator();

      itemConfigurationExport = createItem("Konfiguration exportieren");
      menuAdmin.add(itemConfigurationExport);

      itemConfiguractionImport = createItem("Konfiguration importieren / bearbeiten");
      menuAdmin.add(itemConfiguractionImport);
   }

   public void changeAdminMode(final boolean mode) {
      changeModes(mode, superUserMode);
   }

   public void changeSuperUserMode(final boolean mode) {
      changeModes(adminMode, mode);
   }

   private void changeModes(final boolean adminMode, final boolean superUserMode) {
      this.adminMode = adminMode;
      this.superUserMode = superUserMode;
      final boolean adminOrSuperuserMode = adminMode || superUserMode;

      if (adminMode) {
         itemEnableSuperuser.setVisible(false);
         itemDisableSuperuser.setVisible(false);
      } else {
         itemEnableSuperuser.setVisible(!superUserMode);
         itemDisableSuperuser.setVisible(superUserMode);
      }
      itemEnableAdmin.setVisible(!adminMode);
      itemDisableAdmin.setVisible(adminMode);

      itemEinstellungen.setEnabled(adminOrSuperuserMode);

      itemAusweisdatenExportieren.setEnabled(adminOrSuperuserMode);
      itemAusweisdatenImportieren.setEnabled(adminOrSuperuserMode);

      itemPisaDatenImport.setEnabled(adminMode);

      itemConfigurationExport.setEnabled(adminMode);
      itemConfiguractionImport.setEnabled(adminMode);

      itemServerVerbindung.setEnabled(adminOrSuperuserMode);
      itemCheckpointEinstellungen.setEnabled(adminOrSuperuserMode);

      menuAdmin.setEnabled(adminOrSuperuserMode);

      revalidate();
   }

   private JMenu createMenu(final String name, final int keyEvent) {
      final JMenu menu = new JMenu(name);
      menu.setMnemonic(keyEvent);
      menu.getAccessibleContext().setAccessibleDescription(name);
      return menu;
   }

   private JMenuItem createItem(final String name) {
      return createItem(name, null);
   }

   private JMenuItem createItem(final String name, final String actionCommand) {
      final JMenuItem item = new JMenuItem(name);
      applyToItem(item, name);
      return item;
   }

   private JMenuItem createItem(final String name, final int keyEvent) {
      final JMenuItem item = new JMenuItem(name, keyEvent);
      item.setAccelerator(KeyStroke.getKeyStroke(keyEvent, ActionEvent.ALT_MASK));
      applyToItem(item, name);
      return item;
   }

   private void applyToItem(final JMenuItem item, final String name) {
      item.getAccessibleContext().setAccessibleDescription(name);
   }

   // TODO Wird in einem anderen Task aufgeräumt
   // private JMenuItem itemFotosImport;
   // itemFotosImport.setEnabled(adminMode);
   // itemFotosImport = addItem("Fotos importieren", menuAdmin, menuListener,
   // CMD_IMPORT_FOTO);

}
