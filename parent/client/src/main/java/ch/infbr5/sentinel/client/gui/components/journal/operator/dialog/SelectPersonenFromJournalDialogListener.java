package ch.infbr5.sentinel.client.gui.components.journal.operator.dialog;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JOptionPane;
import javax.swing.JPanel;

import ch.infbr5.sentinel.client.gui.components.checkin.CheckInModel;
import ch.infbr5.sentinel.client.gui.components.checkin.CheckInSelectionValue;

public class SelectPersonenFromJournalDialogListener implements ActionListener {
	private final CheckInModel checkInModel;
	private final JPanel parent;
	private final SelectedPersonModel selectedPersonModel;

	public SelectPersonenFromJournalDialogListener(CheckInModel checkInModel, JPanel operatorEntryDialogPanel, SelectedPersonModel selectedPersonModel) {
		this.checkInModel = checkInModel;
		this.parent = operatorEntryDialogPanel;
		this.selectedPersonModel = selectedPersonModel;
	}

	public void actionPerformed(ActionEvent e) {
		this.displayPersonSelectionDialog();
	}

	private void displayPersonSelectionDialog() {
		CheckInSelectionValue[] values = this.checkInModel.getPersonenMitAusweis();

		CheckInSelectionValue selected = null;

		if (values.length > 0) {
			selected = (CheckInSelectionValue) JOptionPane.showInputDialog(
					this.parent, "Manuele Auswahl",
					"Auswahl nur im Notfall (wird protokolliert).",
					JOptionPane.WARNING_MESSAGE, null, values, values[0]);
		} else {
			JOptionPane.showMessageDialog(this.parent, "Keine Ausweise vorhanden",
					"Warnung", JOptionPane.WARNING_MESSAGE);
		}

		if (selected != null) {
			this.selectedPersonModel.setBarcode(selected.getBarcode());
		}
	}
}
