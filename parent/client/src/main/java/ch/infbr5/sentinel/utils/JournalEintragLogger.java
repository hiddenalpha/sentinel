package ch.infbr5.sentinel.utils;

import ch.infbr5.sentinel.client.gui.components.journal.old.JournalChangeListener;

public interface JournalEintragLogger {
	public void addJournalChangedListener(JournalChangeListener l);

	public void addOperatorEintrag(String cause, String action, String personTriggerId, boolean isDone);

	public void addSystemEintrag(String cause, Class incidenceClass);
}
