package ch.infbr5.sentinel.utils;

public class Login {
	public static boolean authenticate(String username, String password) {
		if (username.equals("inf") && password.equals("br5")) {
			return true;
		}

		return false;
	}
}
