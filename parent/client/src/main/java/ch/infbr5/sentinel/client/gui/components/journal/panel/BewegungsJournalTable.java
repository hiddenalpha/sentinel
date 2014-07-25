package ch.infbr5.sentinel.client.gui.components.journal.panel;

import javax.swing.JTable;

public class BewegungsJournalTable extends JTable {

	private static final long serialVersionUID = 1L;

	public BewegungsJournalTable(final BewegungsJournalModel model) {
		super(model);

		adjustColumnWidthBewegung();
		setAutoCreateRowSorter(true);
		getRowSorter().toggleSortOrder(0);
		getRowSorter().toggleSortOrder(0);
	}

	public void adjustColumnWidthBewegung() {
		getColumnModel().getColumn(0).setWidth(100);
		getColumnModel().getColumn(0).setPreferredWidth(100);
		getColumnModel().getColumn(0).setMaxWidth(100);

		getColumnModel().getColumn(1).setPreferredWidth(150);
		getColumnModel().getColumn(1).setMaxWidth(150);

		getColumnModel().getColumn(3).setWidth(80);
		getColumnModel().getColumn(3).setPreferredWidth(80);
		getColumnModel().getColumn(3).setMaxWidth(80);
	}

}
