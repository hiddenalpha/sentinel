package ch.infbr5.sentinel.client.gui.components.journal.system;

import ch.infbr5.sentinel.client.gui.components.journal.old.JournalEintragModel;

public interface SystemEintragModel extends JournalEintragModel {
	public String getIncidenceClass();

	public void setIncidenceClass(String incidenceClass);
}
