package ch.infbr5.sentinel.server.model.journal;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import ch.infbr5.sentinel.server.model.Checkpoint;

@Entity
public class JournalEintrag {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	private long millis;

	@ManyToOne
	private Checkpoint checkpoint;

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

	public Checkpoint getCheckpoint() {
		return checkpoint;
	}

	public void setCheckpoint(Checkpoint checkpoint) {
		this.checkpoint = checkpoint;
	}

}
