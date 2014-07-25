package ch.infbr5.sentinel.client.polling;

import ch.infbr5.sentinel.client.config.ConfigurationLocalHelper;
import ch.infbr5.sentinel.client.gui.components.journal.panel.SystemJournalModel;
import ch.infbr5.sentinel.client.util.ServiceHelper;
import ch.infbr5.sentinel.client.wsgen.JournalResponse;
import ch.infbr5.sentinel.client.wsgen.JournalSystemMeldung;

public class SystemJournalUpdater extends AbstractPollingModelUpdater {

	private SystemJournalModel model;

	public SystemJournalUpdater(final SystemJournalModel model) {
		this.model = model;
	}

	@Override
	void updateModel() {
		JournalResponse response = ServiceHelper.getJournalService().getSystemJournalSeit(
				ConfigurationLocalHelper.getConfig().getCheckpointId(), getLastUpdate());
		for (JournalSystemMeldung m : response.getSystemMeldungen()) {
			model.add(m);
		}
	}

}
