package ch.infbr5.sentinel.client.polling;

import ch.infbr5.sentinel.client.gui.components.journal.list.GefechtsJournalModel;
import ch.infbr5.sentinel.client.util.ConfigurationLocalHelper;
import ch.infbr5.sentinel.client.util.ServiceHelper;
import ch.infbr5.sentinel.client.wsgen.JournalGefechtsMeldung;
import ch.infbr5.sentinel.client.wsgen.JournalResponse;

public class GefechtsJournalUpdater extends AbstractPollingModelUpdater {

	private GefechtsJournalModel model;

	public GefechtsJournalUpdater(GefechtsJournalModel model) {
		this.model = model;
	}

	@Override
	void updateModel() {
		JournalResponse response = ServiceHelper.getJournalService().getGefechtsJournalSeit(
				ConfigurationLocalHelper.getConfig().getCheckpointId(), getLastUpdate());
		for (JournalGefechtsMeldung m : response.getGefechtsMeldungen()) {
			model.add(m);
		}
	}

}
