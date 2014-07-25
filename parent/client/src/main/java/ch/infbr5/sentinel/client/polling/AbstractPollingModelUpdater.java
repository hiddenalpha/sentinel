package ch.infbr5.sentinel.client.polling;

import java.util.Date;

public abstract class AbstractPollingModelUpdater {

	private static final int UPDATE_ZYKLUS_MILLIS = 5000;

	private long lastUpdate;

	private Runnable runnable;

	abstract void updateModel();

	protected long getLastUpdate() {
		return this.lastUpdate;
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
					updateModel();
					lastUpdate = new Date().getTime();
				}
			}
		};
		Thread t = new Thread(runnable);
		t.start();
	}

}
