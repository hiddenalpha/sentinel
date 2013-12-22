package ch.infbr5.sentinel.server.tools;

import java.util.Date;

public class BTransferObject {

	private String name;
	private int definitionId;
	private Date gueltigVon;
	private Date gueltigBis;
	private Date letzteAenderung;
	private boolean ungueltig;

	public int getDefinitionId() {
		return this.definitionId;
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

	public String getName() {
		return this.name;
	}

	public boolean getUngueltig() {
		return this.ungueltig;
	}

	public void setDefinitionId(int definitionId) {
		this.definitionId = definitionId;
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

	public void setName(String name) {
		this.name = name;
	}

	public void setUngueltig(boolean ungueltig) {
		this.ungueltig = ungueltig;
	}

}
