package ch.infbr5.sentinel.client.gui.components.configuration;

import java.util.List;

import javax.swing.table.AbstractTableModel;

public abstract class AbstractAdminTableModel<T> extends AbstractTableModel {

	private static final long serialVersionUID = 1L;

	private List<T> data;

	public AbstractAdminTableModel (){
		super();
		updateData();
	}

	@Override
	public String getColumnName(int columnIndex) {
		return getHeaderNames()[columnIndex];
	}

	@Override
	public int getColumnCount() {
		return getHeaderNames().length;
	}

	@Override
	public int getRowCount() {
		return data.size();
	}

	public T getDataRecord(int rowIndex) {
		return data.get(rowIndex);
	}

	public void updateDataRecord(T record){
		updateBackendObject(record);
		updateData();
	}

	public void removeDataRecord(T record) {
		removeBackendObject(record);
		updateData();
	}

	public void updateData() {
		data = getBackendObjects();
		fireTableDataChanged();
	}

	public abstract String[] getHeaderNames();

	public abstract T getNewDataRecord();

	public abstract void removeBackendObject(T object);

	public abstract void updateBackendObject(T object);

	public abstract List<T> getBackendObjects();
}
