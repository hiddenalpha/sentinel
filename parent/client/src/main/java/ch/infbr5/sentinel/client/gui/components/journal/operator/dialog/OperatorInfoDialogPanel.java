package ch.infbr5.sentinel.client.gui.components.journal.operator.dialog;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import net.miginfocom.swing.MigLayout;
import ch.infbr5.sentinel.client.wsgen.OperatorEintrag;

public class OperatorInfoDialogPanel extends JPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public OperatorInfoDialogPanel(OperatorEintrag operatorEintrag) {

		this.setLayout(new MigLayout("wrap 2", "[min!]10px[150px]", ""));

		this.initializeComponents(operatorEintrag);
	}

	private void initializeComponents(OperatorEintrag operatorEintrag) {
		this.add(new JLabel("Aktion:"));
		JTextField actionTextField = new JTextField(operatorEintrag.getAction());
		actionTextField.setEditable(false);
		this.add(actionTextField, "growx");

		this.add(new JLabel("Getriggerte Person:"));
		JTextField triggerPersonIdTextField = new JTextField(operatorEintrag.getPersonTriggerId());
		triggerPersonIdTextField.setEditable(false);
		this.add(triggerPersonIdTextField, "growx");


		this.add(new JLabel("Erledigt?"));
		JCheckBox isDoneCheckBox = new JCheckBox("", operatorEintrag.isDone());
		isDoneCheckBox.setEnabled(false);
		this.add(isDoneCheckBox);

		this.add(new JLabel("Grund:"));
		JTextField causeTextField = new JTextField(operatorEintrag.getCause());
		causeTextField.setEditable(false);
		this.add(causeTextField, "growx");

		this.add(new JLabel("Erfasser:"));
		JTextField creatorTextField = new JTextField(operatorEintrag.getCreator());
		creatorTextField.setEditable(false);
		this.add(creatorTextField, "growx");
	}
}
