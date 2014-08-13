package ch.infbr5.sentinel.client.gui.components;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.KeyStroke;

import ch.infbr5.sentinel.client.config.ConfigurationHelper;
import ch.infbr5.sentinel.client.config.ConfigurationLocalHelper;

public class AppMenuBar extends JMenuBar {

	public static final String CMD_DISPLAY_PERSON_SELECTION_DLG = "DISPLAY_PERSON_SELECTION_DLG";
	public static final String CMD_RESET_SYSTEM = "RESET_SYSTEM";
	public static final String CMD_EXIT = "EXIT";
	public static final String CMD_EINSTELLUNGEN = "EINSTELLUNGEN";
	public static final String CMD_SERVER_EINSTELLUNG = "SERVER_EINSTELLUNG";
	public static final String CMD_EXPORT_PERSONDATA = "EXPORT_PERSONDATA";
	public static final String CMD_IMPORT_PERSONDATA = "IMPORT_PERSONDATA";
	public static final String CMD_EXPORT_CONFIG = "EXPORT_CONFIG";
	public static final String CMD_IMPORT_CONFIG = "IMPORT_CONFIG";
	public static final String CMD_IMPORT_FOTO = "FOTO_IMPORT";
	public static final String CMD_IMPORT_PISADATA_BESTAND = "IMPORT_PISADATA_BESTAND";
	public static final String CMD_IMPORT_PISADATA_EINR = "IMPORT_PISADATA_EINR";
	public static final String CMD_IMPORT_AUSWEISVORLAGE = "CMD_IMPORT_AUSWEISVORLAGE";
	public static final String CMD_IMPORT_WASSERZEICHEN = "CMD_IMPORT_WASSERZEICHEN";
	public static final String CMD_CHECKPOINT_EINSTELLUNGEN = "CMD_CHECKPOINT_EINSTELLUNGEN";
	public static final String CMD_ENABLED_ADMIN_MODE = "CMD_ENABLED_ADMIN_MODE";
	public static final String CMD_DISABLE_ADMIN_MODE = "CMD_DISABLE_ADMIN_MODE";

	private static final long serialVersionUID = 1L;

	private ActionListener menuListener;

	private boolean adminMode;
	private boolean superUserMode;
	private boolean adminOrSuperuserMode;

	private JMenu menuStart;
	private JMenu menuCheckpoint;
	private JMenu menuAdmin;

	private JMenuItem itemDisableAdmin;
	private JMenuItem itemEnableAdmin;
	private JMenuItem itemDisableSuperuser;
	private JMenuItem itemEnableSuperuser;
	private JMenuItem itemEinstellungen;
	private JMenuItem itemAusweisdatenExportieren;
	private JMenuItem itemAusweisdatenImportieren;
	private JMenuItem itemPisaDatenBestand;
	private JMenuItem itemPisaDatenEinrueckung;
	private JMenuItem itemFotosImport;
	private JMenuItem itemConfiguractionImport;
	private JMenuItem itemConfigurationExport;
	private JMenuItem itemImportAusweisVorlage;
	private JMenuItem itemImportWasserzeichen;

	public AppMenuBar(ActionListener startMenuListener, boolean adminMode, boolean superuserMode) {
		super();

		this.menuListener = startMenuListener;

		this.adminMode = adminMode;
		this.superUserMode = superuserMode;

		this.createStartMenu();
		this.createCheckpointMenu();
		this.createAdminMenu();

		configureActivation();
	}

	private void createStartMenu() {
		menuStart = new JMenu("Start");
		menuStart.setMnemonic(KeyEvent.VK_S);
		menuStart.getAccessibleContext().setAccessibleDescription("Start");
		this.add(menuStart);

		itemEinstellungen = createItem("Einstellungen", CMD_EINSTELLUNGEN, KeyEvent.VK_E);
		itemEinstellungen.addActionListener(menuListener);
		menuStart.add(itemEinstellungen);

		menuStart.addSeparator();

		itemEnableSuperuser = createItem("Superuser Modus starten");
		itemEnableSuperuser.addActionListener(createEnableSuperuserModeListener());
		menuStart.add(itemEnableSuperuser);

		itemDisableSuperuser = createItem("Superuser Modus beenden");
		itemDisableSuperuser.addActionListener(createDisableSuperuserModeListener());
		menuStart.add(itemDisableSuperuser);

		itemEnableAdmin = createItem("Admin Modus starten");
		itemEnableAdmin.addActionListener(createEnableAdminModeListener());
		menuStart.add(itemEnableAdmin);

		itemDisableAdmin = createItem("Admin Modus beenden");
		itemDisableAdmin.addActionListener(createDisableAdminModeListener());
		menuStart.add(itemDisableAdmin);

		menuStart.addSeparator();

		JMenuItem itemBeenden = createItem("Beenden", CMD_EXIT, KeyEvent.VK_B);
		itemBeenden.addActionListener(menuListener);
		menuStart.add(itemBeenden);
	}

	private void createCheckpointMenu() {
		menuCheckpoint = new JMenu("Checkpoint");
		menuCheckpoint.setMnemonic(KeyEvent.VK_C);
		menuCheckpoint.getAccessibleContext().setAccessibleDescription("Checkpoint");
		this.add(menuCheckpoint);

		JMenuItem menuItem = createItem("Manuelle Auswahl", CMD_DISPLAY_PERSON_SELECTION_DLG, KeyEvent.VK_F6);
		menuItem.addActionListener(menuListener);
		menuCheckpoint.add(menuItem);

		menuCheckpoint.addSeparator();

		JMenuItem itemServerVerbindung = createItem("Server-Verbindung", CMD_SERVER_EINSTELLUNG);
		itemServerVerbindung.addActionListener(menuListener);
		menuCheckpoint.add(itemServerVerbindung);

		JMenuItem itemCheckpointEinstellungen = createItem("Checkpoint-Einstellungen", CMD_CHECKPOINT_EINSTELLUNGEN);
		itemCheckpointEinstellungen.addActionListener(menuListener);
		menuCheckpoint.add(itemCheckpointEinstellungen);
	}

	private void createAdminMenu() {
		this.menuAdmin = new JMenu("Datenaustausch");
		this.add(this.menuAdmin);

		itemAusweisdatenExportieren = addItem("Ausweisdaten exportieren", menuAdmin, menuListener, CMD_EXPORT_PERSONDATA);
		itemAusweisdatenImportieren = addItem("Ausweisdaten importieren", menuAdmin, menuListener, CMD_IMPORT_PERSONDATA);

		this.menuAdmin.addSeparator();

		itemPisaDatenBestand = addItem("Pisadaten importieren (Bestandesliste)", menuAdmin, menuListener, CMD_IMPORT_PISADATA_BESTAND);
		itemPisaDatenEinrueckung = addItem("Pisadaten importieren (Einrückungsliste)", menuAdmin, menuListener, CMD_IMPORT_PISADATA_EINR);

		this.menuAdmin.addSeparator();

		itemFotosImport = addItem("Fotos importieren", menuAdmin, menuListener, CMD_IMPORT_FOTO);

		this.menuAdmin.addSeparator();

		itemConfigurationExport = addItem("Configuration exportieren", menuAdmin, menuListener, CMD_EXPORT_CONFIG);
		itemConfiguractionImport = addItem("Configuration importieren", menuAdmin, menuListener, CMD_IMPORT_CONFIG);

		this.menuAdmin.addSeparator();

		itemImportAusweisVorlage = addItem("Ausweisvorlage importieren", menuAdmin, menuListener, CMD_IMPORT_AUSWEISVORLAGE);
		itemImportWasserzeichen = addItem("Wasserzeichen importieren", menuAdmin, menuListener, CMD_IMPORT_WASSERZEICHEN);
	}

	private ActionListener createDisableSuperuserModeListener() {
		return new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				changeSuperUserMode(false);
			}
		};
	}

	private ActionListener createEnableSuperuserModeListener() {
		return new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				String password = ConfigurationHelper.getSuperUserPassword();
				if (askForPassword(password)) {
					changeSuperUserMode(true);
				}
			}
		};
	}

	private ActionListener createEnableAdminModeListener() {
		return new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				String password = ConfigurationHelper.getAdminPassword();
				if (askForPassword(password)) {
					changeAdminMode(true);
				}
			}
		};
	}

	private ActionListener createDisableAdminModeListener() {
		return new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				changeAdminMode(false);
			}
		};
	}

	private void configureActivation() {
		adminOrSuperuserMode = adminMode || superUserMode;

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

		itemPisaDatenBestand.setEnabled(adminMode);
		itemPisaDatenEinrueckung.setEnabled(adminMode);

		itemFotosImport.setEnabled(adminMode);
		itemConfigurationExport.setEnabled(adminMode);
		itemConfiguractionImport.setEnabled(adminMode);
		itemImportAusweisVorlage.setEnabled(adminMode);
		itemImportWasserzeichen.setEnabled(adminMode);

		menuAdmin.setEnabled(adminOrSuperuserMode);
	}


	private void changeSuperUserMode(boolean value) {
		superUserMode = value;
		configureActivation();
		ConfigurationLocalHelper.getConfig().setSuperuserMode(superUserMode);
		revalidate();
	}

	private void changeAdminMode(boolean value) {
		adminMode = value;
		configureActivation();
		ConfigurationLocalHelper.getConfig().setAdminMode(adminMode);
		revalidate();
	}

	private JMenuItem addItem(String text, JMenu menu, ActionListener listener, String cmd) {
		JMenuItem item = new JMenuItem(text);
		item.setActionCommand(cmd);
		item.addActionListener(listener);
		menu.add(item);
		return item;
	}

	private JMenuItem createItem(String name) {
		return createItem(name, null);
	}

	private JMenuItem createItem(String name, String actionCommand) {
		JMenuItem item = new JMenuItem(name);
		applyToItem(item, name, actionCommand);
		return item;
	}

	private JMenuItem createItem(String name, String actionCommand, int keyEvent) {
		JMenuItem item = new JMenuItem(name, keyEvent);
		item.setAccelerator(KeyStroke.getKeyStroke(keyEvent, ActionEvent.ALT_MASK));
		applyToItem(item, name, actionCommand);
		return item;
	}

	private void applyToItem(JMenuItem item, String name, String actionCommand) {
		item.setName(actionCommand);
		item.getAccessibleContext().setAccessibleDescription(name);
		item.setActionCommand(actionCommand);
	}

	private boolean askForPassword(String originalPassword) {
		if (originalPassword == null || originalPassword.isEmpty()) {
			JOptionPane.showMessageDialog(null, "Auf dem Server ist kein Passwort definiert.", "Fehler", JOptionPane.ERROR_MESSAGE);
			return false;
		}

		JPasswordField field = new JPasswordField();
		JLabel txt = new JLabel("Bitte Passwort eingeben");

		Object[] fields = { txt, field };
		JOptionPane.showConfirmDialog(null, fields, "Passwort", JOptionPane.OK_CANCEL_OPTION, JOptionPane.INFORMATION_MESSAGE);
		String adminPassword = String.valueOf(field.getPassword());

		if (adminPassword != null && adminPassword.equals(originalPassword)) {
			return true;
		} else {
			JOptionPane.showMessageDialog(null, "Passwort stimmt nicht korrekt.", "Adminpasswort falsch", JOptionPane.WARNING_MESSAGE);
			return false;
		}
	}
}
