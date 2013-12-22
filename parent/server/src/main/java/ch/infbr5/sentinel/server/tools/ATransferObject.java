package ch.infbr5.sentinel.server.tools;

import java.util.Date;

public class ATransferObject {
	private Date gueltigVon;
	private Date gueltigBis;
	private String barCode;
	private boolean ungueltig;
	private boolean gedruckt;
	private String boxname;
	private int slotNr;
	private Date letzteAenderung;

	public String getBarCode() {
		return this.barCode;
	}

	public String getBoxname() {
		return this.boxname;
	}

	public boolean getGedruckt() {
		return this.gedruckt;
	}

	public Date getGueltigBis() {
		return this.gueltigBis;
	}

	public Date getGueltigVon() {
		return this.gueltigVon;
	}

	public Date getLetzteAenderung() {
		return this.letzteAenderung;
	}

	public int getSlotNr() {
		return this.slotNr;
	}

	public boolean getUngueltig() {
		return this.ungueltig;
	}

	public void setBarCode(String barCode) {
		this.barCode = barCode;
	}

	public void setBoxname(String boxname) {
		this.boxname = boxname;
	}

	public void setGedruckt(boolean gedruckt) {
		this.gedruckt = gedruckt;
	}

	public void setGueltigBis(Date gueltigBis) {
		this.gueltigBis = gueltigBis;
	}

	public void setGueltigVon(Date gueltigVon) {
		this.gueltigVon = gueltigVon;
	}

	public void setLetzteAenderung(Date letzteAenderung) {
		this.letzteAenderung = letzteAenderung;
	}

	public void setSlotNr(int slotNr) {
		this.slotNr = slotNr;
	}

	public void setUngueltig(boolean ungueltig) {
		this.ungueltig = ungueltig;
	}

}
