package ch.infbr5.sentinel.server.ws;

public class JournalResponse {

	private JournalEintragDetails[] records;

	public JournalEintragDetails[] getRecords() {
		return this.records;
	}

	public void setRecords(JournalEintragDetails[] records) {
		this.records = records;
	}

}
