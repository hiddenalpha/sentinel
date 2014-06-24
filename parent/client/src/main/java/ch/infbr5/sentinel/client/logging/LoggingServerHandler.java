package ch.infbr5.sentinel.client.logging;

import java.util.logging.Handler;
import java.util.logging.LogRecord;

import javax.xml.ws.WebServiceException;

import ch.infbr5.sentinel.client.util.ConfigurationLocalHelper;
import ch.infbr5.sentinel.client.util.ServiceHelper;
import ch.infbr5.sentinel.client.wsgen.JournalService;
import ch.infbr5.sentinel.client.wsgen.JournalSystemMeldung;

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
				JournalSystemMeldung eintrag = new JournalSystemMeldung();
				eintrag.setLevel(record.getLevel().getName());
				eintrag.setMillis(record.getMillis());
				eintrag.setSequence(record.getSequenceNumber());
				eintrag.setThread(record.getThreadID());
				eintrag.setMessage(record.getMessage());
				eintrag.setMethod(record.getSourceMethodName());
				eintrag.setCheckpointId(ConfigurationLocalHelper.getConfig().getCheckpointId());
				eintrag.setLoggerClass(record.getLoggerName());
				eintrag.setOperator("operator");
				eintrag.setReportedClass(record.getSourceClassName());
				//service.addLogEintrag(eintrag);
			}
		} catch (WebServiceException e) {
			// do nothing.
		}
	}

}
