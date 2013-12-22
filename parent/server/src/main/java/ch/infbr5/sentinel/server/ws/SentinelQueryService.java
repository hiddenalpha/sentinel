package ch.infbr5.sentinel.server.ws;

import java.awt.Image;
import java.util.List;

import javax.jws.HandlerChain;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.xml.ws.soap.MTOM;

import ch.infbr5.sentinel.server.db.EntityManagerHelper;
import ch.infbr5.sentinel.server.db.ImageStore;
import ch.infbr5.sentinel.server.db.QueryHelper;
import ch.infbr5.sentinel.server.model.Ausweis;
import ch.infbr5.sentinel.server.model.Checkpoint;
import ch.infbr5.sentinel.server.model.ObjectFactory;
import ch.infbr5.sentinel.server.model.Person;
import ch.infbr5.sentinel.server.model.PraesenzStatus;
import ch.infbr5.sentinel.server.model.ZonenPraesenz;

@MTOM
@WebService(name = "SentinelQueryService", targetNamespace = "http://ws.sentinel.infbr5.ch/")
@HandlerChain(file = "/META-INF/ws-handler-chain.xml")
@SOAPBinding(style = SOAPBinding.Style.RPC)
public class SentinelQueryService {

	@WebMethod
	public OperationResponse abmelden(@WebParam(name = "checkpointId") Long checkpointId,
			@WebParam(name = "barcode") String barcode) {

		// Checkpoint ermitteln
		Checkpoint checkpoint = QueryHelper.getCheckpoint(checkpointId);
		// alle offenen Praesenzen schliessen
		OperationResponse response = CheckpointHelper.setZonenPraesenz(barcode, checkpoint.getCheckInZonen(),
				PraesenzStatus.ABGEMELDET);

		CheckpointHelper.setCounters(checkpoint.getCheckInZonen().get(0).getId(), response);
		
		this.setPersonTriggerEintragIfAvailable(barcode, response);

		return response;
	}

	@WebMethod
	public OperationResponse anmelden(@WebParam(name = "checkpointId") Long checkpointId,
			@WebParam(name = "barcode") String barcode) {

		// Checkpoint ermitteln
		Checkpoint checkpoint = QueryHelper.getCheckpoint(checkpointId);
		// Für Zone anmelden
		OperationResponse response = CheckpointHelper.setZonenPraesenz(barcode, checkpoint.getCheckInZonen(),
				PraesenzStatus.ANGEMELDET);

		CheckpointHelper.setCounters(checkpoint.getCheckInZonen().get(0).getId(), response);

		this.setPersonTriggerEintragIfAvailable(barcode, response);

		return response;
	}

	@WebMethod
	public OperationResponse beurlauben(@WebParam(name = "checkpointId") Long checkpointId,
			@WebParam(name = "barcode") String barcode) {

		// Checkpoint ermitteln
		Checkpoint checkpoint = QueryHelper.getCheckpoint(checkpointId);
		// CHECKOUT: IN(Urlaub) -> OUT(Innerhalb)
		OperationResponse response = CheckpointHelper.passCheckpoint(barcode, checkpoint.getCheckInZonen(),
				PraesenzStatus.AUSSERHALB, checkpoint.getCheckInZonen(), PraesenzStatus.URLAUB);

		CheckpointHelper.setCounters(checkpoint.getCheckInZonen().get(0).getId(), response);

		this.setPersonTriggerEintragIfAvailable(barcode, response);

		return response;
	}

	@WebMethod
	public OperationResponse checkin(@WebParam(name = "checkpointId") Long checkpointId,
			@WebParam(name = "barcode") String barcode) {

		// Checkpoint ermitteln
		Checkpoint checkpoint = QueryHelper.getCheckpoint(checkpointId);
		// CHECKIN: OUT(Ausserhalb) -> IN(Innerhalb)
		OperationResponse response = CheckpointHelper.passCheckpoint(barcode, checkpoint.getCheckInZonen(),
				PraesenzStatus.AUSSERHALB, checkpoint.getCheckInZonen(), PraesenzStatus.INNERHALB);

		CheckpointHelper.setCounters(checkpoint.getCheckInZonen().get(0).getId(), response);

		this.setPersonTriggerEintragIfAvailable(barcode, response);

		return response;
	}

	@WebMethod
	public OperationResponse checkout(@WebParam(name = "checkpointId") Long checkpointId,
			@WebParam(name = "barcode") String barcode) {

		// Checkpoint ermitteln
		Checkpoint checkpoint = QueryHelper.getCheckpoint(checkpointId);
		// CHECKOUT: IN(Ausserhalb) -> OUT(Innerhalb)
		OperationResponse response = CheckpointHelper.passCheckpoint(barcode, checkpoint.getCheckInZonen(),
				PraesenzStatus.INNERHALB, checkpoint.getCheckInZonen(), PraesenzStatus.AUSSERHALB);

		CheckpointHelper.setCounters(checkpoint.getCheckInZonen().get(0).getId(), response);

		this.setPersonTriggerEintragIfAvailable(barcode, response);

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

	private void setPersonTriggerEintragIfAvailable(String barcode, OperationResponse response) {
		// TODO
		// OperatorEintrag operatorEintrag = QueryHelper
		// .getPersonTriggerEintrag(barcode);

		// if (operatorEintrag != null) {
		// response.setPersonTriggerEintrag(operatorEintrag);
		// }
	}
}
