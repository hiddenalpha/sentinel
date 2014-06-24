package ch.infbr5.sentinel.server.model.journal;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;

import ch.infbr5.sentinel.server.model.Person;

@Entity
@NamedQueries({
	@NamedQuery(name = "findGefechtsMeldungen", query = "SELECT r FROM GefechtsMeldung r WHERE r.checkpointId = :checkpointId order by r.millis desc")
})

	//@NamedQuery(name="findBenutzerMeldungEintraege",query="SELECT o FROM GefechtsMeldung o WHERE o.checkpointId = :checkpointId"),
	//@NamedQuery(name="getPersonTriggerEintrag",query="SELECT o FROM GefechtsMeldung o WHERE o.idPersonGefechtsMeldungIsFor = :idPerson AND o.isDone = false"),
public class GefechtsMeldung extends JournalEintrag {

	private String text;

	private boolean isDone = false;

	private String creator;

	@ManyToOne
	private Person person;

	public Person getPerson() {
		return person;
	}

	public void setPerson(Person person) {
		this.person = person;
	}

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

}
