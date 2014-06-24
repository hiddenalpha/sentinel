package ch.infbr5.sentinel.server.ws.journal;

public class JournalEintrag {

	private long id;
	
	private long millis;
	
	private long checkpointId;

	public long getCheckpointId() {
		return checkpointId;
	}

	public void setCheckpointId(long checkpointId) {
		this.checkpointId = checkpointId;
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
