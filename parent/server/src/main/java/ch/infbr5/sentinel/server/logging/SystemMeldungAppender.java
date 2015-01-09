package ch.infbr5.sentinel.server.logging;

import javax.persistence.EntityManager;

import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.spi.LoggingEvent;

import ch.infbr5.sentinel.server.db.EntityManagerHelper;
import ch.infbr5.sentinel.server.model.journal.SystemMeldung;

public class SystemMeldungAppender extends AppenderSkeleton {

   public static boolean ENABLE = false;

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
      if (!ENABLE) {
         return;
      }

      final SystemMeldung meldung = new SystemMeldung();

      // Generelle Informationen
      meldung.setCheckpoint(null); // TODO In der Regel laeuft ein Serveraktion
      // im Kontext eines Client-Requests ab.
      // Hier sollte man dann den Checkpoint
      // eintragen.
      meldung.setMessage(event.getMessage().toString());
      meldung.setLevel(event.getLevel().toString());
      meldung.setMillis(event.getTimeStamp());

      // Zusätzliche Informationen
      meldung.setLoggerClass(event.getFQNOfLoggerClass());
      meldung.setCallerClass(event.getLocationInformation().getClassName());
      meldung.setCallerMethod(event.getLocationInformation().getMethodName());
      meldung.setCallerLineNumber(event.getLocationInformation().getLineNumber());
      meldung.setCallerFilename(event.getLocationInformation().getFileName());

      final EntityManager em = EntityManagerHelper.createEntityManager();
      em.getTransaction().begin();
      em.persist(meldung);
      em.getTransaction().commit();
      em.close();
   }

}
