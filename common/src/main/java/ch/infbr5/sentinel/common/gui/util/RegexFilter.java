package ch.infbr5.sentinel.common.gui.util;

import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;

class RegexFilter extends DocumentFilter {

	private final String pattern;
	private final Border trueBorder, falseBorder;
	private final JTextField textfield;

	public RegexFilter(final String pattern, final JTextField textfield,
			final Border falseBorder, final Border trueBorder) {
		this.pattern = pattern;
		this.falseBorder = falseBorder;
		this.trueBorder = trueBorder;
		this.textfield = textfield;
	}

	@Override
	public void remove(FilterBypass fb, int offset, int length)
			throws BadLocationException {
		super.remove(fb, offset, length);
		checkRegex(textfield.getText());
	}

	@Override
	public void replace(FilterBypass fb, int offs, int length, String str,
			AttributeSet a) throws BadLocationException {
		super.replace(fb, offs, length, str, a);
		checkRegex(textfield.getText());
	}

	private void checkRegex(String str) { // hier evtl. noch bissle was
											// schrauben :)
		if (textfield.isEditable()) {
			if (!str.matches(pattern)) {
				textfield.setBorder(falseBorder);
			} else {
				textfield.setBorder(trueBorder);
			}
		} else {
			textfield.setBorder(UIManager.getBorder("TextField.border"));
		}
	}

}
