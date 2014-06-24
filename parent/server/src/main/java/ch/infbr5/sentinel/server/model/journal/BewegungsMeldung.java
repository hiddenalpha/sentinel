package ch.infbr5.sentinel.server.model.journal;

import javax.persistence.Entity;

@Entity
public class BewegungsMeldung extends JournalEintrag {

	private String message;

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
	
}
