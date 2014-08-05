package ch.infbr5.sentinel.server.ws.journal;

import java.util.Calendar;
import java.util.List;

import javax.annotation.Resource;
import javax.jws.HandlerChain;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.persistence.EntityManager;
import javax.xml.ws.WebServiceContext;
import javax.xml.ws.soap.MTOM;

import org.apache.log4j.Logger;

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

	@Resource
	private WebServiceContext context;

	private static Logger log = Logger.getLogger(JournalService.class);

	@WebMethod
	public void addSystemMeldung(@WebParam(name = "meldung") JournalSystemMeldung meldung) {
		SystemMeldung record = Mapper.mapJournalSystemMeldungToSystemMeldung().apply(meldung);
		EntityManagerHelper.getEntityManager(context).persist(record);
	}

	@WebMethod
	public void addGefechtsMeldung(@WebParam(name = "meldung") JournalGefechtsMeldung meldung) {
		log.info("Neue Gefechtsmeldung am Checkpoint " + meldung.getCheckpoint().getName() + " erfasst.");
		GefechtsMeldung record = Mapper.mapJournalGefechtsMeldungToGefechtsMeldung(getEntityManager()).apply(meldung);
		EntityManagerHelper.getEntityManager(context).persist(record);
	}

	@WebMethod
	public void updateGefechtsMeldung(@WebParam(name = "meldung") JournalGefechtsMeldung meldung) {
		log.info("Gefechtsmeldung wird aktualisiert");
		GefechtsMeldung gefechtsMeldung = getQueryHelper().getGefechtsMeldungen(meldung.getId());

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
	public JournalResponse getSystemJournalSeit(@WebParam(name = "timeInMillis") long timeInMillis) {
		List<SystemMeldung> data = getQueryHelper().getSystemMeldungenSeit(timeInMillis);
		List<JournalSystemMeldung> eintraege = Lists.transform(data, Mapper.mapSystemMeldungToJournalSystemMeldung());

		JournalResponse response = new JournalResponse();
		response.setSystemMeldungen(eintraege);
		return response;
	}

	@WebMethod
	public JournalResponse getBewegungsJournalSeit(@WebParam(name = "timeInMillis") long timeInMillis) {
		List<BewegungsMeldung> data = getQueryHelper().getBewegungsMeldungenSeit(timeInMillis);
		List<JournalBewegungsMeldung> eintraege = Lists.transform(data, Mapper.mapBewegungsMeldungToJournalBewegungsMeldung());

		JournalResponse response = new JournalResponse();
		response.setBewegungsMeldungen(eintraege);
		return response;
	}

	@WebMethod
	public JournalResponse getGefechtsJournalSeit(@WebParam(name = "timeInMillis") long timeInMillis) {
		List<GefechtsMeldung> data = getQueryHelper().getGefechtsMeldungenSeit(timeInMillis);
		List<JournalGefechtsMeldung> eintraege = Lists.transform(data, Mapper.mapGefechtsMeldungToJournalGefechtsMeldung());

		JournalResponse response = new JournalResponse();
		response.setGefechtsMeldungen(eintraege);
		return response;
	}

	@WebMethod
	public JournalResponse getJournalSeit(@WebParam(name = "timeInMillis") long timeInMillis) {
		JournalResponse response = new JournalResponse();
		response.setSystemMeldungen(this.getSystemJournalSeit(timeInMillis).getSystemMeldungen());
		response.setGefechtsMeldungen(this.getGefechtsJournalSeit(timeInMillis).getGefechtsMeldungen());
		response.setBewegungsMeldungen(this.getBewegungsJournalSeit(timeInMillis).getBewegungsMeldungen());
		return response;
	}

	private EntityManager getEntityManager() {
		return EntityManagerHelper.getEntityManager(context);
	}

	private QueryHelper getQueryHelper() {
		return new QueryHelper(getEntityManager());
	}

}
