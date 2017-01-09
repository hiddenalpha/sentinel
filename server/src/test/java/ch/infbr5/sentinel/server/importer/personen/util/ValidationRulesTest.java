package ch.infbr5.sentinel.server.importer.personen.util;

import org.junit.Assert;
import org.junit.Test;

public class ValidationRulesTest {

	@Test
	public void testValidGeburi() {
		Assert.assertTrue(ValidationRules.isValidGeburtstag("15.16.2014"));
		Assert.assertTrue(!ValidationRules.isValidGeburtstag("1516.2014"));
		Assert.assertTrue(!ValidationRules.isValidGeburtstag(null));
		Assert.assertTrue(!ValidationRules.isValidGeburtstag("756.5211.1421.07"));
	}
	
	@Test
	public void testValidAhvNr() {
		Assert.assertTrue(ValidationRules.isValidAhvNr("756.5211.1421.07"));
		Assert.assertTrue(!ValidationRules.isValidAhvNr(null));
		Assert.assertTrue(!ValidationRules.isValidAhvNr("756.5211.1421.08"));
	}
	
	@Test
	public void testValidGrad() {
		Assert.assertTrue(ValidationRules.isValidGrad("Sdt"));
		Assert.assertTrue(ValidationRules.isValidGrad("Sdt."));
		Assert.assertTrue(!ValidationRules.isValidGrad("Sd"));
		Assert.assertTrue(!ValidationRules.isValidGrad(null));
		Assert.assertTrue(!ValidationRules.isValidGrad("756.5211.1421.07"));
	}

}
