package ch.infbr5.sentinel.client.gui.components.journal.dialog;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import net.miginfocom.swing.MigLayout;
import ch.infbr5.sentinel.client.util.ServiceHelper;
import ch.infbr5.sentinel.client.wsgen.JournalGefechtsMeldung;

public class NewGefechtsMeldungDialog extends JDialog {

	private static final long serialVersionUID = 1L;

	// UI Components
	private JButton btnSave;

	private JButton btnCancel;

	private final GefechtsMeldungPanel gefechtsMeldungPanel;

	public NewGefechtsMeldungDialog(JFrame parent, JournalGefechtsMeldung m) {
		super(parent);

		setModal(true);
		setTitle("Gefechtsmeldung erfassen");
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

		gefechtsMeldungPanel = new GefechtsMeldungPanel(m);

		btnSave = new JButton("Speichern");
		btnSave.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (gefechtsMeldungPanel.isDataValid()) {

					JournalGefechtsMeldung meldung = gefechtsMeldungPanel.getGefechtsMeldung();
					ServiceHelper.getJournalService().addGefechtsMeldung(meldung);

					gefechtsMeldungPanel.clearFields();
					dispose();
				} else {
					JOptionPane.showMessageDialog(null, gefechtsMeldungPanel.errorMessage());
				}
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
		add(btnSave, "tag ok, span, split");
		add(btnCancel, "tag cancel");

		pack();
		setLocationRelativeTo(null);
	}

}
