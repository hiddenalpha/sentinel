package ch.infbr5.sentinel.server.model.journal;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class JournalEintrag {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	
	private long millis;
	
	private long checkpointId;
	
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getMillis() {
		return millis;
	}

	public void setMillis(long millis) {
		this.millis = millis;
	}

	public long getCheckpointId() {
		return checkpointId;
	}

	public void setCheckpointId(long checkpointId) {
		this.checkpointId = checkpointId;
	}

}
