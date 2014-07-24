package ch.infbr5.sentinel.server.ws;

import java.util.Calendar;
import java.util.List;
import java.util.ListIterator;

import ch.infbr5.sentinel.server.db.EntityManagerHelper;
import ch.infbr5.sentinel.server.db.ImageStore;
import ch.infbr5.sentinel.server.db.QueryHelper;
import ch.infbr5.sentinel.server.model.Ausweis;
import ch.infbr5.sentinel.server.model.Checkpoint;
import ch.infbr5.sentinel.server.model.ObjectFactory;
import ch.infbr5.sentinel.server.model.Person;
import ch.infbr5.sentinel.server.model.PraesenzStatus;
import ch.infbr5.sentinel.server.model.Zone;
import ch.infbr5.sentinel.server.model.ZonenPraesenz;

public class CheckpointHelper {

	/**
	 * Sucht alle offnen (bis Datum in der Zukunft) ZonenPraesenz und schliesst
	 * diese (setzt die aktuelle Zeit und Datum).
	 *
	 * @param person
	 * @param zone
	 */
	private static void closeOpenedZonenPraesenz(Person person, Zone zone) {
		List<ZonenPraesenz> lzp = QueryHelper.findZonenPraesenz(zone, person);
		for (ZonenPraesenz zp : lzp) {
			zp.setBis(Calendar.getInstance());
			EntityManagerHelper.getEntityManager().persist(zp);
		}
	}

	public static OperationResponse passCheckpoint(long checkpointId, String barcode, List<Zone> fromZonen,
			PraesenzStatus fromZonenStatus, List<Zone> toZonen, PraesenzStatus toZonenStatus) {

		OperationResponse response = new OperationResponse();

		// Ausweis suchen
		Ausweis ausweis = QueryHelper.findAusweisByBarcode(barcode);

		if (ausweis == null) {
			response.setMessage("Ausweis nicht vorhanden. Checkin gescheitert. Barcode-Nummer: " + barcode);
			response.setStatus(OperationResponseStatus.FAIL);

			return response;
		} else if (ausweis.isInvalid()) {
			response.setMessage("Ausweis gesperrt. Checkin gescheitert. Barcode-Nummer: " + barcode);
			response.setStatus(OperationResponseStatus.FAIL);

			return response;
		}

		response.setStatus(OperationResponseStatus.SUCESS);

		Person person = ausweis.getPerson();
		if (ImageStore.hasImage(person.getAhvNr())) {
			response.setImageId(person.getAhvNr());
		}

		// Zutritt mit diesem Ausweis zur neuen Zone prüfen
		for (ListIterator<Zone> iterator = toZonen.listIterator(); iterator.hasNext();) {
			Zone zone = iterator.next();
			if (!zone.isAccessAuthorized(ausweis)) {
				response.setStatus(OperationResponseStatus.FAIL);
				response.setMessage("Person hat keinen Zutritt in Zone " + zone.getName() + " Checkin gescheitert.");
				return response;
			}
		}

		// offene Präsenz beenden und neue eröffnen
		CheckpointHelper.setZonenPraesenz(person, fromZonen, fromZonenStatus);
		CheckpointHelper.setZonenPraesenz(person, toZonen, toZonenStatus);

		response.setMessage("Checkin erfolgreich. " + toZonenStatus.name() + " " + person.getName());

		return response;
	}

	public static void setCounters(long zoneId, OperationResponse response) {
		response.setCounterIn(QueryHelper.getCountOfZonenPraesenz(zoneId, PraesenzStatus.INNERHALB));
		response.setCounterOut(QueryHelper.getCountOfZonenPraesenz(zoneId, PraesenzStatus.AUSSERHALB));
		response.setCounterUrlaub(QueryHelper.getCountOfZonenPraesenz(zoneId, PraesenzStatus.URLAUB));
		response.setCounterAngemeldet(QueryHelper.getCountOfZonenPraesenz(zoneId, PraesenzStatus.ANGEMELDET));
	}

	public static OperationResponse setZonenPraesenz(Person person, Checkpoint checkpoint, PraesenzStatus status) {

		// Präsenz setzen
		CheckpointHelper.setZonenPraesenz(person, checkpoint.getCheckInZonen(), status);



		// Response erstellen
		OperationResponse response = new OperationResponse();
		response.setMessage(status.name() + " " + person.getName()); // TODO
		response.setStatus(OperationResponseStatus.SUCESS);
		if (ImageStore.hasImage(person.getAhvNr())) {
			response.setImageId(person.getAhvNr());
		}

		return response;
	}

	/**
	 * Setzt den Status einer Person für eine Liste von Zonen.
	 *
	 */
	private static void setZonenPraesenz(Person person, List<Zone> zonen, PraesenzStatus status) {
		ListIterator<Zone> iterator = zonen.listIterator();
		while (iterator.hasNext()) {
			Zone zone = iterator.next();

			// Nach bestehenden offenen Einträgen suchen
			CheckpointHelper.closeOpenedZonenPraesenz(person, zone);

			// neue erstellen
			if (status != PraesenzStatus.ABGEMELDET) {
				ZonenPraesenz praesenz = ObjectFactory.createPraesenzInZone(zone, person, status);
				EntityManagerHelper.getEntityManager().persist(praesenz);
			}
		}
	}

}
