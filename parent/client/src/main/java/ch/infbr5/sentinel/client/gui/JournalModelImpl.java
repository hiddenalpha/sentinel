package ch.infbr5.sentinel.client.gui;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import ch.infbr5.sentinel.client.gui.components.journal.old.JournalEintragModel;
import ch.infbr5.sentinel.client.gui.components.journal.old.JournalModel;
import ch.infbr5.sentinel.client.gui.components.journal.old.TableDataChangeListener;
import ch.infbr5.sentinel.utils.JournalEintragLogger;

public class JournalModelImpl implements JournalModel {
	
	private Vector<JournalEintragModel> journalEintraege;
	
	private List<TableDataChangeListener> tableDataChangeListeners = new ArrayList<TableDataChangeListener>();
	
	public JournalModelImpl(JournalEintragLogger journalEintragLogger) {
		this.journalEintraege = new Vector<JournalEintragModel>();
	}
	
	public void addJournalEintrag(JournalEintragModel eintrag) {
		this.journalEintraege.add(eintrag);
	}

	public void addTableDataChangedListener(TableDataChangeListener l) {
		this.tableDataChangeListeners.add(l);
	}

	public Vector<JournalEintragModel> getJournalEintraege() {
		return this.journalEintraege;
	}

	public JournalEintragModel getJournalEintrag(int selectedRowIndex) {
		return this.journalEintraege.get(selectedRowIndex);
	}

}
