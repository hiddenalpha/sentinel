package ch.infbr5.sentinel.server.gui;

import java.util.Date;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import org.apache.log4j.spi.LoggingEvent;

import ch.infbr5.sentinel.common.util.DateFormater;

public class LoggerModel extends AbstractTableModel {

	private static final long serialVersionUID = 1L;

	private String[] columnNames = {"Datum", "Level", "Nachricht"};

	private List<LoggingEvent> events;

	public LoggerModel(List<LoggingEvent> events) {
		this.events = events;
	}

	@Override
	public String getColumnName(int column) {
		return columnNames[column];
	}

	@Override
	public int getRowCount() {
		return events.size();
	}

	@Override
	public int getColumnCount() {
		return columnNames.length;
	}

	public void add(LoggingEvent m) {
		this.events.add(0, m);
		fireTableRowsInserted(0, 0);
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		LoggingEvent meldung = events.get(rowIndex);
		if (columnIndex == 0) {
			return DateFormater.formatToDateWithTime(new Date(meldung.getTimeStamp()));
		}
		if (columnIndex == 1) {
			return meldung.getLevel();
		}
		if (columnIndex == 2) {
			return meldung.getMessage();
		}
		return "na";
	}

}
