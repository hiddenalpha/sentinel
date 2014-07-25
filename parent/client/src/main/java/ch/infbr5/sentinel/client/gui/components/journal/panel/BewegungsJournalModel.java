package ch.infbr5.sentinel.client.gui.components.journal.panel;

import java.sql.Date;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import ch.infbr5.sentinel.client.util.Formater;
import ch.infbr5.sentinel.client.wsgen.JournalBewegungsMeldung;

public class BewegungsJournalModel extends AbstractTableModel {

	private static final long serialVersionUID = 1L;

	private String[] columnNames = {"Datum", "Checkpoint", "Name", "Aktion"};

	private List<JournalBewegungsMeldung> meldungen;

	public BewegungsJournalModel(List<JournalBewegungsMeldung> meldungen) {
		this.meldungen = meldungen;
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

	public void add(JournalBewegungsMeldung m) {
		this.meldungen.add(0, m);
		fireTableRowsInserted(0, 0);
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		JournalBewegungsMeldung meldung = meldungen.get(rowIndex);
		if (columnIndex == 0) {
			return Formater.formatWithTime(new Date(meldung.getMillis()));
		}
		if (columnIndex == 1) {
			return meldung.getCheckpoint().getName();
		}
		if (columnIndex == 2) {
			return Formater.getFullName(meldung.getPerson());
		}
		if (columnIndex == 3) {
			return meldung.getPraesenzStatus();
		}
		return "na";
	}

}
