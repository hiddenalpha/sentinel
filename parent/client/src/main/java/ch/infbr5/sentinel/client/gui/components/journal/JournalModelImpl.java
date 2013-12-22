package ch.infbr5.sentinel.client.gui.components.journal;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.Vector;

import ch.infbr5.sentinel.client.gui.components.journal.old.JournalChangeListener;
import ch.infbr5.sentinel.client.gui.components.journal.old.JournalChangedEvent;
import ch.infbr5.sentinel.client.gui.components.journal.old.JournalEintragModel;
import ch.infbr5.sentinel.client.gui.components.journal.old.JournalModel;
import ch.infbr5.sentinel.client.gui.components.journal.old.TableDataChangeListener;
import ch.infbr5.sentinel.client.gui.components.journal.old.TableDataChangedEvent;
import ch.infbr5.sentinel.client.gui.components.journal.system.SystemEintragModelImpl;
import ch.infbr5.sentinel.utils.JournalEintragLogger;

public class JournalModelImpl implements JournalModel, JournalChangeListener {
	private Vector<JournalEintragModel> journalEintraege;
	private List<TableDataChangeListener> tableDataChangeListeners = new ArrayList<TableDataChangeListener>();
	private final long checkpointId;

	public JournalModelImpl(JournalEintragLogger journalEintragLogger, long checkpointId) {
		this.checkpointId = checkpointId;
		journalEintragLogger.addJournalChangedListener(this);

		this.journalEintraege = new Vector<JournalEintragModel>();

		this.updateJournalEintraege();
	}

	public void addJournalEintrag(JournalEintragModel eintrag) {
		this.journalEintraege.add(eintrag);
	}

	public void addTableDataChangedListener(TableDataChangeListener l) {
		this.tableDataChangeListeners.add(l);
	}

	private void createDummyEintrag() {
		SystemEintragModelImpl systemEintragModel = new SystemEintragModelImpl();
		systemEintragModel.setCause("Init");
		systemEintragModel.setCreator("System");
		systemEintragModel.setIncidenceClass(JournalModelImpl.class.getName());

		this.journalEintraege.add(systemEintragModel);
	}

	private void fireTableDataChanged() {
		for (ListIterator<TableDataChangeListener> iterator = this.tableDataChangeListeners
				.listIterator(); iterator.hasNext();) {
			TableDataChangeListener l = iterator.next();
			l.tableDataChanged(new TableDataChangedEvent(this));
		}
	}

	public Vector<JournalEintragModel> getJournalEintraege() {
		if (this.journalEintraege.size() <= 0) {
			this.createDummyEintrag();
		}

		return this.journalEintraege;
	}

	public JournalEintragModel getJournalEintrag(int selectedRowIndex) {
		return this.journalEintraege.get(selectedRowIndex);
	}

	public void journalChanged(JournalChangedEvent e) {
//		this.updateJournalEintraege();
//
//		this.fireTableDataChanged();
	}

	private void updateJournalEintraege() {
//		this.journalEintraege.clear();
//
//		JournalResponse journalSystemResponse = ServiceHelper.getJournalService().getSystemEintraege(this.checkpointId);
//		List<SystemEintragDetails> systemEintraege = journalSystemResponse.getSystemEintraege();
//		for (int i = 0; i < systemEintraege.size(); i++) {
//			SystemEintragDetails systemEintragDetails = systemEintraege.get(i);
//
//			SystemEintragModel systemEintragModel = new SystemEintragModelImpl();
//			systemEintragModel.setCause(systemEintragDetails.getCause());
//			systemEintragModel.setCreator(systemEintragDetails.getCreator());
//			systemEintragModel.setDate(systemEintragDetails.getDate());
//			systemEintragModel.setIncidenceClass(systemEintragDetails
//					.getIncidenceClass());
//
//			this.addJournalEintrag(systemEintragModel);
//		}
//
//		JournalResponse journalOperatorResponse = ServiceHelper.getJournalService().getOperatorEintraege(this.checkpointId);
//		List<OperatorEintragDetails> operatorEintraege = journalOperatorResponse.getOperatorEintraege();
//		for (int i = 0; i < operatorEintraege.size(); i++) {
//			OperatorEintragDetails operatorEintragDetails = operatorEintraege
//					.get(i);
//
//			OperatorEintragModel operatorEintragModel = new OperatorEintragModelImpl();
//			operatorEintragModel.setCause(operatorEintragDetails.getCause());
//			operatorEintragModel
//			.setCreator(operatorEintragDetails.getCreator());
//			operatorEintragModel.setDate(operatorEintragDetails.getDate());
//			operatorEintragModel.setAction(operatorEintragDetails.getAction());
//			operatorEintragModel.setPersonTriggerId(operatorEintragDetails
//					.getPersonTriggerId());
//			operatorEintragModel.setDone(operatorEintragDetails.isDone());
//
//			this.addJournalEintrag(operatorEintragModel);
//		}
//
//		Collections.sort(this.journalEintraege,
//				new Comparator<JournalEintragModel>() {
//			public int compare(JournalEintragModel o2,
//					JournalEintragModel o1) {
//				if (o1.getDate() == null && o2.getDate() == null) {
//					return 0;
//				}
//
//				if (o2.getDate() == null) {
//					return 1;
//				}
//
//				if (o1.getDate() == null) {
//					return -1;
//				}
//
//				return o2.getDate().compare(o1.getDate());
//			}
//		});
	}
}
