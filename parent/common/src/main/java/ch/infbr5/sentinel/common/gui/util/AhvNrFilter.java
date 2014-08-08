package ch.infbr5.sentinel.common.gui.util;

import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;

import ch.infbr5.sentinel.common.validator.AhvNrValidator;
import ch.infbr5.sentinel.common.validator.CommonValidator;

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
		CommonValidator validator = new AhvNrValidator();
		return validator.validate(nr);
	}

}
