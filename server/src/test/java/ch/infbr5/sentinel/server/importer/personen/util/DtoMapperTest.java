package ch.infbr5.sentinel.server.importer.personen.util;

import java.util.Calendar;
import java.util.GregorianCalendar;

import org.junit.Assert;
import org.junit.Test;

import ch.infbr5.sentinel.server.model.Einheit;
import ch.infbr5.sentinel.server.model.Grad;
import ch.infbr5.sentinel.server.model.Person;
import ch.infbr5.sentinel.server.ws.PersonDetails;

public class DtoMapperTest {

	@Test
	public void testToPersonDetails() {
		Person p = new Person();
		p.setAhvNr("768.469.678.14");
		p.setFunktion("funktion");
		p.setName("name");
		p.setVorname("vorname");
		
		Einheit e = new Einheit();
		e.setId(15l);
		e.setName("einheit");
		p.setEinheit(e);
		p.setGrad(Grad.BUNDESRAT);
		
		Calendar c = new GregorianCalendar(2011, 8, 9);
		p.setGeburtsdatum(c);
		
		p.setId(18l);
		
		PersonDetails details = DtoMapper.toPersonDetails(p);
		
		Assert.assertEquals("768.469.678.14", details.getAhvNr());
		Assert.assertEquals("funktion", details.getFunktion());
		Assert.assertEquals("name", details.getName());
		Assert.assertEquals("vorname", details.getVorname());
		Assert.assertEquals("einheit", details.getEinheitText());
		Assert.assertEquals(Long.valueOf(15l), details.getEinheitId());
		Assert.assertEquals(new GregorianCalendar(2011, 8, 9), details.getGeburtsdatum());
		Assert.assertEquals(Long.valueOf(18l), details.getId());
		Assert.assertEquals(Grad.BUNDESRAT.toString(), details.getGrad());
	}

}
