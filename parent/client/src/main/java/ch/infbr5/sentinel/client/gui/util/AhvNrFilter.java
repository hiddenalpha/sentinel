package ch.infbr5.sentinel.client.gui.util;

import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;

public class AhvNrFilter extends DocumentFilter {

	private final Border trueBorder, falseBorder;
	private final JTextField textfield;

	public AhvNrFilter(final JTextField textfield, final Border falseBorder,
			final Border trueBorder) {
		this.falseBorder = falseBorder;
		this.trueBorder = trueBorder;
		this.textfield = textfield;
	}

	@Override
	public void remove(FilterBypass fb, int offset, int length)
			throws BadLocationException {
		super.remove(fb, offset, length);
		checkAhvNr(textfield.getText());
	}

	@Override
	public void replace(FilterBypass fb, int offs, int length, String str,
			AttributeSet a) throws BadLocationException {
		super.replace(fb, offs, length, str, a);
		checkAhvNr(textfield.getText());
	}

	private void checkAhvNr(String str) { // hier evtl. noch bissle was
											// schrauben :)
		if (textfield.isEditable()) {
			if (!isValidAhvNr(str)) {
				textfield.setBorder(falseBorder);
			} else {
				textfield.setBorder(trueBorder);
			}
		} else {
			textfield.setBorder(UIManager.getBorder("TextField.border"));
		}
	}

	private boolean isValidAhvNr(String nr) {
		int checksum = 0;

		if (nr.length() != 16)
			return false;

		try {
			checksum += Character.getNumericValue(nr.charAt(0));
			checksum += Character.getNumericValue(nr.charAt(1)) * 3;
			checksum += Character.getNumericValue(nr.charAt(2));
			if (!String.valueOf(nr.charAt(3)).equals("."))
				return false;

			checksum += Character.getNumericValue(nr.charAt(4)) * 3;
			checksum += Character.getNumericValue(nr.charAt(5));
			checksum += Character.getNumericValue(nr.charAt(6)) * 3;
			checksum += Character.getNumericValue(nr.charAt(7));
			if (!String.valueOf(nr.charAt(8)).equals("."))
				return false;

			checksum += Character.getNumericValue(nr.charAt(9)) * 3;
			checksum += Character.getNumericValue(nr.charAt(10));
			checksum += Character.getNumericValue(nr.charAt(11)) * 3;
			checksum += Character.getNumericValue(nr.charAt(12));
			if (!String.valueOf(nr.charAt(13)).equals("."))
				return false;

			checksum += Character.getNumericValue(nr.charAt(14)) * 3;
			int pruefziffer = Character.getNumericValue(nr.charAt(15));

			return ((checksum + pruefziffer) % 10 == 0);

		} catch (NumberFormatException ex) {
			return false;
		}
	}

}
