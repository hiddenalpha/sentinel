package ch.infbr5.sentinel.client.gui.components.journal.system;

import ch.infbr5.sentinel.client.gui.components.journal.old.JournalEintragModelImpl;

public class SystemEintragModelImpl extends JournalEintragModelImpl implements SystemEintragModel {
	private String incidenceClass;

	public String getIncidenceClass() {
		return this.incidenceClass;
	}

	public void setIncidenceClass(String incidenceClass) {
		this.incidenceClass = incidenceClass;
	}
}
