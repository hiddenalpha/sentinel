package ch.infbr5.sentinel.client.polling;

import java.util.Date;

import javax.swing.DefaultListModel;

import ch.infbr5.sentinel.client.util.ConfigurationLocalHelper;
import ch.infbr5.sentinel.client.util.ServiceHelper;
import ch.infbr5.sentinel.client.wsgen.JournalGefechtsMeldung;
import ch.infbr5.sentinel.client.wsgen.JournalResponse;

public class UpdateGefechtsJournal {

	private Runnable runnable;

	private long lastUpdate;

	public UpdateGefechtsJournal(final DefaultListModel<JournalGefechtsMeldung> model) {
		this.lastUpdate = new Date().getTime();

		runnable = new Runnable() {
			@Override
			public void run() {
				while (true) {

					try {
						Thread.sleep(5000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}

					JournalResponse response = ServiceHelper.getJournalService().getGefechtsJournalSeit(ConfigurationLocalHelper.getConfig().getCheckpointId(), lastUpdate);

					lastUpdate = new Date().getTime();

					for (JournalGefechtsMeldung m : response.getGefechtsMeldungen()) {
						model.add(0, m);
					}
				}

			}
		};
		Thread t = new Thread(runnable);
		t.start();
	}

}
