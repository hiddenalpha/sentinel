package ch.infbr5.sentinel.server.ws.journal;

import java.util.List;

public class JournalResponse {

	private List<JournalSystemMeldung> systemMeldungen;
	
	private List<JournalBewegungsMeldung> bewegungsMeldungen;
	
	private List<JournalGefechtsMeldung> gefechtsMeldungen;

	public List<JournalSystemMeldung> getSystemMeldungen() {
		return systemMeldungen;
	}

	public void setSystemMeldungen(List<JournalSystemMeldung> systemMeldungen) {
		this.systemMeldungen = systemMeldungen;
	}

	public List<JournalBewegungsMeldung> getBewegungsMeldungen() {
		return bewegungsMeldungen;
	}

	public void setBewegungsMeldungen(
			List<JournalBewegungsMeldung> bewegungsMeldungen) {
		this.bewegungsMeldungen = bewegungsMeldungen;
	}

	public List<JournalGefechtsMeldung> getGefechtsMeldungen() {
		return gefechtsMeldungen;
	}

	public void setGefechtsMeldungen(List<JournalGefechtsMeldung> gefechtsMeldungen) {
		this.gefechtsMeldungen = gefechtsMeldungen;
	}
	
}
