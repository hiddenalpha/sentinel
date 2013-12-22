package ch.infbr5.sentinel.client.logging;

import ch.infbr5.sentinel.client.wsgen.JournalService;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

import javax.xml.ws.WebServiceException;

import ch.infbr5.sentinel.client.util.ConfigurationLocalHelper;
import ch.infbr5.sentinel.client.util.ServiceHelper;

public class LoggingServerHandler extends Handler {

	@Override
	public void close() throws SecurityException {

	}

	@Override
	public void flush() {

	}

	@Override
	public void publish(LogRecord record) {

		try {
			JournalService service = ServiceHelper.getJournalService();
			if (service != null) {
				
				//TODO: Add operator
				
				service.addLogEintrag(record.getSourceClassName(), record.getLoggerName(), record.getMillis(),
						record.getSequenceNumber(), record.getLevel().getName(), record.getSourceMethodName(),
						record.getThreadID(), record.getMessage(), "Operator", ConfigurationLocalHelper.getConfig().getCheckpointId());

			}
		} catch (WebServiceException e) {
			// do nothing.
		}
	}

}
