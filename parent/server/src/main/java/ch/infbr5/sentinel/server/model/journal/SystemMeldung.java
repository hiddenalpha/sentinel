package ch.infbr5.sentinel.server.model.journal;

import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;

@Entity
@NamedQueries({ @NamedQuery(name = "findLogEintrag", query = "SELECT r FROM SystemMeldung r WHERE r.checkpoint.id = :checkpointId order by r.millis desc"), })
public class SystemMeldung extends JournalEintrag {

	private String reportedClass;
	private String loggerClass;

	private long sequence;

	private String level;
	private String method;
	private int thread;
	private String message;

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
	private int type;
	private String operator;

}
