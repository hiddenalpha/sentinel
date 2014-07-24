package ch.infbr5.sentinel.client.gui.components.journal.list;

import java.awt.Component;
import java.util.Date;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;
import javax.swing.border.Border;

import net.miginfocom.swing.MigLayout;
import ch.infbr5.sentinel.client.util.Formater;
import ch.infbr5.sentinel.client.wsgen.JournalBewegungsMeldung;

public class ListCellJournalBewegungsMeldungRenderer<T extends JournalBewegungsMeldung> extends JPanel implements ListCellRenderer<T> {

	private static final long serialVersionUID = 1L;

	private Border raisedbevel;

	private Border loweredbevel;

	private JLabel lblDatumZeit;

	private JLabel lblCheckpoint;

	private JLabel lblStatus;

	private JLabel lblPerson;

	public ListCellJournalBewegungsMeldungRenderer() {
		setLayout(new MigLayout());

		raisedbevel = BorderFactory.createRaisedBevelBorder();
		loweredbevel = BorderFactory.createLoweredBevelBorder();

		lblDatumZeit = new JLabel();
		add(lblDatumZeit);

		lblCheckpoint = new JLabel();
		add(lblCheckpoint);

		lblStatus = new JLabel();
		add(lblStatus, "width 90px");

		lblPerson = new JLabel();
		add(lblPerson);
	}

	@Override
	public Component getListCellRendererComponent(JList<? extends T> list,
			T meldung, int index, boolean isSelected, boolean cellHasFocus) {

		Date dateTime = new Date();
		dateTime.setTime(meldung.getMillis());
		lblDatumZeit.setText(Formater.formatDateTime(dateTime));

		lblCheckpoint.setText(String.valueOf(meldung.getCheckpoint().getName()));

		lblStatus.setText(meldung.getPraesenzStatus());

		lblPerson.setText(Formater.getFullName(meldung.getPerson()));

		if (cellHasFocus) {
			setBorder(loweredbevel);
		} else {
			setBorder(raisedbevel);
		}

		return this;
	}

}
