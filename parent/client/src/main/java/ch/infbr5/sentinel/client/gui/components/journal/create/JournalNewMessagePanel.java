package ch.infbr5.sentinel.client.gui.components.journal.create;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Date;
import java.util.List;
import java.util.Vector;

import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import net.miginfocom.swing.MigLayout;
import ch.infbr5.sentinel.client.gui.util.SwingHelper;
import ch.infbr5.sentinel.client.util.ConfigurationLocalHelper;
import ch.infbr5.sentinel.client.util.ServiceHelper;
import ch.infbr5.sentinel.client.wsgen.JournalGefechtsMeldung;
import ch.infbr5.sentinel.client.wsgen.OperationResponse;
import ch.infbr5.sentinel.client.wsgen.PersonDetails;

public class JournalNewMessagePanel extends JPanel implements ActionListener {

	private static final long serialVersionUID = 1L;

	private JTextArea txtAreaMessage;

	private JTextField txtCreator;

	private JComboBox<CmbItemPerson> cmbMeldungIstFuer;

	private JButton btnSave;

	private JButton btnCancel;

	private JCheckBox ckbStatus;

	private CmbItemPerson emptyItem;

	private DefaultListModel<JournalGefechtsMeldung> model;

	public JournalNewMessagePanel(DefaultListModel<JournalGefechtsMeldung> model) {
		this.model = model;

		setLayout(new MigLayout("inset 20"));

		SwingHelper.addSeparator(this, "Journaleintrag");

		txtCreator = SwingHelper.createTextField(30);
		add(SwingHelper.createLabel("Erstellt von"), "gap para");
		add(txtCreator, "span, growx");

		cmbMeldungIstFuer = new JComboBox<>();
		add(SwingHelper.createLabel("Meldung ist für "), "gap para");
		add(cmbMeldungIstFuer, "span, growx");

		ckbStatus = new JCheckBox();
		add(SwingHelper.createLabel("Erledigt"), "gap para");
		add(ckbStatus, "span, growx");

		add(SwingHelper.createLabel("Text"), "gap para");
		txtAreaMessage = SwingHelper.createTextArea(7, 20);
		txtAreaMessage.setLineWrap(true);
		txtAreaMessage.setWrapStyleWord(true);
		JScrollPane scrollPane = new JScrollPane(txtAreaMessage);
		scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		add(scrollPane, "span, growx");

		btnSave = new JButton("Speichern");
		btnSave.addActionListener(this);
		btnSave.setActionCommand("JOURNALPANEL_SAVE");
		add(btnSave, "tag ok, span, split");

		btnCancel = new JButton("Abbrechen");
		btnCancel.addActionListener(this);
		btnCancel.setActionCommand("JOURNALPANEL_CANCEL");
		add(btnCancel, "tag cancel");

		loadData();
	}

	private void loadData() {
		emptyItem = new CmbItemPerson();

		OperationResponse reponse = ServiceHelper.getSentinelService().getAllePersonen();
		Vector<CmbItemPerson> items = convert(reponse.getPersonDetails());
		items.add(0, emptyItem);
		ComboBoxModel<CmbItemPerson> model = new DefaultComboBoxModel<>(items);
		cmbMeldungIstFuer.setModel(model);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand().equals("JOURNALPANEL_SAVE")) {
			if (isDataValid()) {
				JournalGefechtsMeldung meldung = new JournalGefechtsMeldung();
				meldung.setCheckpointId(ConfigurationLocalHelper.getConfig().getCheckpointId());
				meldung.setMillis(new Date().getTime());

				meldung.setDone(ckbStatus.isSelected());
				meldung.setCreator(txtCreator.getText());
				meldung.setText(txtAreaMessage.getText());

				CmbItemPerson item = (CmbItemPerson) cmbMeldungIstFuer.getSelectedItem();
				if (item != emptyItem) {
					meldung.setPersonDetails(item.detail);
				}

				ServiceHelper.getJournalService().addGefechtsMeldung(meldung);
				model.add(0, meldung);

				clearFields();
			}
		} else if (e.getActionCommand().equals("JOURNALPANEL_CANCEL")) {
			clearFields();
		}
	}

	private void clearFields() {
		txtAreaMessage.setText("");
		txtCreator.setText("");
	}

	private boolean isDataValid() {
		return !txtAreaMessage.getText().equals("");
	}

	class CmbItemPerson {

		private PersonDetails detail;

		@Override
		public String toString() {
			if (detail != null) {
				return detail.getName() + " " + detail.getVorname() + " (" + detail.getGrad()  + ")";
			} else {
				return "";
			}
		}

	}

	private Vector<CmbItemPerson> convert(List<PersonDetails> personen) {
		Vector<CmbItemPerson> items = new Vector<>();
		for (PersonDetails detail : personen) {
			CmbItemPerson item = new CmbItemPerson();
			item.detail = detail;
			items.add(item);
		}
		return items;
	}

}
