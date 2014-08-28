package ch.infbr5.sentinel.client.gui.components.journal.panel;

import javax.swing.JTable;

public class SystemJournalTable extends JTable {

	private static final long serialVersionUID = 1L;

	public SystemJournalTable(final SystemJournalModel model) {
		super(model);

		adjustColumnWidthBewegung();
		setAutoCreateRowSorter(true);
		getRowSorter().toggleSortOrder(0);
		getRowSorter().toggleSortOrder(0);
	}

	public void adjustColumnWidthBewegung() {
		getColumnModel().getColumn(0).setPreferredWidth(120);
		getColumnModel().getColumn(0).setMaxWidth(120);

		getColumnModel().getColumn(1).setPreferredWidth(150);
		getColumnModel().getColumn(1).setMaxWidth(150);

		getColumnModel().getColumn(2).setPreferredWidth(80);
		getColumnModel().getColumn(2).setMaxWidth(80);
	}

}
