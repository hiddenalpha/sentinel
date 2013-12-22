package ch.infbr5.sentinel.server.tools;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PTransferObject {

	private String name;
	private String vorname;
	private Date geburtsdatum;
	private String gradText;
	private String ahvNr;
	private String einheit;
	private String psptext;
	private String funktion;
	private Date letzteAenderung;
	private byte[] image;
	private List<ATransferObject> ausweisListe = new ArrayList<ATransferObject>();
	private List<BTransferObject> berechtigungListe = new ArrayList<BTransferObject>();

	//	public PTransferObject(Person person) {
	//		this.setName(person.getName());
	//		this.setVorname(person.getVorname());
	//		this.setGeburtsdatum(person.getGeburtsdatum());
	//		this.setAhvNr(person.getAhvNr());
	//		if (person.getGrad() != null)
	//			this.setGradText(person.getGrad().getKurzform());
	//		this.setEinheit(person.getEinheit());
	//		this.setPsptext(person.getPsptext());
	//		this.setFunktion(person.getFunktion());
	//		this.setLetzteAenderung(person.getLetzteAenderung());
	//		this.setImage(ImageStore.loadJPEGImage(person));
	//
	//		List<ATransferObject> ausweisListe = new ArrayList<ATransferObject>();
	//		for (Iterator<Ausweis> iterAusweise = person.getAusweise().iterator(); iterAusweise.hasNext();) {
	//			Ausweis ausweis = iterAusweise.next();
	//
	//			// if (!ausweis.getUngueltig()) {
	//				ATransferObject aTransferObj = new ATransferObject();
	//				aTransferObj.setBarCode(ausweis.getBarCode());
	//				aTransferObj.setGueltigVon(ausweis.getGueltigVon());
	//				aTransferObj.setGueltigBis(ausweis.getGueltigBis());
	//				aTransferObj.setUngueltig(ausweis.getUngueltig());
	//				aTransferObj.setGedruckt(ausweis.getGedruckt());
	//				if (ausweis.getSlot() != null) {
	//					aTransferObj.setBoxname(ausweis.getSlot().getBox().getName());
	//					aTransferObj.setSlotNr(ausweis.getSlot().getSlotNumber());
	//				}
	//				aTransferObj.setLetzteAenderung(ausweis.getLetzteAenderung());
	//				ausweisListe.add(aTransferObj);
	//		//	}
	//		}
	//		this.setAusweisListe(ausweisListe);
	//
	//		List<BTransferObject> berechtigungListe = new ArrayList<BTransferObject>();
	//		for (Iterator<Berechtigung> iterBerchtigung = person.getBerechtigungen().iterator(); iterBerchtigung.hasNext();) {
	//			Berechtigung berechtigung = iterBerchtigung.next();
	//			BTransferObject bTransferObj = new BTransferObject();
	//			bTransferObj.setDefinitionId(berechtigung.getDefinitionId());
	//			bTransferObj.setGueltigBis(berechtigung.getGueltigBis());
	//			bTransferObj.setGueltigVon(berechtigung.getGueltigVon());
	//			bTransferObj.setLetzteAenderung(berechtigung.getLetzteAenderung());
	//			bTransferObj.setName(berechtigung.getName());
	//			bTransferObj.setUngueltig(berechtigung.getUngueltig());
	//			berechtigungListe.add(bTransferObj);
	//		}
	//		this.setBerechtigungListe(berechtigungListe);
	//	}


	public PTransferObject() {
		// TODO Auto-generated constructor stub
	}


	public String getAhvNr() {
		return this.ahvNr;
	}

	public List<ATransferObject> getAusweisListe() {
		return this.ausweisListe;
	}

	public List<BTransferObject> getBerechtigungListe() {
		return this.berechtigungListe;
	}

	public String getEinheit() {
		return this.einheit;
	}

	public String getFunktion() {
		return this.funktion;
	}

	public Date getGeburtsdatum() {
		return this.geburtsdatum;
	}

	public String getGradText() {
		return this.gradText;
	}

	public byte[] getImage() {
		return this.image;
	}

	public Date getLetzteAenderung() {
		return this.letzteAenderung;
	}

	public String getName() {
		return this.name;
	}

	public String getPsptext() {
		return this.psptext;
	}

	public String getVorname() {
		return this.vorname;
	}

	public void setAhvNr(String ahvNr) {
		this.ahvNr = ahvNr;
	}

	public void setAusweisListe(List<ATransferObject> ausweisListe) {
		this.ausweisListe = ausweisListe;
	}

	public void setBerechtigungListe(List<BTransferObject> berechtigungListe) {
		this.berechtigungListe = berechtigungListe;
	}

	public void setEinheit(String einheit) {
		this.einheit = einheit;
	}

	public void setFunktion(String funktion) {
		this.funktion = funktion;
	}

	public void setGeburtsdatum(Date geburtsdatum) {
		this.geburtsdatum = geburtsdatum;
	}

	public void setGradText(String gradText) {
		this.gradText = gradText;
	}

	public void setImage(byte[] image) {
		this.image = image;
	}

	public void setLetzteAenderung(Date letzteAenderung) {
		this.letzteAenderung = letzteAenderung;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setPsptext(String psptext) {
		this.psptext = psptext;
	}

	public void setVorname(String vorname) {
		this.vorname = vorname;
	}



}
