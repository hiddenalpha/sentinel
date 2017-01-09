package ch.infbr5.sentinel.server.mapper;

import javax.persistence.EntityManager;

import ch.infbr5.sentinel.server.db.PersonImageStore;
import ch.infbr5.sentinel.server.db.QueryHelper;
import ch.infbr5.sentinel.server.model.Checkpoint;
import ch.infbr5.sentinel.server.model.ConfigurationValue;
import ch.infbr5.sentinel.server.model.Einheit;
import ch.infbr5.sentinel.server.model.Grad;
import ch.infbr5.sentinel.server.model.Person;
import ch.infbr5.sentinel.server.model.journal.BewegungsMeldung;
import ch.infbr5.sentinel.server.model.journal.GefechtsMeldung;
import ch.infbr5.sentinel.server.model.journal.SystemMeldung;
import ch.infbr5.sentinel.server.ws.CheckpointDetails;
import ch.infbr5.sentinel.server.ws.ConfigurationDetails;
import ch.infbr5.sentinel.server.ws.EinheitDetails;
import ch.infbr5.sentinel.server.ws.PersonDetails;
import ch.infbr5.sentinel.server.ws.journal.JournalBewegungsMeldung;
import ch.infbr5.sentinel.server.ws.journal.JournalGefechtsMeldung;
import ch.infbr5.sentinel.server.ws.journal.JournalSystemMeldung;

import com.google.common.base.Function;

public class Mapper {

   public static Function<JournalGefechtsMeldung, GefechtsMeldung> mapJournalGefechtsMeldungToGefechtsMeldung(
         final EntityManager entityManager) {
      return new Function<JournalGefechtsMeldung, GefechtsMeldung>() {

         @Override
         public GefechtsMeldung apply(final JournalGefechtsMeldung source) {
            final GefechtsMeldung target = new GefechtsMeldung();
            target.setId(source.getId());
            target.setCheckpoint(mapCheckpointDetailsToCheckpoint().apply(source.getCheckpoint()));
            target.setMillis(source.getMillis());
            target.setIstErledigt(source.isIstErledigt());
            target.setMassnahme(source.getMassnahme());
            target.setWerWasWoWie(source.getWerWasWoWie());
            target.setZeitpunktErledigt(source.getZeitpunktErledigt());
            target.setZeitpunktMeldungsEingang(source.getZeitpunktMeldungsEingang());
            if (source.getWeiterleitenAnPerson() != null) {
               target.setWeiterleitenAnPerson(new QueryHelper(entityManager).getPerson(source.getWeiterleitenAnPerson()
                     .getId()));
            }
            return target;
         }

      };
   }

   public static Function<JournalBewegungsMeldung, BewegungsMeldung> mapJournalBewegungsMeldungToBewegungsMeldung() {

      return new Function<JournalBewegungsMeldung, BewegungsMeldung>() {
         @Override
         public BewegungsMeldung apply(final JournalBewegungsMeldung source) {
            final BewegungsMeldung target = new BewegungsMeldung();
            target.setId(source.getId());
            target.setCheckpoint(mapCheckpointDetailsToCheckpoint().apply(source.getCheckpoint()));
            target.setMillis(source.getMillis());
            return target;
         }
      };

   }

   public static Function<SystemMeldung, JournalSystemMeldung> mapSystemMeldungToJournalSystemMeldung() {
      return new Function<SystemMeldung, JournalSystemMeldung>() {

         @Override
         public JournalSystemMeldung apply(final SystemMeldung source) {
            final JournalSystemMeldung target = new JournalSystemMeldung();
            target.setId(source.getId());
            target.setCheckpoint(mapCheckpointToCheckpointDetails().apply(source.getCheckpoint()));
            target.setLevel(source.getLevel());
            target.setMessage(source.getMessage());
            target.setMillis(source.getMillis());
            target.setLoggerClass(source.getLoggerClass());
            target.setCallerClass(source.getCallerClass());
            target.setCallerFilename(source.getCallerFilename());
            target.setCallerLineNumber(source.getCallerLineNumber());
            target.setCallerMethod(source.getCallerMethod());
            return target;
         }

      };
   }

   public static Function<JournalSystemMeldung, SystemMeldung> mapJournalSystemMeldungToSystemMeldung() {
      return new Function<JournalSystemMeldung, SystemMeldung>() {

         @Override
         public SystemMeldung apply(final JournalSystemMeldung source) {
            final SystemMeldung target = new SystemMeldung();
            target.setId(source.getId());
            target.setCheckpoint(mapCheckpointDetailsToCheckpoint().apply(source.getCheckpoint()));
            target.setLevel(source.getLevel());
            target.setMessage(source.getMessage());
            target.setMillis(source.getMillis());
            target.setLoggerClass(source.getLoggerClass());
            target.setCallerClass(source.getCallerClass());
            target.setCallerFilename(source.getCallerFilename());
            target.setCallerLineNumber(source.getCallerLineNumber());
            target.setCallerMethod(source.getCallerMethod());
            return target;
         }

      };
   }

   public static Function<BewegungsMeldung, JournalBewegungsMeldung> mapBewegungsMeldungToJournalBewegungsMeldung() {
      return new Function<BewegungsMeldung, JournalBewegungsMeldung>() {

         @Override
         public JournalBewegungsMeldung apply(final BewegungsMeldung source) {
            final JournalBewegungsMeldung target = new JournalBewegungsMeldung();
            target.setId(source.getId());
            target.setCheckpoint(mapCheckpointToCheckpointDetails().apply(source.getCheckpoint()));
            target.setMillis(source.getMillis());
            if (source.getPerson() != null) {
               target.setPerson(mapToPersonDetails(source.getPerson()));
            }
            target.setPraesenzStatus(source.getOperatorAktion().name());
            return target;
         }
      };
   }

   public static JournalGefechtsMeldung mapToJournalGefechtsMeldung(final GefechtsMeldung gefechtsMeldung) {
      final JournalGefechtsMeldung meldung = new JournalGefechtsMeldung();
      meldung.setId(gefechtsMeldung.getId());
      meldung.setCheckpoint(mapCheckpointToCheckpointDetails().apply(gefechtsMeldung.getCheckpoint()));
      meldung.setMillis(gefechtsMeldung.getMillis());
      meldung.setIstErledigt(gefechtsMeldung.isIstErledigt());
      meldung.setMassnahme(gefechtsMeldung.getMassnahme());
      meldung.setWerWasWoWie(gefechtsMeldung.getWerWasWoWie());
      meldung.setZeitpunktErledigt(gefechtsMeldung.getZeitpunktErledigt());
      meldung.setZeitpunktMeldungsEingang(gefechtsMeldung.getZeitpunktMeldungsEingang());
      if (gefechtsMeldung.getWeiterleitenAnPerson() != null) {
         meldung.setWeiterleitenAnPerson(mapToPersonDetails(gefechtsMeldung.getWeiterleitenAnPerson()));
      }
      return meldung;
   }
   
   public static PersonDetails mapToPersonDetails(final Person person) {
      final PersonDetails personDetails = new PersonDetails();
      personDetails.setId(person.getId());
      personDetails.setAhvNr(person.getAhvNr());
      personDetails.setName(person.getName());
      personDetails.setVorname(person.getVorname());

      if (person.getValidAusweis() != null) {
         personDetails.setBarcode(person.getValidAusweis().getBarcode());
      }

      final Grad grad = person.getGrad();
      personDetails.setGrad(grad != null ? grad.getGradText() : "");

      personDetails.setFunktion(person.getFunktion());
      personDetails.setGeburtsdatum(person.getGeburtsdatum());

      final Einheit einheit = person.getEinheit();
      personDetails.setEinheitId(einheit != null ? einheit.getId() : -1);
      personDetails.setEinheitText(einheit != null ? einheit.getName() : "");

      if (PersonImageStore.hasImage(person.getAhvNr())) {
         personDetails.setImageId(person.getAhvNr());
      }

      return personDetails;
   }
   
   public static Function<PersonDetails, Person> mapPersonDetailsToPerson() {

      return new Function<PersonDetails, Person>() {

         @Override
         public Person apply(final PersonDetails source) {
            final Person target = new Person();

            target.setId(source.getId());
            target.setAhvNr(source.getAhvNr());
            target.setName(source.getName());
            target.setVorname(source.getVorname());
            target.setGrad(Grad.getGrad(source.getGrad()));
            target.setFunktion(source.getFunktion());
            target.setGeburtsdatum(source.getGeburtsdatum());

            return target;
         }
      };

   }

   public static Function<Checkpoint, CheckpointDetails> mapCheckpointToCheckpointDetails() {

      return new Function<Checkpoint, CheckpointDetails>() {

         @Override
         public CheckpointDetails apply(final Checkpoint source) {
            if (source == null) {
               return null;
            }
            final CheckpointDetails target = new CheckpointDetails();
            target.setId(source.getId());
            target.setName(source.getName());
            return target;
         }
      };

   }

   public static Function<CheckpointDetails, Checkpoint> mapCheckpointDetailsToCheckpoint() {
      return new Function<CheckpointDetails, Checkpoint>() {
         @Override
         public Checkpoint apply(final CheckpointDetails source) {
            if (source == null) {
               return null;
            }
            final Checkpoint target = new Checkpoint();
            target.setId(source.getId());
            target.setName(source.getName());
            return target;
         }
      };
   }

   public static EinheitDetails mapToEinheitDetails(Einheit einheit) {
      final EinheitDetails target = new EinheitDetails();
      target.setId(einheit.getId());
      target.setName(einheit.getName());
      target.setRgbColor_GsVb(einheit.getRgbColor_GsVb());
      target.setRgbColor_TrpK(einheit.getRgbColor_TrpK());
      target.setRgbColor_Einh(einheit.getRgbColor_Einh());
      target.setText_GsVb(einheit.getText_GsVb());
      target.setText_TrpK(einheit.getText_TrpK());
      target.setText_Einh(einheit.getText_Einh());
      target.setRgbColor_BackgroundAusweis(einheit.getRgbColor_AusweisBackground());
      return target;
   }
   
   public static Function<EinheitDetails, Einheit> mapEinheitDetailsToEinheit() {
      return new Function<EinheitDetails, Einheit>() {
         @Override
         public Einheit apply(final EinheitDetails source) {
            final Einheit target = new Einheit();
            target.setId(source.getId());
            target.setName(source.getName());
            target.setRgbColor_GsVb(source.getRgbColor_GsVb());
            target.setRgbColor_TrpK(source.getRgbColor_TrpK());
            target.setRgbColor_Einh(source.getRgbColor_Einh());
            target.setText_GsVb(source.getText_GsVb());
            target.setText_TrpK(source.getText_TrpK());
            target.setText_Einh(source.getText_Einh());
            target.setRgbColor_AusweisBackground(source.getRgbColor_BackgroundAusweis());
            return target;
         }
      };
   }

   public static Function<ConfigurationValue, ConfigurationDetails> mapConfigurationValuetoConfigurationDetails() {
      return new Function<ConfigurationValue, ConfigurationDetails>() {
         @Override
         public ConfigurationDetails apply(final ConfigurationValue source) {
            final ConfigurationDetails target = new ConfigurationDetails();
            target.setId(source.getId());
            target.setKey(source.getKey());
            target.setLongValue(source.getLongValue());
            target.setStringValue(source.getStringValue());
            target.setValidFor(source.getValidFor());
            return target;
         }
      };
   }

   public static Function<ConfigurationDetails, ConfigurationValue> mapConfigurationDetailsToConfigurationValue() {
      return new Function<ConfigurationDetails, ConfigurationValue>() {
         @Override
         public ConfigurationValue apply(final ConfigurationDetails source) {
            final ConfigurationValue target = new ConfigurationValue();
            target.setId(source.getId());
            target.setKey(source.getKey());
            target.setLongValue(source.getLongValue());
            target.setStringValue(source.getStringValue());
            target.setValidFor(source.getValidFor());
            return target;
         }
      };
   }

}
