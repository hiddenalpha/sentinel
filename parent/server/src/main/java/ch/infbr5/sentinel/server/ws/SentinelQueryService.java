package ch.infbr5.sentinel.server.ws;

import java.awt.Image;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import javax.jws.HandlerChain;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.xml.ws.soap.MTOM;

import ch.infbr5.sentinel.server.db.EntityManagerHelper;
import ch.infbr5.sentinel.server.db.ImageStore;
import ch.infbr5.sentinel.server.db.QueryHelper;
import ch.infbr5.sentinel.server.mapper.Mapper;
import ch.infbr5.sentinel.server.model.Ausweis;
import ch.infbr5.sentinel.server.model.Checkpoint;
import ch.infbr5.sentinel.server.model.ObjectFactory;
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

	private static Logger log = Logger.getLogger(SentinelQueryService.class.getName());

	@WebMethod
	public OperationResponse abmelden(@WebParam(name = "checkpointId") Long checkpointId,
			@WebParam(name = "barcode") String barcode) {

		PraesenzStatus status = PraesenzStatus.ABGEMELDET;
		Checkpoint checkpoint = QueryHelper.getCheckpoint(checkpointId);
		Person person = QueryHelper.findAusweisByBarcode(barcode).getPerson();
		log.info("Abmelden von " + person.getName() + " bei Checkpoint " + checkpoint.getName());

		// Alle offenen Präsenzen schliessen
		OperationResponse response = CheckpointHelper.setZonenPraesenz(person, checkpoint, status);

		// Auf dem Response Objekt die Zähler setzen
		CheckpointHelper.setCounters(checkpoint.getCheckInZonen().get(0).getId(), response);

		// Trigger Objekt setzen
		setPersonTriggerEintraege(person, response);

		// Bewegungsmeldung
		persistBewegungsMeldung(checkpoint, status, person);

		return response;
	}

	@WebMethod
	public OperationResponse anmelden(@WebParam(name = "checkpointId") Long checkpointId,
			@WebParam(name = "barcode") String barcode) {

		PraesenzStatus status = PraesenzStatus.ANGEMELDET;
		Checkpoint checkpoint = QueryHelper.getCheckpoint(checkpointId);
		Person person = QueryHelper.findAusweisByBarcode(barcode).getPerson();
		log.info("Anmelden von " + person.getName() + " bei Checkpoint " + checkpoint.getName());

		// Für Zone anmelden
		OperationResponse response = CheckpointHelper.setZonenPraesenz(person, checkpoint, status);

		// Auf dem Response Objekt die Zähler setzen
		CheckpointHelper.setCounters(checkpoint.getCheckInZonen().get(0).getId(), response);

		// Trigger Objekt setzen
		setPersonTriggerEintraege(person, response);

		// Bewegungsmeldung
		persistBewegungsMeldung(checkpoint, status, person);

		return response;
	}

	@WebMethod
	public OperationResponse beurlauben(@WebParam(name = "checkpointId") Long checkpointId,
			@WebParam(name = "barcode") String barcode) {

		PraesenzStatus status = PraesenzStatus.URLAUB;
		Checkpoint checkpoint = QueryHelper.getCheckpoint(checkpointId);
		Person person = QueryHelper.findAusweisByBarcode(barcode).getPerson();
		log.info("Urlaub von " + person.getName() + " bei Checkpoint " + checkpoint.getName());

		// CHECKOUT: IN (Urlaub) -> OUT (Innerhalb)
		OperationResponse response = CheckpointHelper.passCheckpoint(checkpointId, barcode,
				checkpoint.getCheckInZonen(), PraesenzStatus.AUSSERHALB, checkpoint.getCheckInZonen(),
				status);

		// Auf dem Response Objekt die Zähler setzen
		CheckpointHelper.setCounters(checkpoint.getCheckInZonen().get(0).getId(), response);

		// Trigger Objekt setzen
		setPersonTriggerEintraege(person, response);

		// Bewegungsmeldung
		persistBewegungsMeldung(checkpoint, status, person);

		return response;
	}

	@WebMethod
	public OperationResponse checkin(@WebParam(name = "checkpointId") Long checkpointId,
			@WebParam(name = "barcode") String barcode) {

		PraesenzStatus status = PraesenzStatus.INNERHALB;
		Checkpoint checkpoint = QueryHelper.getCheckpoint(checkpointId);
		Person person = QueryHelper.findAusweisByBarcode(barcode).getPerson();
		log.info("Checkin von " + person.getName() + " bei Checkpoint " + checkpoint.getName());

		// CHECKIN: OUT(Ausserhalb) -> IN(Innerhalb)
		OperationResponse response = CheckpointHelper.passCheckpoint(checkpointId, barcode,
				checkpoint.getCheckInZonen(), PraesenzStatus.AUSSERHALB, checkpoint.getCheckInZonen(),
				status);

		// Auf dem Response Objekt die Zähler setzen
		CheckpointHelper.setCounters(checkpoint.getCheckInZonen().get(0).getId(), response);

		// Trigger Objekt setzen
		setPersonTriggerEintraege(person, response);

		// Bewegungsmeldung
		persistBewegungsMeldung(checkpoint, status, person);

		return response;
	}

	@WebMethod
	public OperationResponse checkout(@WebParam(name = "checkpointId") Long checkpointId,
			@WebParam(name = "barcode") String barcode) {

		PraesenzStatus status = PraesenzStatus.AUSSERHALB;
		Checkpoint checkpoint = QueryHelper.getCheckpoint(checkpointId);
		Person person = QueryHelper.findAusweisByBarcode(barcode).getPerson();
		log.info("Checkout von " + person.getName() + " bei Checkpoint " + checkpoint.getName());

		// CHECKOUT: IN(Ausserhalb) -> OUT(Innerhalb)
		OperationResponse response = CheckpointHelper.passCheckpoint(checkpointId, barcode,
				checkpoint.getCheckInZonen(), PraesenzStatus.INNERHALB, checkpoint.getCheckInZonen(),
				status);

		// Auf dem Response Objekt die Zähler setzen
		CheckpointHelper.setCounters(checkpoint.getCheckInZonen().get(0).getId(), response);

		// Trigger Objekt setzen
		setPersonTriggerEintraege(person, response);

		// Bewegungsmeldung
		persistBewegungsMeldung(checkpoint, status, person);

		return response;
	}

	@WebMethod
	public OperationResponse getPersonenMitAusweis() {
		List<Ausweis> ausweise = QueryHelper.findAusweise();

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
		List<Person> personen = QueryHelper.getPersonen();
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

		Person p = QueryHelper.getPerson(personId);
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
		Person p = QueryHelper.getPerson(personId);
		if ((p != null) && (p.getValidAusweis() != null)) {
			p.getValidAusweis().invalidate();
		}
		Ausweis a = ObjectFactory.createAusweis(p);
		EntityManagerHelper.getEntityManager().persist(a);
		p.setValidAusweis(a);

		OperationResponse response = new OperationResponse();
		response.setStatus(OperationResponseStatus.SUCESS);

		return response;
	}

	@WebMethod
	public OperationResponse getPersonenStatusListe(@WebParam(name = "zoneId") long zoneId,
			@WebParam(name = "status") PraesenzStatus status) {
		List<ZonenPraesenz> zpList = QueryHelper.findZonenPraesenz(zoneId, status);
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

		CheckpointHelper.setCounters(zoneId, response);
		return response;
	}

	@WebMethod
	public Image getPersonImage(@WebParam(name = "imageId") String imageId) {
		return ImageStore.getImage(imageId);
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
		List<GefechtsMeldung> gefechtsMeldungen = QueryHelper.getPersonTriggerEintraege(person);
		eintraege = Lists.transform(gefechtsMeldungen, Mapper.mapGefechtsMeldungToJournalGefechtsMeldung());
		log.info(eintraege.size() + " Trigger Einträge für " + person.getName() + " gefunden.");
		response.setPersonTriggerEintraege(eintraege);
	}

	private static void persistBewegungsMeldung(Checkpoint checkpoint, PraesenzStatus status, Person person) {
		BewegungsMeldung bewegungsMeldung = new BewegungsMeldung();
		bewegungsMeldung.setCheckpoint(checkpoint);
		bewegungsMeldung.setMillis(new Date().getTime());
		bewegungsMeldung.setPraesenzStatus(status);
		bewegungsMeldung.setPerson(person);
		EntityManagerHelper.getEntityManager().persist(bewegungsMeldung);
	}
}
