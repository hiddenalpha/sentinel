package ch.infbr5.sentinel.server.print;

public class CodeHelper {

	public static int getNoOfCharMod10(String text) {
		if (text != null) {
			return text.length() % 10;
		} else {
			return 0;
		}
	}

}
