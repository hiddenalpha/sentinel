package ch.infbr5.sentinel.client.gui.components.journal.operator.dialog;

import javax.swing.JLabel;
import javax.swing.JPanel;

import net.miginfocom.swing.MigLayout;
import ch.infbr5.sentinel.client.util.Formater;
import ch.infbr5.sentinel.client.wsgen.JournalGefechtsMeldung;
import ch.infbr5.sentinel.client.wsgen.PersonDetails;

public class OperatorInfoDialogPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	public OperatorInfoDialogPanel(JournalGefechtsMeldung personTriggerEintrag) {
		this.setLayout(new MigLayout("wrap 2", "[min!]10px[150px]", ""));
		this.initializeComponents(personTriggerEintrag);
	}

	private void initializeComponents(JournalGefechtsMeldung operatorEintrag) {
		this.add(new JLabel("Checkpoint:"));
		JLabel txtCheckpoint = new JLabel(String.valueOf(operatorEintrag.getCheckpoint().getName()));
		this.add(txtCheckpoint, "growx");

		this.add(new JLabel("Meldungszeitpunkt:"));
		JLabel txtMeldungszeitpunkt = new JLabel(Formater.formatWithTime(operatorEintrag.getZeitpunktMeldungsEingang()));
		this.add(txtMeldungszeitpunkt, "growx");

		this.add(new JLabel("Wer/Was/Wie/Wo:"));
		JLabel txtAreaWerWasWieWo = new JLabel(operatorEintrag.getWerWasWoWie().trim());
		this.add(txtAreaWerWasWieWo, "growx");

		this.add(new JLabel("Massnahmen:"));
		JLabel txtAreaMassnahmen = new JLabel(operatorEintrag.getMassnahme().trim());
		this.add(txtAreaMassnahmen, "growx");

		this.add(new JLabel("Meldung für:"));
		JLabel txtMeldungFuer = new JLabel(getFullName(operatorEintrag.getWeiterleitenAnPerson()));
		this.add(txtMeldungFuer, "growx");
	}

	private String getFullName(PersonDetails person) {
		return person.getGrad() + " " + person.getVorname() + " " + person.getName();
	}

}
