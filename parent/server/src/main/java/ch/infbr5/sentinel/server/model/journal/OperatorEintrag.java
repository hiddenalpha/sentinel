package ch.infbr5.sentinel.server.model.journal;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;

@Entity
@NamedQueries({
	@NamedQuery(name="findOperatorEintraege",query="SELECT o FROM OperatorEintrag o WHERE o.checkpointId = :checkpointId"),
	@NamedQuery(name="getPersonTriggerEintrag",query="SELECT o FROM OperatorEintrag o WHERE o.personTriggerId = :barcode AND o.isDone = false"),
})
public class OperatorEintrag {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String action;
	private String personTriggerId;
	private boolean isDone;
	private String creator;
	private Date date;
	private String cause;
	private Long checkpointId;

	public String getAction() {
		return this.action;
	}

	public String getCause() {
		return this.cause;
	}

	public Long getCheckpointId() {
		return this.checkpointId;
	}

	public String getCreator() {
		return this.creator;
	}

	public Date getDate() {
		return this.date;
	}

	public Long getId() {
		return this.id;
	}

	public String getPersonTriggerId() {
		return this.personTriggerId;
	}

	public boolean isDone() {
		return this.isDone;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public void setCause(String cause) {
		this.cause = cause;
	}

	public void setCheckpointId(Long checkpointId) {
		this.checkpointId = checkpointId;
	}

	public void setCreator(String creator) {
		this.creator = creator;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public void setDone(boolean isDone) {
		this.isDone = isDone;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public void setPersonTriggerId(String personTriggerId) {
		this.personTriggerId = personTriggerId;
	}
}
