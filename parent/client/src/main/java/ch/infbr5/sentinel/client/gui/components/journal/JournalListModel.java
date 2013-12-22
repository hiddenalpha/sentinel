package ch.infbr5.sentinel.client.gui.components.journal;

import java.util.List;

import javax.swing.AbstractListModel;
import javax.swing.ListModel;

import ch.infbr5.sentinel.client.util.ConfigurationLocalHelper;
import ch.infbr5.sentinel.client.util.ServiceHelper;
import ch.infbr5.sentinel.client.wsgen.JournalEintragDetails;
import ch.infbr5.sentinel.client.wsgen.JournalResponse;

public class JournalListModel extends AbstractListModel<JournalEintragDetails> implements ListModel<JournalEintragDetails> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private List<JournalEintragDetails> data;

	public JournalListModel() {
		super();
		updateDate();
	}

	private void updateDate() {
		JournalResponse response = ServiceHelper.getJournalService().getLogEintraege(
				ConfigurationLocalHelper.getConfig().getCheckpointId());
		data = response.getRecords();
	}

	@Override
	public JournalEintragDetails getElementAt(int colIndex) {
		return data.get(colIndex);
	}

	@Override
	public int getSize() {
		return data.size();
	}

}
