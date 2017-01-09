package ch.infbr5.sentinel.server.importer.personen.util;

import ch.infbr5.sentinel.server.model.Einheit;
import ch.infbr5.sentinel.server.model.Grad;
import ch.infbr5.sentinel.server.model.Person;
import ch.infbr5.sentinel.server.ws.PersonDetails;

public class DtoMapper {

	public static PersonDetails toPersonDetails(Person source) {
		PersonDetails target = new PersonDetails();
		target.setAhvNr(source.getAhvNr());
		target.setName(source.getName());
		target.setFunktion(source.getFunktion());
		target.setGeburtsdatum(source.getGeburtsdatum());
		target.setId(source.getId());
		target.setVorname(source.getVorname());

		Einheit einheit = source.getEinheit();
		target.setEinheitId(einheit != null ? einheit.getId() : -1);
		target.setEinheitText(einheit != null ? einheit.getName() : "");

		Grad grad = source.getGrad();
		target.setGrad(grad != null ? grad.toString() : "");

		return target;
	}

}
