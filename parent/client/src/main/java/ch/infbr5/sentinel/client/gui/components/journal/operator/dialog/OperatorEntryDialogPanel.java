package ch.infbr5.sentinel.client.gui.components.journal.operator.dialog;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import net.miginfocom.swing.MigLayout;
import ch.infbr5.sentinel.client.gui.components.checkin.CheckInModel;

public class OperatorEntryDialogPanel extends JPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private JTextField causeTextField;
	private JCheckBox isDoneCheckBox;
	private JTextField actionTextField;

	private final CheckInModel checkInModel;

	private SelectedPersonModel selectedPersonModel;

	public OperatorEntryDialogPanel(CheckInModel checkInModel) {
		this.checkInModel = checkInModel;

		this.setLayout(new MigLayout("wrap 2", "[min!]10px[150px]", ""));

		this.initializeComponents();
	}

	public String getAction() {
		return this.actionTextField.getText();
	}

	public String getCause() {
		return this.causeTextField.getText();
	}

	public String getPersonTriggerId() {
		return this.selectedPersonModel.getBarcode();
	}

	private void initializeComponents() {
		this.add(new JLabel("Aktion:"));
		this.actionTextField = new JTextField("");
		this.add(this.actionTextField, "growx");

		this.add(new JLabel("Person triggern:"));

		JButton selectPersonButton = new JButton("Wähle Person");
		// Add action listener to button
		this.selectedPersonModel = new SelectedPersonModel();
		SelectPersonenFromJournalDialogListener selectPersonenFromJournalDialogListener = new SelectPersonenFromJournalDialogListener(this.checkInModel, this, this.selectedPersonModel);
		selectPersonButton.addActionListener(selectPersonenFromJournalDialogListener);
		this.add(selectPersonButton);

		this.add(new JLabel("Erledigt?"));
		this.isDoneCheckBox = new JCheckBox("", true);
		this.add(this.isDoneCheckBox);

		this.add(new JLabel("Grund:"));
		this.causeTextField = new JTextField("");
		this.add(this.causeTextField, "growx");
	}

	public boolean isDone() {
		return this.isDoneCheckBox.isSelected();
	}
}
