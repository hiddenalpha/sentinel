package ch.infbr5.sentinel.client.gui.components.journal.operator;

import ch.infbr5.sentinel.client.gui.components.journal.old.JournalEintragModel;

public interface OperatorEintragModel extends JournalEintragModel {
	public String getAction();

	public String getPersonTriggerId();

	public boolean isDone();

	public void setAction(String action);

	public void setDone(boolean done);

	public void setPersonTriggerId(String personTriggerId);
}
