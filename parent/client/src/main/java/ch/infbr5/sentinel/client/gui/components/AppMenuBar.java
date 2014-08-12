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
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	private JMenu adminMenu;
	private JMenuItem disableAdminItem;
	private JMenuItem enableAdminItem;
	private JMenuItem propertiesItem;

	public AppMenuBar(ActionListener startMenuListener, boolean adminMode,
			boolean superuserMode) {
		super();

		this.createStartMenu(startMenuListener);
		this.createCheckpointMenu(startMenuListener);
		this.createAdminMenu(startMenuListener, adminMode, superuserMode);

		this.adminMenu.setVisible(adminMode || superuserMode);
		propertiesItem.setEnabled(adminMode || superuserMode);
	}

	private void createAdminMenu(ActionListener startMenuListener,
			boolean adminMode, boolean superuserMode) {
		this.adminMenu = new JMenu("Datenaustausch");
		this.add(this.adminMenu);

		addItem("Ausweisdaten exportieren", adminMenu, startMenuListener,
				CMD_EXPORT_PERSONDATA, adminMode || superuserMode);
		addItem("Ausweisdaten importieren", adminMenu, startMenuListener,
				CMD_IMPORT_PERSONDATA, adminMode || superuserMode);
		this.adminMenu.addSeparator();

		addItem("Pisadaten importieren (Bestandesliste)", adminMenu, startMenuListener,
				CMD_IMPORT_PISADATA_BESTAND, adminMode );
		addItem("Pisadaten importieren (Einrückungsliste)", adminMenu, startMenuListener,
				CMD_IMPORT_PISADATA_EINR, adminMode );
		this.adminMenu.addSeparator();

		addItem("Fotos importieren", adminMenu, startMenuListener,
				CMD_IMPORT_FOTO, adminMode );
		this.adminMenu.addSeparator();

		addItem("Configuration exportieren", adminMenu, startMenuListener,
				CMD_EXPORT_CONFIG, adminMode );

		addItem("Configuration importieren", adminMenu, startMenuListener,
				CMD_IMPORT_CONFIG, adminMode );

		this.adminMenu.addSeparator();

		addItem("Ausweisvorlage importieren", adminMenu, startMenuListener,
				CMD_IMPORT_AUSWEISVORLAGE, adminMode );

		addItem("Wasserzeichen importieren", adminMenu, startMenuListener,
				CMD_IMPORT_WASSERZEICHEN, adminMode );
	}

	private void addItem(String text, JMenu menu, ActionListener listener,
			String cmd, boolean isEnabled) {
		JMenuItem item = new JMenuItem(text);
		item.setActionCommand(cmd);
		item.addActionListener(listener);
		item.setEnabled(isEnabled);
		menu.add(item);
	}

	private void createCheckpointMenu(ActionListener checkpointMenuListener) {
		JMenu menu = new JMenu("Checkpoint");
		menu.setMnemonic(KeyEvent.VK_C);
		menu.getAccessibleContext().setAccessibleDescription("Checkpoint");
		this.add(menu);

		JMenuItem menuItem = new JMenuItem("Manuelle Auswahl", KeyEvent.VK_F6);
		menuItem.setName(CMD_DISPLAY_PERSON_SELECTION_DLG);
		menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F6,
				ActionEvent.ALT_MASK));
		menuItem.setActionCommand(CMD_DISPLAY_PERSON_SELECTION_DLG);
		menuItem.addActionListener(checkpointMenuListener);
		menu.add(menuItem);
	}

	private void createStartMenu(ActionListener startMenuListener) {
		JMenu menu = new JMenu("Start");
		menu.setMnemonic(KeyEvent.VK_S);
		menu.getAccessibleContext().setAccessibleDescription("Start");
		this.add(menu);

		propertiesItem = new JMenuItem("Einstellungen", KeyEvent.VK_E);
		propertiesItem.setName(CMD_EINSTELLUNGEN);
		propertiesItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_E,
				ActionEvent.ALT_MASK));
		propertiesItem.getAccessibleContext().setAccessibleDescription(
				"Einstellungen");
		propertiesItem.setActionCommand(CMD_EINSTELLUNGEN);
		propertiesItem.addActionListener(startMenuListener);
		menu.add(propertiesItem);

		JMenuItem serverVerbindung = new JMenuItem("Server-Verbindung", KeyEvent.VK_V);
		serverVerbindung.setName(CMD_SERVER_EINSTELLUNG);
		serverVerbindung.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V,
				ActionEvent.ALT_MASK));
		serverVerbindung.getAccessibleContext().setAccessibleDescription(
				"Server-Verbindung");
		serverVerbindung.setActionCommand(CMD_SERVER_EINSTELLUNG);
		serverVerbindung.addActionListener(startMenuListener);
		menu.add(serverVerbindung);

		JMenuItem checkpointEinstellungen = new JMenuItem("Checkpoint-Einstellungen", KeyEvent.VK_N);
		checkpointEinstellungen.setName(CMD_CHECKPOINT_EINSTELLUNGEN);
		checkpointEinstellungen.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N,
				ActionEvent.ALT_MASK));
		checkpointEinstellungen.getAccessibleContext().setAccessibleDescription(
				"Checkpoint-Einstellungen");
		checkpointEinstellungen.setActionCommand(CMD_CHECKPOINT_EINSTELLUNGEN);
		checkpointEinstellungen.addActionListener(startMenuListener);
		menu.add(checkpointEinstellungen);

		JMenuItem beendenItem = new JMenuItem("Beenden", KeyEvent.VK_B);
		beendenItem.setName(CMD_EXIT);
		beendenItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_B,
				ActionEvent.ALT_MASK));
		beendenItem.getAccessibleContext().setAccessibleDescription("Beenden");
		beendenItem.setActionCommand(CMD_EXIT);
		beendenItem.addActionListener(startMenuListener);
		menu.add(beendenItem);
	}

	public void disableAdminMode() {
		this.adminMenu.setVisible(false);
		this.enableAdminItem.setVisible(true);
		this.disableAdminItem.setVisible(false);

		this.revalidate();
	}

	public void enableAdminMode(String originalPassword) {
		JPasswordField field = new JPasswordField();
		JLabel txt = new JLabel("Bitte Adminpasswort eingeben");
		Object[] fields = { txt, field };
		JOptionPane.showConfirmDialog(this, fields, "Adminpasswort",
				JOptionPane.OK_CANCEL_OPTION, JOptionPane.INFORMATION_MESSAGE);
		String adminPassword = String.valueOf(field.getPassword());

		if (adminPassword != null && adminPassword.equals(originalPassword)) {
			this.adminMenu.setVisible(true);
			this.enableAdminItem.setVisible(false);
			this.disableAdminItem.setVisible(true);

			this.revalidate();
		} else {
			JOptionPane.showMessageDialog(this,
					"Adminpasswort stimmt nicht überein.",
					"Adminpasswort falsch", JOptionPane.WARNING_MESSAGE);
		}
	}
}
