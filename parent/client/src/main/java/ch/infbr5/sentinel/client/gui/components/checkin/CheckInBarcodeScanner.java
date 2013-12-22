package ch.infbr5.sentinel.client.gui.components.checkin;

import java.awt.AWTException;
import java.awt.KeyEventDispatcher;
import java.awt.event.KeyEvent;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import ch.infbr5.sentinel.client.gui.util.Keyboard;

public class CheckInBarcodeScanner implements KeyEventDispatcher {

	private class MyTimerTask extends TimerTask {
		@Override
		public void run() {
			if (new Date().getTime() - CheckInBarcodeScanner.this.timeLastKeyTyped > 50) {
				CheckInBarcodeScanner.this.redispatch();
			}
		}
	}

	private static final char BARCODE_PREFIX = 'A';
	private boolean receiveBarcode = false;
	private StringBuffer buffer;
	private Long timeLastKeyTyped;
	private char charLastKeyTyped;
	private int ignore = 0;
	private CheckInModel model;
	private Timer timer;

	private boolean isAltPressed = false;

	public CheckInBarcodeScanner(CheckInModel newModel) {
		this.model = newModel;
	}

	public boolean dispatchKeyEvent(KeyEvent e) {
		if (e.getID() == KeyEvent.KEY_TYPED) {
			if (this.receiveBarcode) {
				if (!Character.isLetterOrDigit(e.getKeyChar())) {
					this.fireBarcodeEvent();
				}
			} else {
				if (Character.toUpperCase(e.getKeyChar()) == CheckInBarcodeScanner.BARCODE_PREFIX) {
					if (this.ignore > 0) {
						this.ignore = this.ignore - 1;
					} else {
						this.startBarcodeReading();
					}
				}
			}

			if (this.buffer != null) {
				this.buffer.append(e.getKeyChar());
				this.timeLastKeyTyped = new Date().getTime();
				this.charLastKeyTyped = e.getKeyChar();
			}

		} else if (!this.receiveBarcode && (e.getID() == KeyEvent.KEY_PRESSED)) {
			switch (e.getKeyCode()) {
			case KeyEvent.VK_F1:
				this.model.setOperation(CheckInOperation.CHECKIN);
				break;
			case KeyEvent.VK_F2:
				this.model.setOperation(CheckInOperation.CHECKOUT);
				break;
			case KeyEvent.VK_F3:
				this.model.setOperation(CheckInOperation.URLAUB);
				break;
			case KeyEvent.VK_F4:
				this.model.setOperation(CheckInOperation.ANMELDEN);
				break;
			case KeyEvent.VK_F5:
				this.model.setOperation(CheckInOperation.ABMELDEN);
				break;
			case KeyEvent.VK_ALT:
				this.model.setOperation(CheckInOperation.CHECKOUT);
				this.isAltPressed = true;
				break;

			default:

			}
		} else if (!this.receiveBarcode && (e.getID() == KeyEvent.KEY_RELEASED) && this.isAltPressed) {
			this.isAltPressed  = false;
			this.model.setOperation(CheckInOperation.CHECKIN);
		}

		return this.receiveBarcode;
	}

	private void fireBarcodeEvent() {
		String barcode = this.buffer.toString();
		this.receiveBarcode = false;
		this.buffer = null;
		this.timer.cancel();
		this.timer.purge();

		this.model.handleCheckinEvent(barcode);
	}

	private void redispatch() {
		this.receiveBarcode = false;
		this.buffer = null;
		this.timer.cancel();
		this.timer.purge();

		this.ignore = 1;
		try {
			Keyboard k = new Keyboard();
			k.type(this.charLastKeyTyped);
		} catch (AWTException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void startBarcodeReading() {
		this.buffer = new StringBuffer();
		this.receiveBarcode = true;
		this.timer = new Timer();
		this.timer.schedule(new MyTimerTask(), 50, 50);
	}
}
