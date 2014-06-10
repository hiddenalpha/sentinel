package ch.infbr5.sentinel.client.util;


import org.junit.Assert;
import org.junit.Test;

public class EinheitDetailsClientTest {

	@Test
	public void getterAndSetter() {
		EinheitDetailsClient client = new EinheitDetailsClient(15l, "Ruedi");
		
		Assert.assertEquals(Long.valueOf(15l), client.getId());
		Assert.assertEquals("Ruedi", client.getName());
		
	}

}
