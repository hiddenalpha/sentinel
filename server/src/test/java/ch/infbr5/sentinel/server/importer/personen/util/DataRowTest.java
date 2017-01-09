package ch.infbr5.sentinel.server.importer.personen.util;


import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import ch.infbr5.sentinel.common.util.DateFormater;
import ch.infbr5.sentinel.server.model.Grad;
import ch.infbr5.sentinel.server.ws.PersonDetails;
import ch.infbr5.sentinel.server.ws.importer.mapping.PersonenAttribute;
import ch.infbr5.sentinel.server.ws.importer.mapping.PersonenImportColumn;
import ch.infbr5.sentinel.server.ws.importer.mapping.PersonenImportColumnMapping;

public class DataRowTest {

	private DataRow testeeValid;

	private DataRow testeeInvalidGeburi;

	private String[] dataValid = {"Suter", "Alexander", "IT", "Developer", "11.02.1980", "Sdt", "756.8995.9219.99"};

	private String[] dataInvalidGeburi = {"Suter", "Alexander", "IT", "Developer", ".02.1980", "Sdt", "756.8995.9219.99"};


	@Before
	public void setUp() {
		List<PersonenImportColumnMapping> mappings = new ArrayList<>();
		mappings.add(new PersonenImportColumnMapping(PersonenAttribute.Name, new PersonenImportColumn(0, "Name")));
		mappings.add(new PersonenImportColumnMapping(PersonenAttribute.Vorname, new PersonenImportColumn(1, "Vorname")));
		mappings.add(new PersonenImportColumnMapping(PersonenAttribute.Einheit, new PersonenImportColumn(2, "Einheit")));
		mappings.add(new PersonenImportColumnMapping(PersonenAttribute.Funktion, new PersonenImportColumn(3, "Funktion")));
		mappings.add(new PersonenImportColumnMapping(PersonenAttribute.Geburtstag, new PersonenImportColumn(4, "Geburi")));
		mappings.add(new PersonenImportColumnMapping(PersonenAttribute.Grad, new PersonenImportColumn(5, "Grad")));
		mappings.add(new PersonenImportColumnMapping(PersonenAttribute.AHVNr, new PersonenImportColumn(6, "AHV")));

		testeeValid = new DataRow(dataValid, mappings);
		testeeInvalidGeburi = new DataRow(dataInvalidGeburi, mappings);
	}

	@Test
	public void testCreatePersonDetails() {
		PersonDetails details = testeeValid.createPersonDetails();
		Assert.assertEquals("Suter", details.getName());
		Assert.assertEquals("Alexander", details.getVorname());
		Assert.assertEquals("IT", details.getEinheitText());
		Assert.assertEquals("Developer", details.getFunktion());
		Assert.assertEquals(Grad.SDT.toString(), details.getGrad());
		Assert.assertEquals("756.8995.9219.99", details.getAhvNr());
		Assert.assertEquals("11.02.1980", DateFormater.formatToDate(details.getGeburtsdatum()));
	}

	@Test
	public void testGetGeburtstag() {
		Assert.assertEquals("11.02.1980", DateFormater.formatToDate(testeeValid.getGeburtstag()));
	}

	@Test
	public void testIsValid() {
		Assert.assertTrue(testeeValid.isValid());
		Assert.assertTrue(!testeeInvalidGeburi.isValid());

		if (testeeInvalidGeburi.validationErrorMessage().indexOf("Geburtstag") == -1) {
			Assert.fail("Geburi invalid aber keine Fehlermeldung.");
		}
	}

	@Test
	public void testGet() {
		Assert.assertTrue(testeeValid.isValid());
		Assert.assertTrue(!testeeInvalidGeburi.isValid());
	}
}
