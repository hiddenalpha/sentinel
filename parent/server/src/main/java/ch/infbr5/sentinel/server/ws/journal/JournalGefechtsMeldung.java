package ch.infbr5.sentinel.server.ws.journal;

import java.util.Calendar;

import ch.infbr5.sentinel.server.ws.PersonDetails;

public class JournalGefechtsMeldung extends JournalEintrag {

	private Calendar zeitpunktMeldungsEingang;

	private String werWasWoWie;

	private String massnahme;

	private PersonDetails weiterleitenAnPerson;

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

	public PersonDetails getWeiterleitenAnPerson() {
		return weiterleitenAnPerson;
	}

	public void setWeiterleitenAnPerson(PersonDetails weiterleitenAnPerson) {
		this.weiterleitenAnPerson = weiterleitenAnPerson;
	}

}
