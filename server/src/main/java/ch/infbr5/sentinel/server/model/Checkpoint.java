package ch.infbr5.sentinel.server.model;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;

@Entity
@NamedQueries({
	@NamedQuery(name="getCheckpointById",query="SELECT c FROM Checkpoint c WHERE c.id = :checkpointId"),
	@NamedQuery(name="getCheckpoints",query="SELECT c FROM Checkpoint c")
})
public class Checkpoint {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String name;
	@ManyToMany
	private List<Zone> checkInZonen;
	@ManyToMany
	private List<Zone> checkOutZonen;

	public List<Zone> getCheckInZonen() {
		return this.checkInZonen;
	}

	public List<Zone> getCheckOutZonen() {
		return this.checkOutZonen;
	}

	public Long getId() {
		return this.id;
	}

	public String getName() {
		return this.name;
	}

	public void setCheckInZonen(List<Zone> checkInZonen) {
		this.checkInZonen = checkInZonen;
	}

	public void setCheckOutZonen(List<Zone> checkOutZonen) {
		this.checkOutZonen = checkOutZonen;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public void setName(String name) {
		this.name = name;
	}
}
