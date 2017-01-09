package ch.infbr5.sentinel.server.ws;

import java.util.Calendar;

public class PersonDetails {

	private String grad;
	private String name;
	private String vorname;
	private String funktion;
	private Long einheitId;
	private String einheitText;
	private Calendar lastStatusChange;
	private String imageId;
	private byte[] image;
	private String barcode;
	private Long id;
	private String ahvNr;
	private Calendar geburtsdatum;

	public String getBarcode() {
		return this.barcode;
	}

	public Long getEinheitId() {
		return this.einheitId;
	}

	public String getFunktion() {
		return this.funktion;
	}

	public String getGrad() {
		return this.grad;
	}

	public String getImageId() {
		return this.imageId;
	}

	public Calendar getLastStatusChange() {
		return this.lastStatusChange;
	}

	public String getName() {
		return this.name;
	}

	public String getVorname() {
		return this.vorname;
	}

	public void setBarcode(String barcode) {
		this.barcode = barcode;
	}

	public void setEinheitId(Long einheitId) {
		this.einheitId = einheitId;
	}

	public void setFunktion(String funktion) {
		this.funktion = funktion;
	}

	public void setGrad(String grad) {
		this.grad = grad;
	}

	public void setImageId(String imageId) {
		this.imageId = imageId;
	}

	public void setLastStatusChange(Calendar lastStatusChange) {
		this.lastStatusChange = lastStatusChange;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setVorname(String vorname) {
		this.vorname = vorname;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getId() {
		return id;
	}

	public void setAhvNr(String ahvNr) {
		this.ahvNr = ahvNr;
	}

	public String getAhvNr() {
		return ahvNr;
	}

	public void setGeburtsdatum(Calendar geburtsdatum) {
		this.geburtsdatum = geburtsdatum;
	}

	public Calendar getGeburtsdatum() {
		return geburtsdatum;
	}

	public byte[] getImage() {
		return image;
	}

	public void setImage(byte[] image) {
		this.image = image;
	}

	public String getEinheitText() {
		return einheitText;
	}

	public void setEinheitText(String einheitText) {
		this.einheitText = einheitText;
	}
	
	
}
