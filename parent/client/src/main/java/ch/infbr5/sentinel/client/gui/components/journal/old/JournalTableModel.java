package ch.infbr5.sentinel.client.gui.components.journal.old;

import java.util.Vector;

import javax.swing.table.AbstractTableModel;

public class JournalTableModel extends AbstractTableModel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String[] columnNames = {"Erfasser", "Zeit", "WerWasWoWie" };

	private Vector<JournalEintragModel> data;

	public JournalTableModel(Vector<JournalEintragModel> journalEintraege) {
		this.data = journalEintraege;
	}

	/*
	 * JTable uses this method to determine the default renderer/ editor for
	 * each cell. If we didn't implement this method, then the last column would
	 * contain text ("true"/"false"), rather than a check box.
	 */
	public Class getColumnClass(int c) {
		return this.getValueAt(0, c).getClass();
	}

	public int getColumnCount() {
		return this.columnNames.length;
	}

	public String getColumnName(int col) {
		return this.columnNames[col];
	}

	public int getRowCount() {
		return this.data.size();
	}

	public Object getValueAt(int row, int col) {
		return this.data.get(row).get(col);
	}

	/*
	 * Don't need to implement this method unless your table's data can change.
	 */
	public void setValueAt(Object value, int row, int col) {
		this.data.get(row).set(col, value);

		this.fireTableCellUpdated(row, col);
	}
}
