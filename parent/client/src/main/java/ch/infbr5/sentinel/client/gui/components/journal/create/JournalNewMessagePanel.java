package ch.infbr5.sentinel.client.gui.components.journal.create;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.KeyboardFocusManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;
import javax.swing.SpinnerDateModel;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;

import net.miginfocom.swing.MigLayout;
import ch.infbr5.sentinel.client.gui.util.SwingHelper;
import ch.infbr5.sentinel.client.util.ConfigurationLocalHelper;
import ch.infbr5.sentinel.client.util.ServiceHelper;
import ch.infbr5.sentinel.client.wsgen.JournalGefechtsMeldung;
import ch.infbr5.sentinel.client.wsgen.OperationResponse;
import ch.infbr5.sentinel.client.wsgen.PersonDetails;

import com.google.common.base.Strings;

public class JournalNewMessagePanel extends JPanel implements ActionListener {

	private static final long serialVersionUID = 1L;

	private JTextArea txtAreaWerWasWieWo;

	private JTextArea txtAreaMassnahmen;

	private JComboBox<CmbItemPerson> cmbMeldungIstFuer;

	private JCheckBox ckbStatus;

	private JButton btnSave;

	private JButton btnCancel;

	private CmbItemPerson emptyItem;

	private JSpinner dateSpinner;

	private JButton btnDateUpdate;

	public JournalNewMessagePanel() {
		setLayout(new MigLayout("inset 20"));

		SwingHelper.addSeparator(this, "Gefechtsmeldung");

		add(SwingHelper.createLabel("Zeitpunkt"), "gap para");

		dateSpinner = new JSpinner( new SpinnerDateModel() );
		dateSpinner.setEditor(new JSpinner.DateEditor(dateSpinner, "dd.MM.yyyy HH:mm"));
		dateSpinner.setValue(new Date());
		add(dateSpinner, "width 100%");

		btnDateUpdate = new JButton("Aktualisieren");
		btnDateUpdate.setPreferredSize(new Dimension(30, 8));
		btnDateUpdate.setFont(new Font("Arial", Font.PLAIN, 9));
		btnDateUpdate.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				dateSpinner.setValue(new Date());
			}
		});
		add(btnDateUpdate, "aligny top, alignx right, wrap");

		add(SwingHelper.createLabel("Wer/Was/Wie/Wo"), "gap para");
		txtAreaWerWasWieWo = SwingHelper.createTextArea(7, 20);
		txtAreaWerWasWieWo.setLineWrap(true);
		txtAreaWerWasWieWo.setWrapStyleWord(true);
		patch(txtAreaWerWasWieWo);
		JScrollPane scrollPane = new JScrollPane(txtAreaWerWasWieWo);
		scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		add(scrollPane, "span, growx, width 100%");

		add(SwingHelper.createLabel("Massnahmen"), "gap para");
		txtAreaMassnahmen = SwingHelper.createTextArea(7, 20);
		txtAreaMassnahmen.setLineWrap(true);
		txtAreaMassnahmen.setWrapStyleWord(true);
		patch(txtAreaMassnahmen);
		JScrollPane scrollPane2 = new JScrollPane(txtAreaMassnahmen);
		scrollPane2.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		add(scrollPane2, "span, growx");

		cmbMeldungIstFuer = new JComboBox<>();
		add(SwingHelper.createLabel("Meldung an "), "gap para");
		add(cmbMeldungIstFuer, "span, growx");

		ckbStatus = new JCheckBox();
		add(SwingHelper.createLabel("Erledigt"), "gap para");
		add(ckbStatus, "span, growx");

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
			if (isDataValid() == null) {
				JournalGefechtsMeldung meldung = new JournalGefechtsMeldung();
				meldung.setCheckpoint(ConfigurationLocalHelper.getConfig().getCheckpoint());
				meldung.setMillis(new Date().getTime());

				try {
					GregorianCalendar c = new GregorianCalendar();
					c.setTime((Date) dateSpinner.getValue());
					meldung.setZeitpunktMeldungsEingang(DatatypeFactory.newInstance().newXMLGregorianCalendar(c));
				} catch (DatatypeConfigurationException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} // TODO
				meldung.setWerWasWoWie(txtAreaWerWasWieWo.getText().trim());
				meldung.setMassnahme(txtAreaMassnahmen.getText().trim());
				CmbItemPerson item = (CmbItemPerson) cmbMeldungIstFuer.getSelectedItem();
				if (item != emptyItem) {
					meldung.setWeiterleitenAnPerson(item.detail);
				}
				meldung.setIstErledigt(ckbStatus.isSelected());

				ServiceHelper.getJournalService().addGefechtsMeldung(meldung);

				clearFields();
			} else {
				JOptionPane.showMessageDialog(this, isDataValid());
			}
		} else if (e.getActionCommand().equals("JOURNALPANEL_CANCEL")) {
			clearFields();
		}
	}

	private void clearFields() {
		txtAreaMassnahmen.setText("");
		txtAreaWerWasWieWo.setText("");
		ckbStatus.setSelected(false);
		cmbMeldungIstFuer.setSelectedItem(emptyItem);
		dateSpinner.setValue(new Date());
	}

	private String isDataValid() {
		if (Strings.isNullOrEmpty(txtAreaWerWasWieWo.getText())) {
			return "Wer/Was/Wie/Wo muss ausfgefüllt sein.";
		}
		if (Strings.isNullOrEmpty(txtAreaMassnahmen.getText())) {
			return "Massnahmen muss ausgefüllt sein.";
		}
		return null;
	}

	    public static void patch(Component c) {
	        Set<KeyStroke>
	        strokes = new HashSet<KeyStroke>(Arrays.asList(KeyStroke.getKeyStroke("pressed TAB")));
	        c.setFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS, strokes);
	        strokes = new HashSet<KeyStroke>(Arrays.asList(KeyStroke.getKeyStroke("shift pressed TAB")));
	        c.setFocusTraversalKeys(KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS, strokes);
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
