package ch.infbr5.sentinel.client.polling;

import ch.infbr5.sentinel.client.config.ConfigurationLocalHelper;
import ch.infbr5.sentinel.client.gui.components.journal.panel.BewegungsJournalModel;
import ch.infbr5.sentinel.client.util.ServiceHelper;
import ch.infbr5.sentinel.client.wsgen.JournalBewegungsMeldung;
import ch.infbr5.sentinel.client.wsgen.JournalResponse;

public class BewegungsJournalUpdater extends AbstractPollingModelUpdater {

	private BewegungsJournalModel model;

	public BewegungsJournalUpdater(final BewegungsJournalModel model) {
		this.model = model;
	}

	@Override
	void updateModel() {
		JournalResponse response = ServiceHelper.getJournalService().getBewegungsJournalSeit(
				ConfigurationLocalHelper.getConfig().getCheckpointId(), getLastUpdate());
		for (JournalBewegungsMeldung m : response.getBewegungsMeldungen()) {
			model.add(m);
		}
	}

}
