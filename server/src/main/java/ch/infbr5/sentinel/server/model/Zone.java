package ch.infbr5.sentinel.server.model;

import java.util.Iterator;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;

@Entity
@NamedQueries({
		@NamedQuery(name = Zone.GET_ZONEN_VALUE, query = "SELECT z FROM Zone z"),
})
public class Zone {
	public static final String GET_ZONEN_VALUE = "getZonen";

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String name;

	private boolean undOpRegeln;

	@OneToMany
	private List<Zutrittsregel> regeln;

	public Long getId() {
		return this.id;
	}

	public String getName() {
		return this.name;
	}

	public List<Zutrittsregel> getRegeln() {
		return this.regeln;
	}

	public boolean isAccessAuthorized(Ausweis ausweis) {
		boolean result = false;
		for (Iterator<Zutrittsregel> iterator = this.getRegeln().listIterator(); iterator.hasNext();) {
			Zutrittsregel regel = iterator.next();

			if (this.isUndOpRegeln()) {
				result = result & regel.evaluate(ausweis);
			} else {
				result = result | regel.evaluate(ausweis);
			}

		}
		return result;

	}

	public boolean isUndOpRegeln() {
		return this.undOpRegeln;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setRegeln(List<Zutrittsregel> regeln) {
		this.regeln = regeln;
	}

	public void setUndOpRegeln(boolean undOpRegeln) {
		this.undOpRegeln = undOpRegeln;
	}
}
