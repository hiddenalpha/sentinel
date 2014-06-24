package ch.infbr5.sentinel.server.ws.journal;

import ch.infbr5.sentinel.server.ws.PersonDetails;

public class JournalGefechtsMeldung extends JournalEintrag {

	private String text;

	private boolean isDone = false;

	private String creator;

	private PersonDetails personDetails;

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public boolean isDone() {
		return isDone;
	}

	public void setDone(boolean isDone) {
		this.isDone = isDone;
	}

	public String getCreator() {
		return creator;
	}

	public void setCreator(String creator) {
		this.creator = creator;
	}

	public PersonDetails getPersonDetails() {
		return personDetails;
	}

	public void setPersonDetails(PersonDetails personDetails) {
		this.personDetails = personDetails;
	}

}
