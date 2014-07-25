package ch.infbr5.sentinel.client.gui.components.journal.dialog;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;

import net.miginfocom.swing.MigLayout;
import ch.infbr5.sentinel.client.gui.components.journal.panel.GefechtsJournalModel;
import ch.infbr5.sentinel.client.util.ServiceHelper;
import ch.infbr5.sentinel.client.wsgen.JournalGefechtsMeldung;

public class ChangeStatusGefechtsMeldungDialog extends JDialog {

	private static final long serialVersionUID = 1L;

	// UI Components
	private JButton btnSetErledigt;

	private JButton btnCancel;

	private final GefechtsMeldungPanel gefechtsMeldungPanel;

	private JournalGefechtsMeldung meldung;

	public ChangeStatusGefechtsMeldungDialog(JFrame parent, JournalGefechtsMeldung m, final GefechtsJournalModel journalGefechtsModel) {
		super(parent);

		meldung = m;

		setModal(true);
		setTitle("Gefechtsmeldung");
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

		gefechtsMeldungPanel = new GefechtsMeldungPanel(m);
		gefechtsMeldungPanel.deactivate();

		btnSetErledigt = new JButton("Als erledigt markieren");
		btnSetErledigt.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				meldung.setIstErledigt(true);
				ServiceHelper.getJournalService().updateGefechtsMeldung(meldung);
				journalGefechtsModel.setGefechtsMeldungToDone(meldung);
				dispose();
			}
		});

		btnCancel = new JButton("Abbrechen");
		btnCancel.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				gefechtsMeldungPanel.clearFields();
				dispose();
			}
		});

		setLayout(new MigLayout());
		add(gefechtsMeldungPanel, "wrap");
		add(btnSetErledigt, "tag ok, span, split");
		add(btnCancel, "tag cancel");

		pack();
		setLocationRelativeTo(null);
	}

}
