package ch.infbr5.sentinel.utils;

import java.util.EventObject;

public class DetailInfoPanelChangedEvent extends EventObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private int selectedRowIndex;

	public DetailInfoPanelChangedEvent(Object obj) {
		super(obj);
	}

	public int getSelectedRowIndex() {
		return this.selectedRowIndex;
	}

	public void setSelectedRowIndex(int selectedRowIndex) {
		this.selectedRowIndex = selectedRowIndex;
	}
}
