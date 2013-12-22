package ch.infbr5.sentinel.client.util;

/*
 * EinheitDetailsClient used for showing in JComboBox
 * because ch.infbr5.sentinel.client.wsgen.EinheitDetails is not
 * translating overridden toString-Method while generating
 */
public class EinheitDetailsClient {
	private String name;
	private Long id;

	public EinheitDetailsClient(Long id,String name) {
		this.id = id;
		this.name = name;
	}
	
	public String getName() {
		return this.name;
	}

	public Long getId() {
		return id;
	}

	@Override
	public String toString() {
		return name;
	}
}
