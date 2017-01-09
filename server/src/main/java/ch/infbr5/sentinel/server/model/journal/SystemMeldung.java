package ch.infbr5.sentinel.server.model.journal;

import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;

@Entity
@NamedQueries({
   @NamedQuery(name = "findSystemMeldungenSeit", query = "SELECT r FROM SystemMeldung r WHERE r.millis > :timeInMillis order by r.millis desc"),
   @NamedQuery(name = "findSystemMeldungen", query = "SELECT r FROM SystemMeldung r ORDER BY r.millis DESC") })
public class SystemMeldung extends JournalEintrag {

   private String message;

   private String level;

   private String loggerClass;

   private String callerClass;

   private String callerMethod;

   private String callerLineNumber;

   private String callerFilename;

   public String getMessage() {
      return message;
   }

   public void setMessage(final String message) {
      this.message = message;
   }

   public String getLevel() {
      return level;
   }

   public void setLevel(final String level) {
      this.level = level;
   }

   public String getLoggerClass() {
      return loggerClass;
   }

   public void setLoggerClass(final String loggerClass) {
      this.loggerClass = loggerClass;
   }

   public String getCallerClass() {
      return callerClass;
   }

   public void setCallerClass(final String callerClass) {
      this.callerClass = callerClass;
   }

   public String getCallerMethod() {
      return callerMethod;
   }

   public void setCallerMethod(final String callerMethod) {
      this.callerMethod = callerMethod;
   }

   public String getCallerLineNumber() {
      return callerLineNumber;
   }

   public void setCallerLineNumber(final String callerLineNumber) {
      this.callerLineNumber = callerLineNumber;
   }

   public String getCallerFilename() {
      return callerFilename;
   }

   public void setCallerFilename(final String callerFilename) {
      this.callerFilename = callerFilename;
   }

}
