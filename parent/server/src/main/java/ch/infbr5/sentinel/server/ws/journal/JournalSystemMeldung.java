package ch.infbr5.sentinel.server.ws.journal;


public class JournalSystemMeldung extends JournalEintrag {

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

	public void setMessage(String message) {
		this.message = message;
	}

	public String getLevel() {
		return level;
	}

	public void setLevel(String level) {
		this.level = level;
	}

	public String getLoggerClass() {
		return loggerClass;
	}

	public void setLoggerClass(String loggerClass) {
		this.loggerClass = loggerClass;
	}

	public String getCallerClass() {
		return callerClass;
	}

	public void setCallerClass(String callerClass) {
		this.callerClass = callerClass;
	}

	public String getCallerMethod() {
		return callerMethod;
	}

	public void setCallerMethod(String callerMethod) {
		this.callerMethod = callerMethod;
	}

	public String getCallerLineNumber() {
		return callerLineNumber;
	}

	public void setCallerLineNumber(String callerLineNumber) {
		this.callerLineNumber = callerLineNumber;
	}

	public String getCallerFilename() {
		return callerFilename;
	}

	public void setCallerFilename(String callerFilename) {
		this.callerFilename = callerFilename;
	}

}
