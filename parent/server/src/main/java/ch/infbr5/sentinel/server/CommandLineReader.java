package ch.infbr5.sentinel.server;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

public class CommandLineReader {

	private String[] args;

	private String defaultIp;

	private String defaultPort;

	private boolean defaultDebugMode;

	public CommandLineReader(String[] args, String defaultIp, String defaultPort, boolean defaultDebugMode) {
		this.args = args;
		this.defaultIp = defaultIp;
		this.defaultPort = defaultPort;
		this.defaultDebugMode = defaultDebugMode;
	}

	public String getIp() {
		return getStringValue("ip", defaultIp);
	}

	public String getPort() {
		return getStringValue("port", defaultPort);
	}

	public boolean isDebugMode() {
		CommandLineParser parser = createParser();
		try {
			CommandLine line = parser.parse(createOptions(), args);
			if (line.hasOption("debug")) {
				return true;
			} else {
				return false;
			}
		} catch (ParseException exp) {
			return defaultDebugMode;
		}
	}

	private static CommandLineParser createParser() {
		return new GnuParser();
	}

	private static Options createOptions() {
		Options options = new Options();
		options.addOption(new Option("port", true, "Server Port"));
		options.addOption(new Option("ip", "ipAddress", true, "Server IP Adresse"));
		options.addOption(new Option("debug", "Printing Debugging Informationen"));
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
