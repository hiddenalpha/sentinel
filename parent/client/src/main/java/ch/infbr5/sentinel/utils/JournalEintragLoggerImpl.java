package ch.infbr5.sentinel.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import ch.infbr5.sentinel.client.gui.components.journal.old.JournalChangeListener;
import ch.infbr5.sentinel.client.gui.components.journal.old.JournalChangedEvent;
import ch.infbr5.sentinel.client.util.ServiceHelper;

public class JournalEintragLoggerImpl implements JournalEintragLogger {

	private List<JournalChangeListener> journalChangeListeners = new ArrayList<JournalChangeListener>();
	private final long checkpointId;
	private final String operatorName;

	public JournalEintragLoggerImpl(long checkpointId, String operatorName) {
		this.checkpointId = checkpointId;
		this.operatorName = operatorName;
	}

	public void addJournalChangedListener(JournalChangeListener l) {
		this.journalChangeListeners.add(l);
	}

	public void addOperatorEintrag(String cause, String action, String personTriggerId, boolean isDone) {
//		if (!ServiceHelper.getJournalService().addOperatorEintrag(cause, action, personTriggerId, isDone, this.checkpointId, this.operatorName)) {
//			// todo: wo loggen?
//			System.out.println("Konnte OperatorEintrag '" + cause + "' nicht hinzufügen.");
//		}

		this.fireJournalChanged();
	}

	public void addSystemEintrag(String cause, Class incidenceClass) {
//		if(!ServiceHelper.getJournalService().addSystemEintrag(cause, incidenceClass.getName(), this.checkpointId)) {
//			// todo: wo loggen?
//			System.out.println("Konnte SystemEintrag '" + cause + "' nicht hinzufügen.");
//		}

		this.fireJournalChanged();
	}

	private void fireJournalChanged() {
		for (ListIterator<JournalChangeListener> iterator = this.journalChangeListeners.listIterator(); iterator.hasNext();) {
			JournalChangeListener l = iterator.next();
			l.journalChanged(new JournalChangedEvent(this));
		}
	}
}
