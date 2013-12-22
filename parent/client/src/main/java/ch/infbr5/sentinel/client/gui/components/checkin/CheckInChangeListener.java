package ch.infbr5.sentinel.client.gui.components.checkin;

import java.util.EventListener;

public interface CheckInChangeListener extends EventListener {
	public void valueChanged(CheckInChangedEvent e);
}
