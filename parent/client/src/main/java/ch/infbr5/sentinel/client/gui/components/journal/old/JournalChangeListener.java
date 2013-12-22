package ch.infbr5.sentinel.client.gui.components.journal.old;

import java.util.EventListener;


public interface JournalChangeListener extends EventListener {
	public void journalChanged(JournalChangedEvent e);
}
