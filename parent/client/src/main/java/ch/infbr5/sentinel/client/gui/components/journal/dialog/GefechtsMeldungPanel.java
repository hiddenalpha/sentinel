package ch.infbr5.sentinel.client.gui.components.journal.dialog;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Date;
import java.util.List;
import java.util.Vector;

import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.SpinnerDateModel;
import javax.xml.datatype.XMLGregorianCalendar;

import net.miginfocom.swing.MigLayout;
import ch.infbr5.sentinel.client.config.ConfigurationLocalHelper;
import ch.infbr5.sentinel.client.util.ServiceHelper;
import ch.infbr5.sentinel.client.util.XMLGregorianCalendarConverter;
import ch.infbr5.sentinel.client.wsgen.JournalGefechtsMeldung;
import ch.infbr5.sentinel.client.wsgen.OperationResponse;
import ch.infbr5.sentinel.client.wsgen.PersonDetails;
import ch.infbr5.sentinel.common.gui.util.SwingHelper;
import ch.infbr5.sentinel.common.util.Formater;

import com.google.common.base.Strings;

public class GefechtsMeldungPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	// UI Components
	private JLabel lblZeitpunkt;

	private JSpinner dateSpinner;

	private JButton btnDateUpdate;

	private JLabel lblCheckpointName;

	private JLabel lblCheckpoint;

	private JLabel lblWerWasWieWo;

	private JTextArea txtAreaWerWasWieWo;

	private JLabel lblMassnahmen;

	private JTextArea txtAreaMassnahmen;

	private JLabel lblMeldungAn;

	private JComboBox<CmbItemPerson> cmbMeldungIstFuer;

	private JLabel lblErledigt;

	private JCheckBox ckbStatus;

	private CmbItemPerson emptyItem = new CmbItemPerson();

	public GefechtsMeldungPanel(JournalGefechtsMeldung meldung) {

		lblZeitpunkt = SwingHelper.createLabel("Zeitpunkt");

		dateSpinner = new JSpinner(new SpinnerDateModel());
		dateSpinner.setEditor(new JSpinner.DateEditor(dateSpinner, "dd.MM.yyyy HH:mm"));

		btnDateUpdate = new JButton("Aktualisieren");
		btnDateUpdate.setPreferredSize(new Dimension(30, 8));
		btnDateUpdate.setFont(SwingHelper.smaller(btnDateUpdate.getFont()));
		btnDateUpdate.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				dateSpinner.setValue(new Date());
			}
		});

		lblCheckpointName = SwingHelper.createLabel("Checkpoint");
		lblCheckpoint = SwingHelper.createLabel("");

		lblWerWasWieWo = SwingHelper.createLabel("Wer/Was/Wie/Wo");
		txtAreaWerWasWieWo = createTextArea();

		lblMassnahmen = SwingHelper.createLabel("Massnahmen");
		txtAreaMassnahmen = createTextArea();

		lblMeldungAn = SwingHelper.createLabel("Meldung an ");
		cmbMeldungIstFuer = new JComboBox<>();

		lblErledigt = SwingHelper.createLabel("Erledigt");
		ckbStatus = new JCheckBox();

		setLayout(new MigLayout("inset 20"));

		add(lblZeitpunkt, "gap para");
		add(dateSpinner, "width 100%");
		add(btnDateUpdate, "aligny top, alignx right, wrap");

		add(lblCheckpointName, "gap para");
		add(lblCheckpoint, "wrap");

		add(lblWerWasWieWo, "gap para");
		add(addScrollPane(txtAreaWerWasWieWo), "span, growx, width 100%");

		add(lblMassnahmen, "gap para");
		add(addScrollPane(txtAreaMassnahmen), "span, growx");

		add(lblMeldungAn, "gap para");
		add(cmbMeldungIstFuer, "span, growx");

		add(lblErledigt, "gap para");
		add(ckbStatus, "span, growx");

		fillUpCombobox();

		updateUi(meldung);
	}

	public void updateUi(JournalGefechtsMeldung meldung) {
		dateSpinner.setValue(meldung.getZeitpunktMeldungsEingang().toGregorianCalendar().getTime());
		lblCheckpoint.setText(meldung.getCheckpoint().getName());
		txtAreaWerWasWieWo.setText(meldung.getWerWasWoWie());
		txtAreaMassnahmen.setText(meldung.getMassnahme());
		ckbStatus.setSelected(meldung.isIstErledigt());
		if (meldung.isIstErledigt()) {
			ckbStatus.setText(Formater.formatWithTime(meldung.getZeitpunktErledigt()));
		}
		if (meldung.getWeiterleitenAnPerson() != null) {
			for (int i = 0; i < cmbMeldungIstFuer.getModel().getSize(); i++) {
				CmbItemPerson item = cmbMeldungIstFuer.getModel().getElementAt(i);
				if (item.detail != null) {
					if (item.detail.getId().longValue() == meldung.getWeiterleitenAnPerson().getId()) {
						cmbMeldungIstFuer.setSelectedItem(item);
					}
				}
			}
		}
	}

	public JournalGefechtsMeldung getGefechtsMeldung() {
		JournalGefechtsMeldung meldung = new JournalGefechtsMeldung();
		meldung.setCheckpoint(ConfigurationLocalHelper.getConfig().getCheckpointWithName());
		meldung.setMillis(new Date().getTime());
		meldung.setZeitpunktMeldungsEingang(getZeitpunktsMeldungsEingang());
		meldung.setWerWasWoWie(getWerWasWieWo().trim());
		meldung.setMassnahme(getMassnahmen().trim());
		meldung.setWeiterleitenAnPerson(getWeiterleitenAn());
		meldung.setIstErledigt(istErledigt());
		return meldung;
	}

	public boolean isDataValid() {
		return errorMessage() == null ? true : false;
	}

	public String errorMessage() {
		if (Strings.isNullOrEmpty(txtAreaWerWasWieWo.getText())) {
			return "Wer/Was/Wie/Wo muss ausfgefüllt sein.";
		}
		if (Strings.isNullOrEmpty(txtAreaMassnahmen.getText())) {
			return "Massnahmen muss ausgefüllt sein.";
		}
		return null;
	}

	private XMLGregorianCalendar getZeitpunktsMeldungsEingang() {
		return XMLGregorianCalendarConverter.dateToXMLGregorianCalendar((Date) dateSpinner.getValue());
	}

	private String getMassnahmen() {
		return txtAreaMassnahmen.getText();
	}

	private String getWerWasWieWo() {
		return txtAreaWerWasWieWo.getText();
	}

	private boolean istErledigt() {
		return ckbStatus.isSelected();
	}

	private PersonDetails getWeiterleitenAn() {
		CmbItemPerson item = (CmbItemPerson) cmbMeldungIstFuer.getSelectedItem();
		return item == null ? null : item.detail;
	}

	private void fillUpCombobox() {
		OperationResponse reponse = ServiceHelper.getSentinelService().getAllePersonen();
		Vector<CmbItemPerson> items = convert(reponse.getPersonDetails());
		items.add(0, emptyItem);
		ComboBoxModel<CmbItemPerson> model = new DefaultComboBoxModel<>(items);
		cmbMeldungIstFuer.setModel(model);
	}

	private JTextArea createTextArea() {
		JTextArea txtArea = SwingHelper.createTextArea(7, 20);
		txtArea.setLineWrap(true);
		txtArea.setWrapStyleWord(true);
		SwingHelper.patchKeystrokeTab(txtArea);
		return txtArea;
	}

	private JScrollPane addScrollPane(JTextArea textArea) {
		JScrollPane scrollPane = new JScrollPane(textArea);
		scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		return scrollPane;
	}

	public void clearFields() {
		txtAreaMassnahmen.setText("");
		txtAreaWerWasWieWo.setText("");
		ckbStatus.setSelected(false);
		cmbMeldungIstFuer.setSelectedItem(emptyItem);
		dateSpinner.setValue(new Date());
	}

	class CmbItemPerson {

		private PersonDetails detail;

		@Override
		public String toString() {
			if (detail != null) {
				return detail.getName() + " " + detail.getVorname() + " (" + detail.getGrad() + ")";
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

	public void deactivate() {
		txtAreaMassnahmen.setEnabled(false);
		txtAreaWerWasWieWo.setEnabled(false);
		btnDateUpdate.setEnabled(false);
		cmbMeldungIstFuer.setEnabled(false);
		dateSpinner.setEnabled(false);
		ckbStatus.setEnabled(false);
	}

}
