package ch.infbr5.sentinel.client.gui.components.checkin;

import java.util.List;

import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;

import ch.infbr5.sentinel.client.config.ConfigurationLocalHelper;
import ch.infbr5.sentinel.client.util.ServiceHelper;
import ch.infbr5.sentinel.client.wsgen.OperationResponse;
import ch.infbr5.sentinel.client.wsgen.PersonDetails;
import ch.infbr5.sentinel.client.wsgen.PraesenzStatus;

public class CheckInTableModel extends AbstractTableModel implements TableModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String[] columnNames = { "Name", "Vorname", "Funktion", "seit" };
	PraesenzStatus status;
	private List<PersonDetails> data;
	private CheckInModel checkInModel;

	public CheckInTableModel(PraesenzStatus status, CheckInModel checkInModel) {
		this.status = status;
		this.checkInModel = checkInModel;
		this.update();
	}

	public int getColumnCount() {
		return this.columnNames.length;
	}

	@Override
	public String getColumnName(int columnIndex) {
		return this.columnNames[columnIndex];
	}

	public int getRowCount() {
		return this.data.size();
	}

	public Object getValueAt(int rowIndex, int columnIndex) {
		switch (columnIndex) {
		case 0:
			return this.data.get(rowIndex).getName();
		case 1:
			return this.data.get(rowIndex).getVorname();
		case 2:
			return this.data.get(rowIndex).getFunktion();
		case 3:
			return this.data.get(rowIndex).getLastStatusChange();

		default:
			return null;
		}
	}

	public String getBarcodeAtRow(int rowIndex) {
		return this.data.get(rowIndex).getBarcode();
	}

	public void update() {
		OperationResponse resp = ServiceHelper.getSentinelService().getPersonenStatusListe(
				ConfigurationLocalHelper.getConfig().getCheckpointId(), this.status);
		this.data = resp.getPersonDetails();
		this.checkInModel.updateCounter(resp);
		this.fireTableDataChanged();
	}
}
