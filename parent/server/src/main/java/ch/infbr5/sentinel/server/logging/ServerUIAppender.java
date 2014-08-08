package ch.infbr5.sentinel.server.logging;

import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.spi.LoggingEvent;

import ch.infbr5.sentinel.server.gui.ApplicationFrame;

public class ServerUIAppender extends AppenderSkeleton {

	@Override
	public void close() {
		// Do nothing
	}

	@Override
	public boolean requiresLayout() {
		return false;
	}

	@Override
	protected void append(LoggingEvent event) {
		ApplicationFrame.app.addText(event);
	}

}
