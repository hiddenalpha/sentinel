package ch.infbr5.sentinel.client.logging;

import java.util.ArrayList;
import java.util.List;

import javax.xml.ws.WebServiceException;

import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.spi.LoggingEvent;

import ch.infbr5.sentinel.client.config.ConfigurationLocalHelper;
import ch.infbr5.sentinel.client.util.ServiceHelper;
import ch.infbr5.sentinel.client.wsgen.JournalService;
import ch.infbr5.sentinel.client.wsgen.JournalSystemMeldung;

public class SystemMeldungAppender extends AppenderSkeleton {

   private final List<JournalSystemMeldung> cache = new ArrayList<>();

   private final Object cacheLock = new Object();

   @Override
   public void close() {
      // Do nothing
   }

   @Override
   public boolean requiresLayout() {
      return false;
   }

   @Override
   protected void append(final LoggingEvent event) {
      try {
         final JournalSystemMeldung meldung = createJournalEintrag(event);
         final JournalService service = ServiceHelper.getJournalService();

         synchronized (cacheLock) {
            cache.add(meldung);
            if (service != null) {
               for (final JournalSystemMeldung eintrag : cache) {
                  service.addSystemMeldung(eintrag);
               }
               cache.clear();
            }
         }
      } catch (final WebServiceException e) {
         System.out.println("Fehler beim Aufruf des Webservices zum Loggen: " + e.getMessage());
      }
   }

   private JournalSystemMeldung createJournalEintrag(final LoggingEvent event) {
      final JournalSystemMeldung eintrag = new JournalSystemMeldung();

      // Generelle Informationen
      eintrag.setCheckpoint(ConfigurationLocalHelper.getConfig().getCheckpoint());
      eintrag.setMessage(event.getMessage().toString());
      eintrag.setLevel(event.getLevel().toString());
      eintrag.setMillis(event.getTimeStamp());

      // Zusätzliche Informationen
      eintrag.setLoggerClass(event.getFQNOfLoggerClass());
      eintrag.setCallerClass(event.getLocationInformation().getClassName());
      eintrag.setCallerMethod(event.getLocationInformation().getMethodName());
      eintrag.setCallerLineNumber(event.getLocationInformation().getLineNumber());
      eintrag.setCallerFilename(event.getLocationInformation().getFileName());

      return eintrag;
   }

}
