package ch.infbr5.sentinel.server;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

public class CommandLineReader {

	private String[] args;

	private String defaultIp;

	private String defaultPort;

	private boolean defaultDebugMode;

	private boolean defaultHeadless;

	public CommandLineReader(String[] args, String defaultIp, String defaultPort, boolean defaultDebugMode, boolean defaultHeadless) {
		this.args = args;
		this.defaultIp = defaultIp;
		this.defaultPort = defaultPort;
		this.defaultDebugMode = defaultDebugMode;
		this.defaultHeadless = defaultHeadless;
	}

	public String getIp() {
		return getStringValue("ip", defaultIp);
	}

	public String getPort() {
		return getStringValue("port", defaultPort);
	}

	public boolean isDebugMode() {
		return getBooleanValue("debug", defaultDebugMode);
	}

	public boolean isHeadless() {
		return getBooleanValue("headless", defaultHeadless);
	}

	private boolean getBooleanValue(String name, boolean defaultValue) {
		CommandLineParser parser = createParser();
		try {
			CommandLine line = parser.parse(createOptions(), args);
			if (line.hasOption(name)) {
				return true;
			} else {
				return defaultValue;
			}
		} catch (ParseException exp) {
			return defaultValue;
		}
	}

	private static CommandLineParser createParser() {
		return new DefaultParser();
	}

	private static Options createOptions() {
		Options options = new Options();
		options.addOption(new Option("port", true, "Server Port"));
		options.addOption(new Option("ip", "ipAddress", true, "Server IP Adresse"));
		options.addOption(new Option("debug", "Printing Debugging Informationen"));
		options.addOption(new Option("headless", "Server ohne GUI"));
		return options;
	}

	private String getStringValue(String opt, String defaultValue) {
		CommandLineParser parser = createParser();
		try {
			CommandLine line = parser.parse(createOptions(), args);
			return line.getOptionValue(opt, defaultValue);
		} catch (ParseException exp) {
			return defaultValue;
		}
	}

}
