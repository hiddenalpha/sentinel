package ch.infbr5.sentinel.server.ws.journal;

import java.util.Calendar;
import java.util.List;
import java.util.logging.Logger;

import javax.jws.HandlerChain;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.xml.ws.soap.MTOM;

import ch.infbr5.sentinel.server.db.EntityManagerHelper;
import ch.infbr5.sentinel.server.db.QueryHelper;
import ch.infbr5.sentinel.server.mapper.Mapper;
import ch.infbr5.sentinel.server.model.journal.BewegungsMeldung;
import ch.infbr5.sentinel.server.model.journal.GefechtsMeldung;
import ch.infbr5.sentinel.server.model.journal.SystemMeldung;

import com.google.common.collect.Lists;

@MTOM
@WebService(name = "JournalService", targetNamespace = "http://ws.sentinel.infbr5.ch/")
@HandlerChain(file = "/META-INF/ws-handler-chain.xml")
@SOAPBinding(style = SOAPBinding.Style.RPC)
public class JournalService {

	private static Logger log = Logger.getLogger(JournalService.class.getName());

	private static final int MAXIMAL_COUNT_RESULTS = 10;

	@WebMethod
	public void addSystemMeldung(@WebParam(name = "meldung") JournalSystemMeldung meldung) {
		SystemMeldung record = Mapper.mapJournalSystemMeldungToSystemMeldung().apply(meldung);
		EntityManagerHelper.getEntityManager().persist(record);
	}

	@WebMethod
	public void addBewegungsMeldung(@WebParam(name = "meldung") JournalBewegungsMeldung meldung) {
		BewegungsMeldung record = Mapper.mapJournalBewegungsMeldungToBewegungsMeldung().apply(meldung);
		EntityManagerHelper.getEntityManager().persist(record);
	}

	@WebMethod
	public void addGefechtsMeldung(@WebParam(name = "meldung") JournalGefechtsMeldung meldung) {
		log.info("Neue Gefechtsmeldung am Checkpoint " + meldung.getCheckpointId() + " erfasst.");
		GefechtsMeldung record = Mapper.mapJournalGefechtsMeldungToGefechtsMeldung().apply(meldung);
		EntityManagerHelper.getEntityManager().persist(record);
	}

	@WebMethod
	public void updateGefechtsMeldung(@WebParam(name = "meldung") JournalGefechtsMeldung meldung) {
		log.info("Gefechtsmeldung wird aktualisiert");
		GefechtsMeldung gefechtsMeldung = QueryHelper.getGefechtsMeldungen(meldung.getId());

		if (!gefechtsMeldung.isIstErledigt()) {
			if (meldung.isIstErledigt()) {
				gefechtsMeldung.setZeitpunktErledigt(Calendar.getInstance());
			}
		}

		if (gefechtsMeldung.isIstErledigt()) {
			if (!meldung.isIstErledigt()) {
				gefechtsMeldung.setZeitpunktErledigt(null);
			}
		}

		gefechtsMeldung.setIstErledigt(meldung.isIstErledigt());
	}

	@WebMethod
	public JournalResponse getSystemJournal(@WebParam(name = "checkpointId") long checkpointId) {
		List<SystemMeldung> data = QueryHelper.getSystemMeldungen(checkpointId, MAXIMAL_COUNT_RESULTS);
		List<JournalSystemMeldung> eintraege = Lists.transform(data, Mapper.mapSystemMeldungToJournalSystemMeldung());

		JournalResponse response = new JournalResponse();
		response.setSystemMeldungen(eintraege);
		return response;
	}

	@WebMethod
	public JournalResponse getBewegungsJournal(@WebParam(name = "checkpointId") long checkpointId) {
		List<BewegungsMeldung> data = QueryHelper.getBewegungsMeldungen(checkpointId, MAXIMAL_COUNT_RESULTS);
		List<JournalBewegungsMeldung> eintraege = Lists.transform(data, Mapper.mapBewegungsMeldungToJournalBewegungsMeldung());

		JournalResponse response = new JournalResponse();
		response.setBewegungsMeldungen(eintraege);
		return response;
	}

	@WebMethod
	public JournalResponse getGefechtsJournal(@WebParam(name = "checkpointId") long checkpointId) {
		List<GefechtsMeldung> data = QueryHelper.getGefechtsMeldungen(checkpointId, MAXIMAL_COUNT_RESULTS);
		List<JournalGefechtsMeldung> eintraege = Lists.transform(data, Mapper.mapGefechtsMeldungToJournalGefechtsMeldung());

		JournalResponse response = new JournalResponse();
		response.setGefechtsMeldungen(eintraege);
		return response;
	}

}
