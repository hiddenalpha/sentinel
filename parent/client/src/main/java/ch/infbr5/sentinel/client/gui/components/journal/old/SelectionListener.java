package ch.infbr5.sentinel.client.gui.components.journal.old;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import javax.swing.JTable;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import ch.infbr5.sentinel.utils.DetailInfoPanelChangeListener;
import ch.infbr5.sentinel.utils.DetailInfoPanelChangedEvent;


public class SelectionListener implements ListSelectionListener {
	private JTable table;

	private List<DetailInfoPanelChangeListener> detailInfoPanelChangeListeners = new ArrayList<DetailInfoPanelChangeListener>();

	// It is necessary to keep the table since it is not possible
	// to determine the table from the event's source
	public SelectionListener(JTable table) {
		this.table = table;
	}

	public void addDetailInfoPanelChangedListener(
			DetailInfoPanelChangeListener l) {
		this.detailInfoPanelChangeListeners.add(l);
	}

	private void fireDetailInfoPanelChanged(int selectedRowIndex) {
		for (ListIterator<DetailInfoPanelChangeListener> iterator = this.detailInfoPanelChangeListeners
				.listIterator(); iterator.hasNext();) {
			DetailInfoPanelChangeListener l = iterator.next();
			DetailInfoPanelChangedEvent detailInfoPanelChangedEvent = new DetailInfoPanelChangedEvent(
					this);
			detailInfoPanelChangedEvent.setSelectedRowIndex(selectedRowIndex);

			l.detailInfoPanelChanged(detailInfoPanelChangedEvent);
		}
	}

	public void valueChanged(ListSelectionEvent e) {
		if (e.getValueIsAdjusting()) {
			return;
		}

		int selectedRowIndex = this.table.getSelectedRow();

		if (selectedRowIndex >= 0) {
			this.fireDetailInfoPanelChanged(selectedRowIndex);
		}
	}
}
