package ch.infbr5.sentinel.server.ws;

import java.util.Calendar;
import java.util.List;
import java.util.ListIterator;

import javax.persistence.EntityManager;

import ch.infbr5.sentinel.server.db.PersonImageStore;
import ch.infbr5.sentinel.server.db.QueryHelper;
import ch.infbr5.sentinel.server.model.Ausweis;
import ch.infbr5.sentinel.server.model.Checkpoint;
import ch.infbr5.sentinel.server.model.ObjectFactory;
import ch.infbr5.sentinel.server.model.Person;
import ch.infbr5.sentinel.server.model.PraesenzStatus;
import ch.infbr5.sentinel.server.model.Zone;
import ch.infbr5.sentinel.server.model.ZonenPraesenz;

public class CheckpointHelper {

   private final EntityManager em;

   public CheckpointHelper(final EntityManager em) {
      this.em = em;
   }

   private QueryHelper getQueryHelper() {
      return new QueryHelper(em);
   }

   /**
    * Sucht alle offnen (bis Datum in der Zukunft) ZonenPraesenz und schliesst
    * diese (setzt die aktuelle Zeit und Datum).
    *
    * @param person
    * @param zone
    */
   private void closeOpenedZonenPraesenz(final Person person, final Zone zone) {
      final List<ZonenPraesenz> lzp = getQueryHelper().findZonenPraesenz(zone, person);
      for (final ZonenPraesenz zp : lzp) {
         zp.setBis(Calendar.getInstance());
         em.persist(zp);
      }
   }

   public OperationResponse passCheckpoint(final long checkpointId, final String barcode, final List<Zone> fromZonen,
         final PraesenzStatus fromZonenStatus, final List<Zone> toZonen, final PraesenzStatus toZonenStatus) {

      final OperationResponse response = new OperationResponse();

      // Ausweis suchen
      final Ausweis ausweis = getQueryHelper().findAusweisByBarcode(barcode);

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

      final Person person = ausweis.getPerson();
      if (PersonImageStore.hasImage(person.getAhvNr())) {
         response.setImageId(person.getAhvNr());
      }

      // Zutritt mit diesem Ausweis zur neuen Zone pruefen
      for (final ListIterator<Zone> iterator = toZonen.listIterator(); iterator.hasNext();) {
         final Zone zone = iterator.next();
         if (!zone.isAccessAuthorized(ausweis)) {
            response.setStatus(OperationResponseStatus.FAIL);
            response.setMessage("Person hat keinen Zutritt in Zone " + zone.getName() + " Checkin gescheitert.");
            return response;
         }
      }

      // offene Praesenz beenden und neue eroeffnen
      setZonenPraesenz(person, fromZonen, fromZonenStatus);
      setZonenPraesenz(person, toZonen, toZonenStatus);

      response.setMessage("Checkin erfolgreich. " + toZonenStatus.name() + " " + person.getName());

      return response;
   }

   public void setCounters(final long zoneId, final OperationResponse response) {
      response.setCounterIn(getQueryHelper().getCountOfZonenPraesenz(zoneId, PraesenzStatus.INNERHALB));
      response.setCounterOut(getQueryHelper().getCountOfZonenPraesenz(zoneId, PraesenzStatus.AUSSERHALB));
      response.setCounterUrlaub(getQueryHelper().getCountOfZonenPraesenz(zoneId, PraesenzStatus.URLAUB));
      response.setCounterAngemeldet(getQueryHelper().getCountOfZonenPraesenz(zoneId, PraesenzStatus.ANGEMELDET));
   }

   public OperationResponse setZonenPraesenz(final Person person, final Checkpoint checkpoint,
         final PraesenzStatus status) {

      // Praesenz setzen
      setZonenPraesenz(person, checkpoint.getCheckInZonen(), status);

      // Response erstellen
      final OperationResponse response = new OperationResponse();
      response.setMessage(status.name() + " " + person.getName()); // TODO
      response.setStatus(OperationResponseStatus.SUCESS);
      if (PersonImageStore.hasImage(person.getAhvNr())) {
         response.setImageId(person.getAhvNr());
      }

      return response;
   }

   /**
    * Setzt den Status einer Person f�r eine Liste von Zonen.
    *
    */
   private void setZonenPraesenz(final Person person, final List<Zone> zonen, final PraesenzStatus status) {
      final ListIterator<Zone> iterator = zonen.listIterator();
      while (iterator.hasNext()) {
         final Zone zone = iterator.next();

         // Nach bestehenden offenen Eintraegen suchen
         closeOpenedZonenPraesenz(person, zone);

         // neue erstellen
         if (status != PraesenzStatus.ABGEMELDET) {
            final ZonenPraesenz praesenz = ObjectFactory.createPraesenzInZone(zone, person, status);
            em.persist(praesenz);
         }
      }
   }

}
