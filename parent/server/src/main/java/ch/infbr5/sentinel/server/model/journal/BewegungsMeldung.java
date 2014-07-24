package ch.infbr5.sentinel.server.model.journal;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;

import ch.infbr5.sentinel.server.model.Person;
import ch.infbr5.sentinel.server.model.PraesenzStatus;

@Entity
@NamedQueries({
	@NamedQuery(name = "findBewegungsMeldung", query = "SELECT r FROM BewegungsMeldung r WHERE r.id = :id"),
	@NamedQuery(name = "findBewegungsMeldungen", query = "SELECT r FROM BewegungsMeldung r WHERE r.checkpoint.id = :checkpointId order by r.millis desc"),
	@NamedQuery(name = "findBewegungsMeldungenSeit", query = "SELECT r FROM BewegungsMeldung r WHERE r.checkpoint.id = :checkpointId AND r.millis > :timeInMillis order by r.millis desc"),
})

public class BewegungsMeldung extends JournalEintrag {

	private PraesenzStatus praesenzStatus;

	@ManyToOne
	private Person person;

	public PraesenzStatus getPraesenzStatus() {
		return praesenzStatus;
	}

	public void setPraesenzStatus(PraesenzStatus praesenzStatus) {
		this.praesenzStatus = praesenzStatus;
	}

	public Person getPerson() {
		return person;
	}

	public void setPerson(Person person) {
		this.person = person;
	}

}
