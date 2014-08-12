package ch.infbr5.sentinel.server.ws;

import java.awt.Image;
import java.util.Date;
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
import ch.infbr5.sentinel.server.db.ImageStore;
import ch.infbr5.sentinel.server.db.QueryHelper;
import ch.infbr5.sentinel.server.mapper.Mapper;
import ch.infbr5.sentinel.server.model.Ausweis;
import ch.infbr5.sentinel.server.model.Checkpoint;
import ch.infbr5.sentinel.server.model.ObjectFactory;
import ch.infbr5.sentinel.server.model.OperatorAktion;
import ch.infbr5.sentinel.server.model.Person;
import ch.infbr5.sentinel.server.model.PraesenzStatus;
import ch.infbr5.sentinel.server.model.ZonenPraesenz;
import ch.infbr5.sentinel.server.model.journal.BewegungsMeldung;
import ch.infbr5.sentinel.server.model.journal.GefechtsMeldung;
import ch.infbr5.sentinel.server.ws.journal.JournalGefechtsMeldung;

import com.google.common.collect.Lists;

@MTOM
@WebService(name = "SentinelQueryService", targetNamespace = "http://ws.sentinel.infbr5.ch/")
@HandlerChain(file = "/META-INF/ws-handler-chain.xml")
@SOAPBinding(style = SOAPBinding.Style.RPC)
public class SentinelQueryService {

	@Resource
	private WebServiceContext context;

	private static Logger log = Logger.getLogger(SentinelQueryService.class);

	@WebMethod
	public OperationResponse abmelden(@WebParam(name = "checkpointId") Long checkpointId,
			@WebParam(name = "barcode") String barcode) {

		QueryHelper qh = getQueryHelper();
		Checkpoint checkpoint = qh.getCheckpoint(checkpointId);
		Ausweis ausweis = qh.findAusweisByBarcode(barcode);
		OperationResponse response;
		if (ausweis != null) {
			Person person = ausweis.getPerson();
			log.info("Abmelden von " + person.getName() + " bei Checkpoint " + checkpoint.getName());

			// Alle offenen Präsenzen schliessen
			response = getCheckpointHelper().setZonenPraesenz(person, checkpoint, PraesenzStatus.ABGEMELDET);

			// Auf dem Response Objekt die Zähler setzen
			getCheckpointHelper().setCounters(checkpoint.getCheckInZonen().get(0).getId(), response);

			// Trigger Objekt setzen
			setPersonTriggerEintraege(person, response);

			// Bewegungsmeldung
			persistBewegungsMeldung(checkpoint, OperatorAktion.ABMELDEN, person);
		} else {
			response = new OperationResponse();
			response.setStatus(OperationResponseStatus.FAIL);
			response.setMessage("Kein Ausweis.");
		}

		return response;
	}

	@WebMethod
	public OperationResponse anmelden(@WebParam(name = "checkpointId") Long checkpointId,
			@WebParam(name = "barcode") String barcode) {

		QueryHelper qh = getQueryHelper();
		Checkpoint checkpoint = qh.getCheckpoint(checkpointId);

		Ausweis ausweis = qh.findAusweisByBarcode(barcode);
		OperationResponse response;
		if (ausweis != null) {
			Person person = ausweis.getPerson();
			log.info("Anmelden von " + person.getName() + " bei Checkpoint " + checkpoint.getName());

			// Für Zone anmelden
			response = getCheckpointHelper().setZonenPraesenz(person, checkpoint, PraesenzStatus.ANGEMELDET);

			// Auf dem Response Objekt die Zähler setzen
			getCheckpointHelper().setCounters(checkpoint.getCheckInZonen().get(0).getId(), response);

			// Trigger Objekt setzen
			setPersonTriggerEintraege(person, response);

			// Bewegungsmeldung
			persistBewegungsMeldung(checkpoint, OperatorAktion.ANMELDEN, person);
		} else {
			response = new OperationResponse();
			response.setStatus(OperationResponseStatus.FAIL);
			response.setMessage("Kein Ausweis.");
		}

		return response;
	}

	@WebMethod
	public OperationResponse beurlauben(@WebParam(name = "checkpointId") Long checkpointId,
			@WebParam(name = "barcode") String barcode) {

		QueryHelper qh = getQueryHelper();
		Checkpoint checkpoint = qh.getCheckpoint(checkpointId);

		// CHECKOUT: IN (Urlaub) -> OUT (Innerhalb)
		OperationResponse response = getCheckpointHelper().passCheckpoint(checkpointId, barcode,
				checkpoint.getCheckInZonen(), PraesenzStatus.AUSSERHALB, checkpoint.getCheckInZonen(),
				PraesenzStatus.URLAUB);

		// Auf dem Response Objekt die Zähler setzen
		getCheckpointHelper().setCounters(checkpoint.getCheckInZonen().get(0).getId(), response);

		if (response.getStatus().equals(OperationResponseStatus.SUCESS)) {
			Person person = qh.findAusweisByBarcode(barcode).getPerson();
			log.info("Urlaub von " + person.getName() + " bei Checkpoint " + checkpoint.getName());

			// Trigger Objekt setzen
			setPersonTriggerEintraege(person, response);

			// Bewegungsmeldung
			persistBewegungsMeldung(checkpoint, OperatorAktion.URLAUB, person);
		}

		return response;
	}

	@WebMethod
	public OperationResponse checkin(@WebParam(name = "checkpointId") Long checkpointId,
			@WebParam(name = "barcode") String barcode) {

		QueryHelper qh = getQueryHelper();
		Checkpoint checkpoint = qh.getCheckpoint(checkpointId);

		// CHECKIN: OUT(Ausserhalb) -> IN(Innerhalb)
		OperationResponse response = getCheckpointHelper().passCheckpoint(checkpointId, barcode,
				checkpoint.getCheckInZonen(), PraesenzStatus.AUSSERHALB, checkpoint.getCheckInZonen(),
				PraesenzStatus.INNERHALB);

		// Auf dem Response Objekt die Zähler setzen
		getCheckpointHelper().setCounters(checkpoint.getCheckInZonen().get(0).getId(), response);

		if (response.getStatus().equals(OperationResponseStatus.SUCESS)) {
			Person person = qh.findAusweisByBarcode(barcode).getPerson();
			log.info("Checkin von " + person.getName() + " bei Checkpoint " + checkpoint.getName());

			// Trigger Objekt setzen
			setPersonTriggerEintraege(person, response);

			// Bewegungsmeldung
			persistBewegungsMeldung(checkpoint, OperatorAktion.CHECKIN, person);
		}

		return response;
	}

	@WebMethod
	public OperationResponse checkout(@WebParam(name = "checkpointId") Long checkpointId,
			@WebParam(name = "barcode") String barcode) {

		QueryHelper qh = getQueryHelper();
		Checkpoint checkpoint = qh.getCheckpoint(checkpointId);

		// CHECKOUT: IN(Ausserhalb) -> OUT(Innerhalb)
		OperationResponse response = getCheckpointHelper().passCheckpoint(checkpointId, barcode,
				checkpoint.getCheckInZonen(), PraesenzStatus.INNERHALB, checkpoint.getCheckInZonen(),
				PraesenzStatus.AUSSERHALB);

		// Auf dem Response Objekt die Zähler setzen
		getCheckpointHelper().setCounters(checkpoint.getCheckInZonen().get(0).getId(), response);

		if (response.getStatus().equals(OperationResponseStatus.SUCESS)) {
			Person person = qh.findAusweisByBarcode(barcode).getPerson();
			log.info("Checkout von " + person.getName() + " bei Checkpoint " + checkpoint.getName());

			// Trigger Objekt setzen
			setPersonTriggerEintraege(person, response);

			// Bewegungsmeldung
			persistBewegungsMeldung(checkpoint, OperatorAktion.CHECKOUT, person);
		}

		return response;
	}

	@WebMethod
	public OperationResponse getPersonenMitAusweis() {
		QueryHelper qh = getQueryHelper();
		List<Ausweis> ausweise = qh.findAusweise();

		PersonDetails[] personenDetails = new PersonDetails[ausweise.size()];
		for (int j = 0; j < ausweise.size(); j++) {

			Ausweis ausweis = ausweise.get(j);
			Person person = ausweis.getPerson();

			PersonDetails personDetails = new PersonDetails();

			personDetails.setVorname(person.getVorname());
			personDetails.setName(person.getName());
			personDetails.setFunktion(person.getFunktion());
			if (person.getGrad() != null) {
				personDetails.setGrad(person.getGrad().toString());
			}
			if (person.getEinheit() != null) {
				personDetails.setEinheitId(person.getEinheit().getId());
			}
			personDetails.setBarcode(ausweis.getBarcode());

			personenDetails[j] = personDetails;
		}

		OperationResponse response = new OperationResponse();
		response.setPersonDetails(personenDetails);

		return response;
	}

	@WebMethod
	public OperationResponse getAllePersonen() {
		QueryHelper qh = getQueryHelper();
		List<Person> personen = qh.getPersonen();
		PersonDetails[] details = new PersonDetails[personen.size()];
		int i = 0;
		for (Person p : personen) {
			details[i++] = Mapper.mapPersonToPersonDetails().apply(p);
		}
		OperationResponse response = new OperationResponse();
		response.setPersonDetails(details);
		return response;
	}

	@WebMethod
	public OperationResponse invalidateAusweise(@WebParam(name = "personId") Long personId) {
		QueryHelper qh = getQueryHelper();
		Person p = qh.getPerson(personId);
		if ((p != null) && (p.getValidAusweis() != null)) {
			p.getValidAusweis().invalidate();
			p.setValidAusweis(null);
		}

		OperationResponse response = new OperationResponse();
		response.setStatus(OperationResponseStatus.SUCESS);
		return response;
	}

	@WebMethod
	public OperationResponse neuerAusweis(@WebParam(name = "personId") Long personId) {
		QueryHelper qh = getQueryHelper();
		Person p = qh.getPerson(personId);
		if ((p != null) && (p.getValidAusweis() != null)) {
			p.getValidAusweis().invalidate();
		}
		Ausweis a = ObjectFactory.createAusweis(p, qh.createUniqueBarcode());
		getEntityManager().persist(a);
		p.setValidAusweis(a);

		OperationResponse response = new OperationResponse();
		response.setStatus(OperationResponseStatus.SUCESS);

		return response;
	}

	@WebMethod
	public OperationResponse getPersonenStatusListe(@WebParam(name = "zoneId") long zoneId,
			@WebParam(name = "status") PraesenzStatus status) {
		List<ZonenPraesenz> zpList = getQueryHelper().findZonenPraesenz(zoneId, status);
		OperationResponse response = new OperationResponse();

		PersonDetails[] pdArr = new PersonDetails[zpList.size()];
		for (int j = 0; j < zpList.size(); j++) {

			ZonenPraesenz zp = zpList.get(j);
			Person person = zp.getPerson();

			PersonDetails pd = new PersonDetails();
			pd.setVorname(person.getVorname());
			pd.setName(person.getName());
			pd.setFunktion(person.getFunktion());
			if (person.getValidAusweis() != null) {
				pd.setBarcode(person.getValidAusweis().getBarcode());
			}

			pd.setLastStatusChange(zp.getVon());

			pdArr[j] = pd;

		}
		response.setPersonDetails(pdArr);

		getCheckpointHelper().setCounters(zoneId, response);
		return response;
	}

	@WebMethod
	public Image getPersonImage(@WebParam(name = "imageId") String imageId) {
		return ImageStore.getImage(imageId);
	}

	@WebMethod
	public String ping() {
		return "pong";
	}

	private EntityManager getEntityManager() {
		return EntityManagerHelper.getEntityManager(context);
	}

	private QueryHelper getQueryHelper() {
		return new QueryHelper(getEntityManager());
	}

	private CheckpointHelper getCheckpointHelper() {
		return new CheckpointHelper(getEntityManager());
	}

	/**
	 * Setzt auf dem Response eine Gefechtsmeldung, falls für die gegebene
	 * Person eine unerledigte vorhanden ist.
	 *
	 * @param person
	 *            Person.
	 * @param response
	 *            Response-Objekt an den Client.
	 */
	private void setPersonTriggerEintraege(Person person, OperationResponse response) {
		List<JournalGefechtsMeldung> eintraege = Lists.newArrayList();
		List<GefechtsMeldung> gefechtsMeldungen = getQueryHelper().getPersonTriggerEintraege(person);
		eintraege = Lists.transform(gefechtsMeldungen, Mapper.mapGefechtsMeldungToJournalGefechtsMeldung());
		log.info(eintraege.size() + " Trigger Einträge für " + person.getName() + " gefunden.");
		response.setPersonTriggerEintraege(eintraege);
	}

	private void persistBewegungsMeldung(Checkpoint checkpoint, OperatorAktion aktion, Person person) {
		BewegungsMeldung bewegungsMeldung = new BewegungsMeldung();
		bewegungsMeldung.setCheckpoint(checkpoint);
		bewegungsMeldung.setMillis(new Date().getTime());
		bewegungsMeldung.setOperatorAktion(aktion);
		bewegungsMeldung.setPerson(person);
		getEntityManager().persist(bewegungsMeldung);
	}
}
