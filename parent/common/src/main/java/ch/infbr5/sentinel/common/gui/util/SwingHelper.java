package ch.infbr5.sentinel.common.gui.util;

import java.awt.Color;
import java.awt.Font;
import java.awt.KeyboardFocusManager;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.border.CompoundBorder;
import javax.swing.border.LineBorder;
import javax.swing.text.AbstractDocument;

public class SwingHelper {

	public static final Color COLOR_RED = new Color(240, 230, 140);

	public static final Color COLOR_GREEN = new Color(193, 255, 193);

	private static final Color LABEL_COLOR = new Color(0, 70, 213);;

	public static void addSeparator(JPanel panel, String text) {
		JLabel l = createLabel(text);
		l.setForeground(LABEL_COLOR);

		panel.add(l, "gapbottom 1, span, split 2, aligny center");
		panel.add(new JSeparator(), "gapleft rel, growx");
	}

	public static JLabel createLabel(String text) {
		return createLabel(text, SwingConstants.LEADING);
	}

	public static JLabel createLabel(String text, int align) {
		return new JLabel(text, align);
	}

	public static JTextField createTextField(int cols) {
		return createTextField("", cols, "");
	}

	public static JTextField createTextField(String text) {
		return createTextField(text, 0, "");
	}

	public static JTextField createTextField(int cols, String regex) {
		return createTextField("", cols, regex);
	}

	public static JTextField createTextField(String text, String regex) {
		return createTextField(text, 0, regex);
	}

	public static JTextField createTextField(String text, int cols, String regex) {
		final JTextField b = new JTextField(text, cols);
		if (regex.equalsIgnoreCase("[ahvnr]")) {
			((AbstractDocument) b.getDocument()).setDocumentFilter(new AhvNrFilter(b, new LineBorder(Color.RED),
					new LineBorder(Color.GREEN)));
		} else if (regex != "") {
			((AbstractDocument) b.getDocument()).setDocumentFilter(new RegexFilter(regex, b, new LineBorder(Color.RED),
					new LineBorder(Color.GREEN)));
		}
		return b;
	}

	public static JTextArea createTextArea(int rows, int columns) {
		return createTextArea("", rows, columns);
	}

	public static JTextArea createTextArea(String text, int rows, int columns) {
		return new JTextArea(text, rows, columns);
	}

	public static JCheckBox createCheckBox() {
		return new JCheckBox();
	}

	public static Font smaller(Font font) {
		return new Font(font.getName(), font.getStyle(), font.getSize() - 3);
	}

	public static void patchKeystrokeTab(JComponent component) {
		Set<KeyStroke> strokes = new HashSet<KeyStroke>(Arrays.asList(KeyStroke.getKeyStroke("pressed TAB")));
		component.setFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS, strokes);
		strokes = new HashSet<KeyStroke>(Arrays.asList(KeyStroke.getKeyStroke("shift pressed TAB")));
		component.setFocusTraversalKeys(KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS, strokes);
	}

	public static void attachLabledBorder(String label, JComponent component) {
		CompoundBorder bordering = BorderFactory.createCompoundBorder(BorderFactory.createTitledBorder(label),BorderFactory.createEmptyBorder(5, 5, 5, 5));
		component.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5), bordering));
	}

}
