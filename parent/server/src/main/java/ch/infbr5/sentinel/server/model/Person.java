package ch.infbr5.sentinel.server.model;

import java.util.Calendar;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;

import ch.infbr5.sentinel.server.utils.DateHelper;

@Entity
@NamedQueries({
		@NamedQuery(name = Person.GET_PERSON_BY_ID_VALUE, query = "SELECT p FROM Person p WHERE p.id = :personId"),
		@NamedQuery(name = Person.GET_PERSONEN_VALUE, query = "SELECT p FROM Person p ORDER BY p.name, p.vorname"),
		@NamedQuery(name = Person.GET_PERSONEN_MIT_AUSWEIS, query = "SELECT p FROM Person p WHERE NOT p.validAusweis IS NULL ORDER BY p.name, p.vorname"),
		@NamedQuery(name = Person.GET_PERSONEN_MIT_AUSWEIS_NACH_EINHEIT, query = "SELECT p FROM Person p WHERE p.einheit.name = :einheitName AND NOT p.validAusweis IS NULL ORDER by p.name, p.vorname"),
		@NamedQuery(name = Person.GET_PERSONEN_NACH_EINHEIT, query = "SELECT p FROM Person p WHERE p.einheit.name = :einheitName ORDER by p.einheit.name, p.name, p.vorname"),
		@NamedQuery(name = Person.GET_PERSON_BY_AVHNR, query = "SELECT p FROM Person p WHERE p.ahvNr = :personAhvNr"),
		@NamedQuery(name = Person.GET_PERSON_BY_NAME_AND_DATE, query = "SELECT p FROM Person p WHERE p.name = :personName and p.vorname = :personVorname and p.geburtsdatum = :personGeburtsdatum") })
public class Person {
	public static final String GET_PERSONEN_VALUE = "getPersonen";
	public static final String GET_PERSON_BY_ID_VALUE = "getPersonById";
	public static final String GET_PERSON_BY_AVHNR = "getPersonByAhvNr";
	public static final String GET_PERSON_BY_NAME_AND_DATE = "getPersonByNameAndDate";
	public static final String GET_PERSONEN_MIT_AUSWEIS = "getPersonenMitAuseis";
	public static final String GET_PERSONEN_MIT_AUSWEIS_NACH_EINHEIT = "getPersonenMitAuseisNachEinheit";
	public static final String GET_PERSONEN_NACH_EINHEIT = "getPersonenNachEinheit";;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String name;
	private String vorname;
	private Calendar geburtsdatum;
	private String funktion;
	private String ahvNr;

	@OneToOne(optional = true, orphanRemoval = true)
	private Ausweis validAusweis;

	@Enumerated(EnumType.STRING)
	private Grad grad;

	@ManyToOne
	private Einheit einheit;

	public Ausweis getValidAusweis() {
		return validAusweis;
	}

	public void setValidAusweis(Ausweis validAusweis) {
		this.validAusweis = validAusweis;
	}

	public String getAhvNr() {
		return this.ahvNr;
	}

	public Einheit getEinheit() {
		return this.einheit;
	}

	public String getFunktion() {
		return this.funktion;
	}

	public Calendar getGeburtsdatum() {
		return this.geburtsdatum;
	}

	public Grad getGrad() {
		return this.grad;
	}

	public Long getId() {
		return this.id;
	}

	public String getName() {
		return this.name;
	}

	public String getVorname() {
		return this.vorname;
	}

	public void setAhvNr(String ahvNr) {
		this.ahvNr = ahvNr;
	}

	public void setEinheit(Einheit einheit) {
		this.einheit = einheit;
	}

	public void setFunktion(String funktion) {
		this.funktion = funktion;
	}

	public void setGeburtsdatum(Calendar geburtsdatum) {
		this.geburtsdatum = geburtsdatum;
	}

	public void setGrad(Grad grad) {
		this.grad = grad;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setVorname(String vorname) {
		this.vorname = vorname;
	}

	@Override
	public String toString() {
		String res = getAhvNr() + ", ";
		if (getGrad() != null) {
			res = res + getGrad().toString() + " ";
		}
		res = res + getName() + " " + getVorname() + ", ";
		if (getGeburtsdatum() != null) {
			res = res + DateHelper.getFormatedString(getGeburtsdatum()) + ", ";
		}
		if (getFunktion() != null) {
			res = res + getFunktion() + ", ";
		}
		if (getEinheit() != null) {
			res = res + getEinheit().getName();
		}
		return res;
	}

}
