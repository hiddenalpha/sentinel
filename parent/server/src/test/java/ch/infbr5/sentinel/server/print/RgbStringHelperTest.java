package ch.infbr5.sentinel.server.print;

import static org.junit.Assert.*;

import org.junit.Ignore;
import org.junit.Test;

import ch.infbr5.sentinel.server.utils.RgbStringHelper;
@Ignore
public class RgbStringHelperTest {

	
	@Test
	public void convertTest() {
		RgbStringHelper color = new RgbStringHelper("11AA55");
		
		assertEquals(color.getR(),0x11);
		assertEquals(color.getG(),0xAA);
		assertEquals(color.getB(),0x55);
	}
}
