package ch.infbr5.sentinel.server.model;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;

@Entity
@NamedQueries({
		@NamedQuery(name = Ausweis.FIND_AUSWEIS_BY_BARCODE_VALUE, query = "SELECT a FROM Ausweis a WHERE a.barcode = :barcodeParam"),
		@NamedQuery(name = Ausweis.FIND_AUSWEISE_VALUE, query = "SELECT a FROM Ausweis a WHERE a.invalid = false ORDER BY a.person.name,a.person.vorname"),
		@NamedQuery(name = Ausweis.FIND_AUSWEISE_ZUM_DRUCKEN, query = "SELECT a FROM Ausweis a WHERE (a.invalid = false OR a.invalid IS NULL) AND (a.erstellt = false OR a.erstellt IS NULL)"),
		@NamedQuery(name = Ausweis.INVALIDATE_AUSWEISE_BY_PERSON_VALUE, query = "UPDATE Ausweis a SET a.invalid = true WHERE a.person = :personParam"), })
public class Ausweis {
	public static final String FIND_AUSWEIS_BY_BARCODE_VALUE = "findAusweisByBarcode";
	public static final String FIND_AUSWEISE_VALUE = "findAusweise";
	public static final String INVALIDATE_AUSWEISE_BY_PERSON_VALUE = "invalidateAusweiseByPerson";
	public static final String FIND_AUSWEISE_ZUM_DRUCKEN = "findAusweiseZumDrucken";

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String barcode;

	private boolean invalid;

	private boolean erstellt;

	private Date gueltigVon;

	private Date gueltigBis;

	@ManyToOne
	private Person person;

	@ManyToOne(optional = true)
	private AusweisBox box;

	public String getBarcode() {
		return this.barcode;
	}

	public AusweisBox getBox() {
		return this.box;
	}

	public Long getId() {
		return this.id;
	}

	public Person getPerson() {
		return this.person;
	}

	public void setBarcode(String barcode) {
		this.barcode = barcode;
	}

	public void setBox(AusweisBox box) {
		this.box = box;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public void setPerson(Person person) {
		this.person = person;
	}

	public boolean isInvalid() {
		return invalid;
	}

	public void setInvalid(boolean invalid) {
		this.invalid = invalid;
	}

	public boolean isErstellt() {
		return erstellt;
	}

	public void setErstellt(boolean erstellt) {
		this.erstellt = erstellt;
	}

	public Date getGueltigVon() {
		return gueltigVon;
	}

	public void setGueltigVon(Date gueltigVon) {
		this.gueltigVon = gueltigVon;
	}

	public Date getGueltigBis() {
		return gueltigBis;
	}

	public void setGueltigBis(Date gueltigBis) {
		this.gueltigBis = gueltigBis;
	}

	public void invalidate() {
		setErstellt(true);
		setGueltigBis(new Date());
		setInvalid(true);
	}


}
