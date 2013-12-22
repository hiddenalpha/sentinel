package ch.infbr5.sentinel.client.gui.components.journal.operator;

import ch.infbr5.sentinel.client.gui.components.journal.old.JournalEintragModelImpl;

public class OperatorEintragModelImpl extends JournalEintragModelImpl implements OperatorEintragModel {
	private String action;
	private String personTriggerId;
	private boolean isDone;

	public String getAction() {
		return this.action;
	}

	public String getPersonTriggerId() {
		return this.personTriggerId;
	}

	public boolean isDone() {
		return this.isDone;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public void setDone(boolean done) {
		this.isDone = done;
	}

	public void setPersonTriggerId(String personTriggerId) {
		this.personTriggerId = personTriggerId;
	}
}
