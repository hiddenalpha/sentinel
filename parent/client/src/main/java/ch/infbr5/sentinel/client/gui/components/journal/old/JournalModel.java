package ch.infbr5.sentinel.client.gui.components.journal.old;

import java.util.Vector;


public interface JournalModel {
	public void addJournalEintrag(JournalEintragModel eintrag);

	public void addTableDataChangedListener(TableDataChangeListener l);

	public Vector<JournalEintragModel> getJournalEintraege();

	public JournalEintragModel getJournalEintrag(int selectedRowIndex);
}
