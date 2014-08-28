package ch.infbr5.sentinel.client.gui.components.journal.panel;

import java.util.Date;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import ch.infbr5.sentinel.client.util.PersonDetailsFormater;
import ch.infbr5.sentinel.client.util.XMLGregorianCalendarConverter;
import ch.infbr5.sentinel.client.wsgen.JournalGefechtsMeldung;
import ch.infbr5.sentinel.common.util.DateFormater;

public class GefechtsJournalModel extends AbstractTableModel {

	private static final long serialVersionUID = 1L;

	private String[] columnNames = {"Datum", "Checkpoint", "Wer/Was/Wie/Wo", "Massnahmen", "Für wen?", "Erledigt"};

	private List<JournalGefechtsMeldung> meldungen;

	public GefechtsJournalModel(List<JournalGefechtsMeldung> meldungen) {
		this.meldungen = meldungen;
	}

	public JournalGefechtsMeldung getItem(int row) {
		return meldungen.get(row);
	}

	@Override
	public String getColumnName(int column) {
		return columnNames[column];
	}

	@Override
	public int getRowCount() {
		return meldungen.size();
	}

	@Override
	public int getColumnCount() {
		return columnNames.length;
	}

	public void add(JournalGefechtsMeldung m) {
		this.meldungen.add(0, m);
		fireTableRowsInserted(0, 0);
	}

	@Override
	public Class<?> getColumnClass(int columnIndex) {
		return String.class;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		JournalGefechtsMeldung meldung = meldungen.get(rowIndex);
		if (columnIndex == 0) {
			return DateFormater.formatToDateWithTime(new Date(meldung.getMillis()));
		}
		if (columnIndex == 1) {
			return meldung.getCheckpoint().getName();
		}
		if (columnIndex == 4) {
			return PersonDetailsFormater.getFullName(meldung.getWeiterleitenAnPerson());
		}
		if (columnIndex == 2) {
			return meldung.getWerWasWoWie();
		}
		if (columnIndex == 3) {
			return meldung.getMassnahme();
		}
		if (columnIndex == 5) {
			if (meldung.isIstErledigt()) {
				return DateFormater.formatToDateWithTime(meldung.getZeitpunktErledigt());
			}
			return "";
		}
		return "na";
	}

	public void setGefechtsMeldungToDone(JournalGefechtsMeldung meldung) {
		for (JournalGefechtsMeldung m : meldungen) {
			if (m.getId() == meldung.getId()) {
				m.setIstErledigt(true);
				m.setZeitpunktErledigt(XMLGregorianCalendarConverter.dateToXMLGregorianCalendar(new Date()));
				fireTableDataChanged();
			}
		}
	}

}
