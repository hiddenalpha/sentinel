package ch.infbr5.sentinel.client.polling;

import java.util.Date;

import javax.swing.DefaultListModel;

import ch.infbr5.sentinel.client.util.ConfigurationLocalHelper;
import ch.infbr5.sentinel.client.util.ServiceHelper;
import ch.infbr5.sentinel.client.wsgen.JournalBewegungsMeldung;
import ch.infbr5.sentinel.client.wsgen.JournalResponse;

public class UpdateBewegungsJournal {

	private Runnable runnable;

	private long lastUpdate;

	public UpdateBewegungsJournal(final DefaultListModel<JournalBewegungsMeldung> model) {
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

					JournalResponse response = ServiceHelper.getJournalService().getBewegungsJournalSeit(ConfigurationLocalHelper.getConfig().getCheckpointId(), lastUpdate);

					lastUpdate = new Date().getTime();

					for (JournalBewegungsMeldung m : response.getBewegungsMeldungen()) {
						model.add(0, m);
					}
				}

			}
		};
		Thread t = new Thread(runnable);
		t.start();
	}

}
