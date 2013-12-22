package ch.infbr5.sentinel.server.utils;

public class RgbStringHelper {
	private int r = 0;
	private int g = 0;
	private int b = 0;

	public RgbStringHelper(String color) {

		if (color != null) {
			
			if (color.length() >= 2) {
				try {
					r = Integer.parseInt(color.substring(0, 2),16);
				} catch (NumberFormatException e) {
					r = 0;
				}
			}

			if (color.length() >= 4) {
				try {
					g = Integer.parseInt(color.substring(2, 4),16);
				} catch (NumberFormatException e) {
					g = 0;
				}
			}

			if (color.length() >= 6) {
				try {
					b = Integer.parseInt(color.substring(4, 6),16);
				} catch (NumberFormatException e) {
					b = 0;
				}
			}

		}

	}

	public int getR() {
		return r;
	}

	public int getG() {
		return g;
	}

	public int getB() {
		return b;
	}

}
