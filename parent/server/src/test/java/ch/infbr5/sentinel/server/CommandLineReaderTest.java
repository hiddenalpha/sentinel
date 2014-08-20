package ch.infbr5.sentinel.server;

import org.junit.Assert;
import org.junit.Test;

public class CommandLineReaderTest {

	private CommandLineReader testee;

	@Test
	public void testIpAddress() {
		String args[] = {"-ip" , "15.46.17.19"};
		testee = new CommandLineReader(args, "1745", "8080", false, false);

		Assert.assertEquals("15.46.17.19", testee.getIp());

		String args2[] = {"-ipAddress" , "15.46.14.19"};
		testee = new CommandLineReader(args2, "199", "8080", false, false);
		Assert.assertEquals("15.46.14.19", testee.getIp());

		String args3[] = {"-ipAddrdddess" , "15.46.14.19"};
		testee = new CommandLineReader(args3, "199", "8080", false, false);
		Assert.assertEquals("199", testee.getIp());
	}

	@Test
	public void testPort() {
		String args[] = {"-port" , "9090"};
		testee = new CommandLineReader(args, "1745", "8080", false, false);
		Assert.assertEquals("9090", testee.getPort());


		String args3[] = {"-portting" , "15.46.14.19"};
		testee = new CommandLineReader(args3, "199", "8080", false, false);
		Assert.assertEquals("8080", testee.getPort());
	}

	@Test
	public void testDebugMode() {
		String args[] = {"-debug"};
		testee = new CommandLineReader(args, null, "8080", false, false);
		Assert.assertTrue(testee.isDebugMode());

		String args2[] = {};
		testee = new CommandLineReader(args2, null, "8080", false, false);
		Assert.assertFalse(testee.isDebugMode());
	}
	@Test
	public void testHeadless() {
		String args[] = {"-headless"};
		testee = new CommandLineReader(args, null, "8080", false, false);
		Assert.assertTrue(testee.isHeadless());

		String args2[] = {};
		testee = new CommandLineReader(args2, null, "8080", false, false);
		Assert.assertFalse(testee.isHeadless());
	}

}
