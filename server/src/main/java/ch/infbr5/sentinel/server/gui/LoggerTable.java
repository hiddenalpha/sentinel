package ch.infbr5.sentinel.server.gui;

import javax.swing.JTable;

public class LoggerTable extends JTable {

	private static final long serialVersionUID = 1L;

	public LoggerTable(final LoggerModel model) {
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

		getColumnModel().getColumn(1).setWidth(80);
		getColumnModel().getColumn(1).setPreferredWidth(80);
		getColumnModel().getColumn(1).setMaxWidth(80);
	}

}
