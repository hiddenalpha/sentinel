package ch.infbr5.sentinel.client.gui.components.journal.operator.dialog;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import ch.infbr5.sentinel.client.gui.components.checkin.CheckInModel;
import ch.infbr5.sentinel.utils.JournalEintragLogger;

// besserer name??
public class OperatorEntryDialogListener implements ActionListener {
	private final JFrame parent;
	private final JournalEintragLogger journalEintragLogger;
	private final CheckInModel checkInModel;

	public OperatorEntryDialogListener(JFrame parent, JournalEintragLogger journalEintragLogger, CheckInModel checkInModel) {
		this.parent = parent;
		this.journalEintragLogger = journalEintragLogger;
		this.checkInModel = checkInModel;
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand().equals("ADD_OPERATOR_ENTRY")) {
			OperatorEntryDialogPanel operatorEntryDialogPanel = new OperatorEntryDialogPanel(this.checkInModel);
			int answer = JOptionPane
					.showConfirmDialog(this.parent, operatorEntryDialogPanel, "Journaleintrag hinzufügen",
							JOptionPane.OK_CANCEL_OPTION,
							JOptionPane.PLAIN_MESSAGE);
			if (answer == JOptionPane.YES_OPTION) {
				this.journalEintragLogger.addOperatorEintrag(operatorEntryDialogPanel.getCause(), operatorEntryDialogPanel.getAction(), operatorEntryDialogPanel.getPersonTriggerId(), operatorEntryDialogPanel.isDone());
			} else {
				System.out.println("Aborted operator entry");
			}
		} else {
			System.out.println("Command not handled by "
					+ OperatorEntryDialogListener.class.getName() + ": "
					+ e.getActionCommand());
		}
	}
}