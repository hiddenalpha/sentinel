package ch.infbr5.sentinel.client.gui.components.journal.panel;

import java.sql.Date;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import ch.infbr5.sentinel.client.wsgen.JournalSystemMeldung;
import ch.infbr5.sentinel.common.util.Formater;

public class SystemJournalModel extends AbstractTableModel {

	private static final long serialVersionUID = 1L;

	private String[] columnNames = {"Datum", "Checkpoint", "Level", "Nachricht"};

	private List<JournalSystemMeldung> meldungen;

	public SystemJournalModel(List<JournalSystemMeldung> meldungen) {
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

	public void add(JournalSystemMeldung m) {
		this.meldungen.add(0, m);
		fireTableRowsInserted(0, 0);
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		JournalSystemMeldung meldung = meldungen.get(rowIndex);
		if (columnIndex == 0) {
			return Formater.formatWithTime(new Date(meldung.getMillis()));
		}
		if (columnIndex == 1) {
			if (meldung.getCheckpoint() == null) {
				return "";
			}
			return meldung.getCheckpoint().getName();
		}
		if (columnIndex == 2) {
			return meldung.getLevel();
		}
		if (columnIndex == 3) {
			return meldung.getMessage();
		}
		return "na";
	}

}
