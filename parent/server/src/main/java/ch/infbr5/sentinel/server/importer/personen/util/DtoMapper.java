package ch.infbr5.sentinel.server.importer.personen.util;

import ch.infbr5.sentinel.server.model.Einheit;
import ch.infbr5.sentinel.server.model.Grad;
import ch.infbr5.sentinel.server.model.Person;
import ch.infbr5.sentinel.server.ws.PersonDetails;

public class DtoMapper {

	public static PersonDetails toPersonDetails(Person from) {
		PersonDetails target = new PersonDetails();
		target.setAhvNr(from.getAhvNr());
		target.setName(from.getName());
		target.setFunktion(from.getFunktion());
		target.setGeburtsdatum(from.getGeburtsdatum());
		target.setId(from.getId());
		target.setVorname(from.getVorname());
		
		Einheit einheit = from.getEinheit();
		target.setEinheitId(einheit != null ? einheit.getId() : -1);
		target.setEinheitText(einheit.getName());
		
		Grad grad = from.getGrad();
		target.setGrad(grad != null ? grad.toString() : "");

		return target;
	}
	
}
