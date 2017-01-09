package ch.infbr5.sentinel.client.gui.util;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.KeyEvent;

public class Keyboard {

	private Robot robot;

	public Keyboard() throws AWTException {
		this.robot = new Robot();
	}

	public Keyboard(Robot robot) {
		this.robot = robot;
	}


	private void doType(int... keyCodes) {
		this.doType(keyCodes, 0, keyCodes.length);
	}

	private void doType(int[] keyCodes, int offset, int length) {
		if (length == 0) {
			return;
		}

		this.robot.keyPress(keyCodes[offset]);
		this.doType(keyCodes, offset + 1, length - 1);
		this.robot.keyRelease(keyCodes[offset]);
	}

	public void type(char character) {
		switch (character) {
		case 'a': this.doType(KeyEvent.VK_A); break;
		case 'b': this.doType(KeyEvent.VK_B); break;
		case 'c': this.doType(KeyEvent.VK_C); break;
		case 'd': this.doType(KeyEvent.VK_D); break;
		case 'e': this.doType(KeyEvent.VK_E); break;
		case 'f': this.doType(KeyEvent.VK_F); break;
		case 'g': this.doType(KeyEvent.VK_G); break;
		case 'h': this.doType(KeyEvent.VK_H); break;
		case 'i': this.doType(KeyEvent.VK_I); break;
		case 'j': this.doType(KeyEvent.VK_J); break;
		case 'k': this.doType(KeyEvent.VK_K); break;
		case 'l': this.doType(KeyEvent.VK_L); break;
		case 'm': this.doType(KeyEvent.VK_M); break;
		case 'n': this.doType(KeyEvent.VK_N); break;
		case 'o': this.doType(KeyEvent.VK_O); break;
		case 'p': this.doType(KeyEvent.VK_P); break;
		case 'q': this.doType(KeyEvent.VK_Q); break;
		case 'r': this.doType(KeyEvent.VK_R); break;
		case 's': this.doType(KeyEvent.VK_S); break;
		case 't': this.doType(KeyEvent.VK_T); break;
		case 'u': this.doType(KeyEvent.VK_U); break;
		case 'v': this.doType(KeyEvent.VK_V); break;
		case 'w': this.doType(KeyEvent.VK_W); break;
		case 'x': this.doType(KeyEvent.VK_X); break;
		case 'y': this.doType(KeyEvent.VK_Y); break;
		case 'z': this.doType(KeyEvent.VK_Z); break;
		case 'A': this.doType(KeyEvent.VK_SHIFT, KeyEvent.VK_A); break;
		case 'B': this.doType(KeyEvent.VK_SHIFT, KeyEvent.VK_B); break;
		case 'C': this.doType(KeyEvent.VK_SHIFT, KeyEvent.VK_C); break;
		case 'D': this.doType(KeyEvent.VK_SHIFT, KeyEvent.VK_D); break;
		case 'E': this.doType(KeyEvent.VK_SHIFT, KeyEvent.VK_E); break;
		case 'F': this.doType(KeyEvent.VK_SHIFT, KeyEvent.VK_F); break;
		case 'G': this.doType(KeyEvent.VK_SHIFT, KeyEvent.VK_G); break;
		case 'H': this.doType(KeyEvent.VK_SHIFT, KeyEvent.VK_H); break;
		case 'I': this.doType(KeyEvent.VK_SHIFT, KeyEvent.VK_I); break;
		case 'J': this.doType(KeyEvent.VK_SHIFT, KeyEvent.VK_J); break;
		case 'K': this.doType(KeyEvent.VK_SHIFT, KeyEvent.VK_K); break;
		case 'L': this.doType(KeyEvent.VK_SHIFT, KeyEvent.VK_L); break;
		case 'M': this.doType(KeyEvent.VK_SHIFT, KeyEvent.VK_M); break;
		case 'N': this.doType(KeyEvent.VK_SHIFT, KeyEvent.VK_N); break;
		case 'O': this.doType(KeyEvent.VK_SHIFT, KeyEvent.VK_O); break;
		case 'P': this.doType(KeyEvent.VK_SHIFT, KeyEvent.VK_P); break;
		case 'Q': this.doType(KeyEvent.VK_SHIFT, KeyEvent.VK_Q); break;
		case 'R': this.doType(KeyEvent.VK_SHIFT, KeyEvent.VK_R); break;
		case 'S': this.doType(KeyEvent.VK_SHIFT, KeyEvent.VK_S); break;
		case 'T': this.doType(KeyEvent.VK_SHIFT, KeyEvent.VK_T); break;
		case 'U': this.doType(KeyEvent.VK_SHIFT, KeyEvent.VK_U); break;
		case 'V': this.doType(KeyEvent.VK_SHIFT, KeyEvent.VK_V); break;
		case 'W': this.doType(KeyEvent.VK_SHIFT, KeyEvent.VK_W); break;
		case 'X': this.doType(KeyEvent.VK_SHIFT, KeyEvent.VK_X); break;
		case 'Y': this.doType(KeyEvent.VK_SHIFT, KeyEvent.VK_Y); break;
		case 'Z': this.doType(KeyEvent.VK_SHIFT, KeyEvent.VK_Z); break;
		case '`': this.doType(KeyEvent.VK_BACK_QUOTE); break;
		case '0': this.doType(KeyEvent.VK_0); break;
		case '1': this.doType(KeyEvent.VK_1); break;
		case '2': this.doType(KeyEvent.VK_2); break;
		case '3': this.doType(KeyEvent.VK_3); break;
		case '4': this.doType(KeyEvent.VK_4); break;
		case '5': this.doType(KeyEvent.VK_5); break;
		case '6': this.doType(KeyEvent.VK_6); break;
		case '7': this.doType(KeyEvent.VK_7); break;
		case '8': this.doType(KeyEvent.VK_8); break;
		case '9': this.doType(KeyEvent.VK_9); break;
		case '-': this.doType(KeyEvent.VK_MINUS); break;
		case '=': this.doType(KeyEvent.VK_EQUALS); break;
		case '~': this.doType(KeyEvent.VK_SHIFT, KeyEvent.VK_BACK_QUOTE); break;
		case '!': this.doType(KeyEvent.VK_EXCLAMATION_MARK); break;
		case '@': this.doType(KeyEvent.VK_AT); break;
		case '#': this.doType(KeyEvent.VK_NUMBER_SIGN); break;
		case '$': this.doType(KeyEvent.VK_DOLLAR); break;
		case '%': this.doType(KeyEvent.VK_SHIFT, KeyEvent.VK_5); break;
		case '^': this.doType(KeyEvent.VK_CIRCUMFLEX); break;
		case '&': this.doType(KeyEvent.VK_AMPERSAND); break;
		case '*': this.doType(KeyEvent.VK_ASTERISK); break;
		case '(': this.doType(KeyEvent.VK_LEFT_PARENTHESIS); break;
		case ')': this.doType(KeyEvent.VK_RIGHT_PARENTHESIS); break;
		case '_': this.doType(KeyEvent.VK_UNDERSCORE); break;
		case '+': this.doType(KeyEvent.VK_PLUS); break;
		case '\t': this.doType(KeyEvent.VK_TAB); break;
		case '\n': this.doType(KeyEvent.VK_ENTER); break;
		case '[': this.doType(KeyEvent.VK_OPEN_BRACKET); break;
		case ']': this.doType(KeyEvent.VK_CLOSE_BRACKET); break;
		case '\\': this.doType(KeyEvent.VK_BACK_SLASH); break;
		case '{': this.doType(KeyEvent.VK_SHIFT, KeyEvent.VK_OPEN_BRACKET); break;
		case '}': this.doType(KeyEvent.VK_SHIFT, KeyEvent.VK_CLOSE_BRACKET); break;
		case '|': this.doType(KeyEvent.VK_SHIFT, KeyEvent.VK_BACK_SLASH); break;
		case ';': this.doType(KeyEvent.VK_SEMICOLON); break;
		case ':': this.doType(KeyEvent.VK_COLON); break;
		case '\'': this.doType(KeyEvent.VK_QUOTE); break;
		case '"': this.doType(KeyEvent.VK_QUOTEDBL); break;
		case ',': this.doType(KeyEvent.VK_COMMA); break;
		case '<': this.doType(KeyEvent.VK_LESS); break;
		case '.': this.doType(KeyEvent.VK_PERIOD); break;
		case '>': this.doType(KeyEvent.VK_GREATER); break;
		case '/': this.doType(KeyEvent.VK_SLASH); break;
		case '?': this.doType(KeyEvent.VK_SHIFT, KeyEvent.VK_SLASH); break;
		case ' ': this.doType(KeyEvent.VK_SPACE); break;
		default:
			throw new IllegalArgumentException("Cannot type character " + character);
		}
	}

	public void type(CharSequence characters) {
		int length = characters.length();
		for (int i = 0; i < length; i++) {
			char character = characters.charAt(i);
			this.type(character);
		}
	}

}
