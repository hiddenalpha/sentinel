package ch.infbr5.sentinel.server.ws.journal;

import ch.infbr5.sentinel.server.ws.CheckpointDetails;

public class JournalEintrag {

	private long id;

	private long millis;

	private CheckpointDetails checkpoint;

	public CheckpointDetails getCheckpoint() {
		return checkpoint;
	}

	public void setCheckpoint(CheckpointDetails checkpoint) {
		this.checkpoint = checkpoint;
	}

	public long getMillis() {
		return millis;
	}

	public void setMillis(long millis) {
		this.millis = millis;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

}
