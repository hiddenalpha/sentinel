package ch.infbr5.sentinel.client.gui.components.checkin;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTable;

import org.apache.log4j.Logger;

public class CheckInTableContextMenu extends MouseAdapter implements ActionListener {

	private static Logger log = Logger.getLogger(CheckInTableContextMenu.class);

	private static final String CMD_CHECKOUT = "CMD_CHECKOUT";
	private static final String CMD_CHECKIN = "CMD_CHECKIN";
	private static final String CMD_URLAUB = "CMD_URLAUB";
	private static final String CMD_ANMELDEN = "CMD_ANMELDEN";
	private static final String CMD_ABMELDEN = "CMD_ABMELDEN";
	private CheckInModel checkInModel;
	private CheckInTableModel checkInTableModel;
	private JTable table;

	public CheckInTableContextMenu(JTable table, CheckInTableModel checkInTabelModel, CheckInModel checkInModel) {
		this.table = table;
		this.checkInTableModel = checkInTabelModel;
		this.checkInModel = checkInModel;
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		int r = table.rowAtPoint(e.getPoint());
		if (r >= 0 && r < table.getRowCount()) {
			table.setRowSelectionInterval(r, r);
		} else {
			table.clearSelection();
		}

		int rowindex = table.getSelectedRow();
		if (rowindex < 0)
			return;
		if (e.getComponent() instanceof JTable) {
			JPopupMenu popup = new JPopupMenu();
			popup.add(createJMenuItem("CHECKIN", CMD_CHECKIN));
			popup.add(createJMenuItem("CHECKOUT", CMD_CHECKOUT));
			popup.add(createJMenuItem("URLAUB", CMD_URLAUB));
			popup.add(createJMenuItem("ANMELDEN", CMD_ANMELDEN));
			popup.add(createJMenuItem("ABMELDEN", CMD_ABMELDEN));
			popup.show(e.getComponent(), e.getX(), e.getY());
		}

	}

	private JMenuItem createJMenuItem(String name, String cmd) {
		JMenuItem item = new JMenuItem(name);
		item.setActionCommand(cmd);
		item.addActionListener(this);
		return item;
	}

	@Override
	public void actionPerformed(ActionEvent e) {

		if (!e.getActionCommand().isEmpty()) {

			switch (e.getActionCommand()) {

			case CMD_ABMELDEN:
				checkInModel.setOperation(CheckInOperation.ABMELDEN);
				break;

			case CMD_ANMELDEN:
				checkInModel.setOperation(CheckInOperation.ANMELDEN);
				break;

			case CMD_URLAUB:
				checkInModel.setOperation(CheckInOperation.URLAUB);
				break;

			case CMD_CHECKIN:
				checkInModel.setOperation(CheckInOperation.CHECKIN);
				break;

			case CMD_CHECKOUT:
				checkInModel.setOperation(CheckInOperation.CHECKOUT);
				break;

			default:
				log.warn("Command not handled by " + this.getClass().getName() + ": " + e.getActionCommand());
				return;
			}

			String barcode = checkInTableModel.getBarcodeAtRow(table.getSelectedRow());
			checkInModel.handleCheckinEvent(barcode);
		}
	}

}
