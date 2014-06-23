package ch.infbr5.sentinel.client.wsgen;


import org.junit.Assert;
import org.junit.Test;

public class PersonenAttributeTest {

	@Test
	public void fromValue() {
		PersonenAttribute attribute = PersonenAttribute.fromValue("AHVNr");
		Assert.assertEquals(PersonenAttribute.AHV_NR, attribute);
	}

}
