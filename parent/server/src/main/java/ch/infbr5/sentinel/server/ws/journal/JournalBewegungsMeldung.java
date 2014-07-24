package ch.infbr5.sentinel.server.ws.journal;

import ch.infbr5.sentinel.server.ws.PersonDetails;

public class JournalBewegungsMeldung extends JournalEintrag {

	private String praesenzStatus;

	private PersonDetails person;

	public String getPraesenzStatus() {
		return praesenzStatus;
	}

	public void setPraesenzStatus(String praesenzStatus) {
		this.praesenzStatus = praesenzStatus;
	}

	public PersonDetails getPerson() {
		return person;
	}

	public void setPerson(PersonDetails person) {
		this.person = person;
	}


}
