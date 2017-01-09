package ch.infbr5.sentinel.client.polling;

import java.util.Date;

import ch.infbr5.sentinel.client.gui.components.journal.panel.BewegungsJournalModel;
import ch.infbr5.sentinel.client.gui.components.journal.panel.GefechtsJournalModel;
import ch.infbr5.sentinel.client.gui.components.journal.panel.SystemJournalModel;
import ch.infbr5.sentinel.client.util.ServiceHelper;
import ch.infbr5.sentinel.client.wsgen.JournalBewegungsMeldung;
import ch.infbr5.sentinel.client.wsgen.JournalGefechtsMeldung;
import ch.infbr5.sentinel.client.wsgen.JournalResponse;
import ch.infbr5.sentinel.client.wsgen.JournalSystemMeldung;

public class PollingModelUpdater {

	private GefechtsJournalModel modelGefechtsJournal;

	private SystemJournalModel modelSystemJournal;

	private BewegungsJournalModel modelBewegungsJournal;

	private static final int UPDATE_ZYKLUS_MILLIS = 5000;

	private long lastUpdate;

	private Runnable runnable;

	public PollingModelUpdater(GefechtsJournalModel modelGefechtsJournal, SystemJournalModel modelSystemJournal,
			BewegungsJournalModel modelBewegungsJournal) {
		this.modelGefechtsJournal = modelGefechtsJournal;
		this.modelSystemJournal = modelSystemJournal;
		this.modelBewegungsJournal = modelBewegungsJournal;
	}

	public void startKeepUpdated() {
		this.lastUpdate = new Date().getTime();

		runnable = new Runnable() {
			@Override
			public void run() {
				while (true) {
					try {
						Thread.sleep(UPDATE_ZYKLUS_MILLIS);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}

					JournalResponse response = ServiceHelper.getJournalService().getJournalSeit(lastUpdate);

					lastUpdate = new Date().getTime();

					for (JournalBewegungsMeldung m : response.getBewegungsMeldungen()) {
						modelBewegungsJournal.add(m);
					}

					for (JournalGefechtsMeldung m : response.getGefechtsMeldungen()) {
						modelGefechtsJournal.add(m);
					}

					for (JournalSystemMeldung m : response.getSystemMeldungen()) {
						modelSystemJournal.add(m);
					}
				}
			}
		};
		Thread t = new Thread(runnable);
		t.start();
	}

}
