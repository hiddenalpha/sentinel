package ch.infbr5.sentinel.server.ws;


public class JournalEintragDetails {

	private long id;

	private String reportedClass;
	private String loggerClass;

	private long millis;
	private long sequence;

	private String level;
	private String method;
	private int thread;
	private String message;
	private long checkpointId;

	private int type;
	private String operator;

	
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getReportedClass() {
		return reportedClass;
	}

	public void setReportedClass(String reportedClass) {
		this.reportedClass = reportedClass;
	}

	public String getLoggerClass() {
		return loggerClass;
	}

	public void setLoggerClass(String loggerClass) {
		this.loggerClass = loggerClass;
	}

	public long getMillis() {
		return millis;
	}

	public void setMillis(long millis) {
		this.millis = millis;
	}

	public long getSequence() {
		return sequence;
	}

	public void setSequence(long sequence) {
		this.sequence = sequence;
	}

	public String getLevel() {
		return level;
	}

	public void setLevel(String level) {
		this.level = level;
	}

	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	public int getThread() {
		return thread;
	}

	public void setThread(int thread) {
		this.thread = thread;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public long getCheckpointId() {
		return checkpointId;
	}

	public void setCheckpointId(long checkpointId) {
		this.checkpointId = checkpointId;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getOperator() {
		return operator;
	}

	public void setOperator(String operator) {
		this.operator = operator;
	}

}
