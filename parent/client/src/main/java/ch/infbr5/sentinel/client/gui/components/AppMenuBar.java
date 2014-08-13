package ch.infbr5.sentinel.client.gui.components;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.KeyStroke;

import ch.infbr5.sentinel.client.config.ConfigurationLocalHelper;
import ch.infbr5.sentinel.client.util.ServiceHelper;
import ch.infbr5.sentinel.common.config.ConfigConstants;

import com.google.common.collect.Lists;

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
	private boolean adminOrSuperuserMode;

	private JMenu menuStart;
	private JMenu menuCheckpoint;
	private JMenu menuAdmin;

	private JMenuItem itemDisableAdmin;
	private JMenuItem itemEnableAdmin;
	private JMenuItem itemEinstellungen;

	private List<JMenuItem> itemsAdmin;

	public AppMenuBar(ActionListener startMenuListener, boolean adminMode, boolean superuserMode) {
		super();

		this.menuListener = startMenuListener;
		this.adminMode = adminMode;
		this.adminOrSuperuserMode = adminMode || superuserMode;

		this.itemsAdmin = Lists.newArrayList();

		this.createStartMenu();
		this.createCheckpointMenu();
		this.createAdminMenu();
	}

	private void createStartMenu() {
		menuStart = new JMenu("Start");
		menuStart.setMnemonic(KeyEvent.VK_S);
		menuStart.getAccessibleContext().setAccessibleDescription("Start");
		this.add(menuStart);

		itemEinstellungen = createItem("Einstellungen", CMD_EINSTELLUNGEN, KeyEvent.VK_E);
		itemEinstellungen.addActionListener(menuListener);
		itemEinstellungen.setEnabled(adminOrSuperuserMode);
		menuStart.add(itemEinstellungen);

		menuStart.addSeparator();

		itemEnableAdmin = createItem("Admin Modus starten", CMD_ENABLED_ADMIN_MODE);
		itemEnableAdmin.addActionListener(createEnableAdminModeListener());
		itemEnableAdmin.setVisible(!adminMode);
		menuStart.add(itemEnableAdmin);

		itemDisableAdmin = createItem("Admin Modus beenden", CMD_DISABLE_ADMIN_MODE);
		itemDisableAdmin.addActionListener(createDisableAdminModeListener());
		itemDisableAdmin.setVisible(adminMode);
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

		itemsAdmin.add(addItem("Ausweisdaten exportieren", menuAdmin, menuListener, CMD_EXPORT_PERSONDATA, adminOrSuperuserMode));
		itemsAdmin.add(addItem("Ausweisdaten importieren", menuAdmin, menuListener, CMD_IMPORT_PERSONDATA, adminOrSuperuserMode));

		this.menuAdmin.addSeparator();

		itemsAdmin.add(addItem("Pisadaten importieren (Bestandesliste)", menuAdmin, menuListener, CMD_IMPORT_PISADATA_BESTAND,
				adminMode));
		itemsAdmin.add(addItem("Pisadaten importieren (Einrückungsliste)", menuAdmin, menuListener, CMD_IMPORT_PISADATA_EINR,
				adminMode));
		this.menuAdmin.addSeparator();

		itemsAdmin.add(addItem("Fotos importieren", menuAdmin, menuListener, CMD_IMPORT_FOTO, adminMode));

		this.menuAdmin.addSeparator();

		itemsAdmin.add(addItem("Configuration exportieren", menuAdmin, menuListener, CMD_EXPORT_CONFIG, adminMode));
		itemsAdmin.add(addItem("Configuration importieren", menuAdmin, menuListener, CMD_IMPORT_CONFIG, adminMode));

		this.menuAdmin.addSeparator();

		itemsAdmin.add(addItem("Ausweisvorlage importieren", menuAdmin, menuListener, CMD_IMPORT_AUSWEISVORLAGE, adminMode));
		itemsAdmin.add(addItem("Wasserzeichen importieren", menuAdmin, menuListener, CMD_IMPORT_WASSERZEICHEN, adminMode));

		this.menuAdmin.setEnabled(adminOrSuperuserMode);
	}

	private JMenuItem addItem(String text, JMenu menu, ActionListener listener, String cmd, boolean isEnabled) {
		JMenuItem item = new JMenuItem(text);
		item.setActionCommand(cmd);
		item.addActionListener(listener);
		item.setEnabled(isEnabled);
		menu.add(item);
		return item;
	}

	private ActionListener createEnableAdminModeListener() {
		return new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (enableAdminMode(ServiceHelper
						.getConfigurationsService()
						.getConfigurationValue(ConfigurationLocalHelper.getConfig().getCheckpointId(), ConfigConstants.ADMIN_PASSWORD)
						.getConfigurationDetails().get(0).getStringValue())) {
					menuAdmin.setEnabled(true);
					itemEnableAdmin.setVisible(false);
					itemDisableAdmin.setVisible(true);
					itemEinstellungen.setEnabled(true);
					for (JMenuItem item : itemsAdmin) {
						item.setEnabled(true);
					}
					ConfigurationLocalHelper.getConfig().setAdminMode(true);
					revalidate();
				}
			}
		};
	}

	private ActionListener createDisableAdminModeListener() {
		return new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				menuAdmin.setEnabled(false);
				itemEnableAdmin.setVisible(true);
				itemDisableAdmin.setVisible(false);
				itemEinstellungen.setEnabled(false);
				for (JMenuItem item : itemsAdmin) {
					item.setEnabled(false);
				}
				ConfigurationLocalHelper.getConfig().setAdminMode(false);
			}
		};
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

	private boolean enableAdminMode(String originalPassword) {
		JPasswordField field = new JPasswordField();
		JLabel txt = new JLabel("Bitte Adminpasswort eingeben");
		Object[] fields = { txt, field };
		JOptionPane.showConfirmDialog(this, fields, "Adminpasswort", JOptionPane.OK_CANCEL_OPTION,
				JOptionPane.INFORMATION_MESSAGE);
		String adminPassword = String.valueOf(field.getPassword());

		if (adminPassword != null && adminPassword.equals(originalPassword)) {
			return true;
		} else {
			JOptionPane.showMessageDialog(this, "Adminpasswort stimmt nicht korrekt.", "Adminpasswort falsch",
					JOptionPane.WARNING_MESSAGE);
			return false;
		}
	}
}
