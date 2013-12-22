package ch.infbr5.sentinel.client.gui.components.journal.old;

import java.util.EventListener;

public interface TableDataChangeListener extends EventListener {
	public void tableDataChanged(TableDataChangedEvent e);
}
