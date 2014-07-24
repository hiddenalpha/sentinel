package ch.infbr5.sentinel.server.model.journal;

import java.util.Calendar;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;

import ch.infbr5.sentinel.server.model.Person;

/**
 * Modellierung gemäss Gefechtsjournal.
 *
 * @author Alex
 */
@Entity
@NamedQueries({
		@NamedQuery(name = "findGefechtsMeldung", query = "SELECT r FROM GefechtsMeldung r WHERE r.id = :id"),
		@NamedQuery(name = "findGefechtsMeldungen", query = "SELECT r FROM GefechtsMeldung r WHERE r.checkpoint.id = :checkpointId order by r.millis desc"),
		@NamedQuery(name = "findGefechtsMeldungenSeit", query = "SELECT r FROM GefechtsMeldung r WHERE r.checkpoint.id = :checkpointId and r.millis > :timeInMillis order by r.millis desc"),
		@NamedQuery(name = "getPersonTriggerEintraege", query = "SELECT o FROM GefechtsMeldung o WHERE o.weiterleitenAnPerson.id = :idPerson AND o.istErledigt = false"), })
public class GefechtsMeldung extends JournalEintrag {

	private Calendar zeitpunktMeldungsEingang;

	private String werWasWoWie;

	private String massnahme;

	@ManyToOne
	private Person weiterleitenAnPerson;

	private Calendar zeitpunktErledigt;

	private boolean istErledigt;

	public Calendar getZeitpunktMeldungsEingang() {
		return zeitpunktMeldungsEingang;
	}

	public void setZeitpunktMeldungsEingang(Calendar zeitpunktMeldungsEingang) {
		this.zeitpunktMeldungsEingang = zeitpunktMeldungsEingang;
	}

	public String getWerWasWoWie() {
		return werWasWoWie;
	}

	public void setWerWasWoWie(String werWasWoWie) {
		this.werWasWoWie = werWasWoWie;
	}

	public String getMassnahme() {
		return massnahme;
	}

	public void setMassnahme(String massnahme) {
		this.massnahme = massnahme;
	}

	public Person getWeiterleitenAnPerson() {
		return weiterleitenAnPerson;
	}

	public void setWeiterleitenAnPerson(Person weiterleitenAnPerson) {
		this.weiterleitenAnPerson = weiterleitenAnPerson;
	}

	public Calendar getZeitpunktErledigt() {
		return zeitpunktErledigt;
	}

	public void setZeitpunktErledigt(Calendar zeitpunktErledigt) {
		this.zeitpunktErledigt = zeitpunktErledigt;
	}

	public boolean isIstErledigt() {
		return istErledigt;
	}

	public void setIstErledigt(boolean istErledigt) {
		this.istErledigt = istErledigt;
	}

}
