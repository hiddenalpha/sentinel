package ch.infbr5.sentinel.client.gui.components.journal.old;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import net.miginfocom.swing.MigLayout;
import ch.infbr5.sentinel.client.gui.components.journal.operator.OperatorEintragModel;
import ch.infbr5.sentinel.client.gui.components.journal.system.SystemEintragModel;
import ch.infbr5.sentinel.utils.DetailInfoPanelChangeListener;
import ch.infbr5.sentinel.utils.DetailInfoPanelChangedEvent;

public class DetailInfoPanel extends JPanel implements
DetailInfoPanelChangeListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JournalModel model;

	public DetailInfoPanel(JournalModel model) {
		super();

		this.setLayout(new MigLayout("wrap 2", "[min!]10px[]", ""));

		this.model = model;
	}

	public void detailInfoPanelChanged(DetailInfoPanelChangedEvent e) {
		int selectedRowIndex = e.getSelectedRowIndex();

		this.refreshData(this.model.getJournalEintrag(selectedRowIndex));
	}

	private void initJournalEintragDetailComponents(
			JournalEintragModel journalEintragModel) {
		this.add(new JLabel("Zeit:"));
		JTextField dateTextField = new JTextField(journalEintragModel.getDate().toString());
		dateTextField.setEditable(false);
		this.add(dateTextField, "growx");

		this.add(new JLabel("Erfasser:"));
		JTextField creatorTextField = new JTextField(journalEintragModel.getCreator());
		creatorTextField.setEditable(false);
		this.add(creatorTextField, "growx");

		this.add(new JLabel("Grund:"));
		JTextField causeTextField = new JTextField(journalEintragModel.getCause());
		causeTextField.setEditable(false);
		this.add(causeTextField, "growx");
	}

	private void initOperatorEintragDetailComponents(
			OperatorEintragModel operatorEintragModel) {
		this.initJournalEintragDetailComponents(operatorEintragModel);

		this.add(new JLabel("Aktion:"));
		JTextField actionTextField = new JTextField(operatorEintragModel.getAction());
		actionTextField.setEditable(false);
		this.add(actionTextField, "growx");

		this.add(new JLabel("PersonTriggerId:"));
		JTextField personTriggerIdTextField = new JTextField(operatorEintragModel.getPersonTriggerId());
		personTriggerIdTextField.setEditable(false);
		this.add(personTriggerIdTextField, "growx");

		this.add(new JLabel("Erledigt?"));
		JCheckBox doneCheckBox = new JCheckBox("", operatorEintragModel.isDone());
		doneCheckBox.setEnabled(false);
		this.add(doneCheckBox, "growx");
	}

	private void initSystemEintragDetailComponents(
			SystemEintragModel systemEintragModel) {
		this.initJournalEintragDetailComponents(systemEintragModel);

		this.add(new JLabel("Klasse:"));
		JTextField incidenceClassTextField = new JTextField(systemEintragModel.getIncidenceClass());
		incidenceClassTextField.setEditable(false);
		this.add(incidenceClassTextField, "growx");
	}

	private void refreshData(JournalEintragModel journalEintragModel) {
		if (journalEintragModel == null) {
			// todo: fehler? ignorieren?
			return;
		}

		this.removeAll();

		if (journalEintragModel instanceof SystemEintragModel) {
			this.initSystemEintragDetailComponents((SystemEintragModel) journalEintragModel);
		} else if (journalEintragModel instanceof OperatorEintragModel) {
			this.initOperatorEintragDetailComponents((OperatorEintragModel) journalEintragModel);
		} else {
			// todo: throw error? ignore?
		}

		this.revalidate();
	}
}
