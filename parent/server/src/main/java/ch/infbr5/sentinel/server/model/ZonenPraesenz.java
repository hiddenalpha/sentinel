package ch.infbr5.sentinel.server.model;

import java.util.Calendar;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;

@Entity
@NamedQueries({
	@NamedQuery(name="findOpenZonenPraesenzByPersonAndZone",query="SELECT zp FROM ZonenPraesenz zp WHERE zp.person = :personParam AND zp.zone = :zoneParam AND zp.bis is null"), 
	@NamedQuery(name="findOpenZonenPraesenByStatusAndZone",query="SELECT zp FROM ZonenPraesenz zp WHERE zp.status = :statusParam AND zp.zone.id = :zoneParam AND zp.bis is null ORDER BY zp.person.name, zp.person.vorname"), 
	@NamedQuery(name="getCountOfOpenZonenPraesenByStatusAndZone",query="SELECT count(zp) FROM ZonenPraesenz zp WHERE zp.status = :statusParam AND zp.zone.id = :zoneParam AND zp.bis is null"),
	@NamedQuery(name=ZonenPraesenz.GET_ALL_ZONEN_PRAESENZ, query="SELECT zp FROM ZonenPraesenz zp")
})
public class ZonenPraesenz {

	public static final String GET_ALL_ZONEN_PRAESENZ = "getAllZonenPraesenz";

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne
	private Zone zone;
	@ManyToOne
	private Person person;
	private Calendar von;
	private Calendar bis;
	private PraesenzStatus status;
	private boolean	evakuert;

	public Calendar getBis() {
		return this.bis;
	}

	public Long getId() {
		return this.id;
	}

	public Person getPerson() {
		return this.person;
	}

	public PraesenzStatus getStatus() {
		return this.status;
	}

	public Calendar getVon() {
		return this.von;
	}

	public Zone getZone() {
		return this.zone;
	}

	public boolean isEvakuert() {
		return this.evakuert;
	}

	public void setBis(Calendar bis) {
		this.bis = bis;
	}

	public void setEvakuert(boolean evakuert) {
		this.evakuert = evakuert;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public void setPerson(Person person) {
		this.person = person;
	}

	public void setStatus(PraesenzStatus status) {
		this.status = status;
	}

	public void setVon(Calendar von) {
		this.von = von;
	}

	public void setZone(Zone zone) {
		this.zone = zone;
	}

}
