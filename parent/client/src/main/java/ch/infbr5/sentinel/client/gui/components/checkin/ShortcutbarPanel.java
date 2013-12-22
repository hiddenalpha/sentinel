package ch.infbr5.sentinel.client.gui.components.checkin;

import java.awt.FlowLayout;
import java.awt.KeyboardFocusManager;

import javax.swing.BorderFactory;
import javax.swing.JPanel;

public class ShortcutbarPanel extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private CheckInModel model;

	public ShortcutbarPanel(CheckInModel model) {
		super();
		this.model = model;
		this.initComponents();
	}

	private void initComponents() {
		this.setLayout(new FlowLayout());

		this.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createTitledBorder("Barcode Scanner Modus"),
				BorderFactory.createEmptyBorder(5, 5, 5, 5)));

		this.add(new Shortcut(this.model, CheckInOperation.CHECKIN, "F1"));
		this.add(new Shortcut(this.model, CheckInOperation.CHECKOUT, "F2"));
		this.add(new Shortcut(this.model, CheckInOperation.URLAUB, "F3"));
		this.add(new Shortcut(this.model, CheckInOperation.ANMELDEN, "F4"));
		this.add(new Shortcut(this.model, CheckInOperation.ABMELDEN, "F5"));

		// TODO darf nur einmal installiert werden.
		KeyboardFocusManager manager = KeyboardFocusManager.getCurrentKeyboardFocusManager();
		CheckInBarcodeScanner keyEventDispatcher = new CheckInBarcodeScanner(this.model);
		manager.addKeyEventDispatcher(keyEventDispatcher);
	}

}
