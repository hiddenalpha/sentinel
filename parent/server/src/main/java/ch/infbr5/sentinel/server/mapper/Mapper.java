package ch.infbr5.sentinel.server.mapper;

import ch.infbr5.sentinel.server.db.QueryHelper;
import ch.infbr5.sentinel.server.model.Checkpoint;
import ch.infbr5.sentinel.server.model.Person;
import ch.infbr5.sentinel.server.model.journal.BewegungsMeldung;
import ch.infbr5.sentinel.server.model.journal.GefechtsMeldung;
import ch.infbr5.sentinel.server.model.journal.SystemMeldung;
import ch.infbr5.sentinel.server.ws.CheckpointDetails;
import ch.infbr5.sentinel.server.ws.PersonDetails;
import ch.infbr5.sentinel.server.ws.journal.JournalBewegungsMeldung;
import ch.infbr5.sentinel.server.ws.journal.JournalGefechtsMeldung;
import ch.infbr5.sentinel.server.ws.journal.JournalSystemMeldung;

import com.google.common.base.Function;

public class Mapper {

	public static Function<JournalGefechtsMeldung, GefechtsMeldung> mapJournalGefechtsMeldungToGefechtsMeldung() {
		return new Function<JournalGefechtsMeldung, GefechtsMeldung>() {

			@Override
			public GefechtsMeldung apply(JournalGefechtsMeldung source) {
				GefechtsMeldung target = new GefechtsMeldung();
				target.setId(source.getId());
				target.setCheckpoint(mapCheckpointDetailsToCheckpoint().apply(source.getCheckpoint()));
				target.setMillis(source.getMillis());
				target.setIstErledigt(source.isIstErledigt());
				target.setMassnahme(source.getMassnahme());
				target.setWerWasWoWie(source.getWerWasWoWie());
				target.setZeitpunktErledigt(source.getZeitpunktErledigt());
				target.setZeitpunktMeldungsEingang(source.getZeitpunktMeldungsEingang());
				if (source.getWeiterleitenAnPerson() != null) {
					target.setWeiterleitenAnPerson(QueryHelper.getPerson(source.getWeiterleitenAnPerson().getId()));
				}
				return target;
			}

		};
	}

	public static Function<JournalBewegungsMeldung, BewegungsMeldung> mapJournalBewegungsMeldungToBewegungsMeldung() {

		return new Function<JournalBewegungsMeldung, BewegungsMeldung>()  {
			@Override
			public BewegungsMeldung apply(JournalBewegungsMeldung source) {
				BewegungsMeldung target = new BewegungsMeldung();
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
			public JournalSystemMeldung apply(SystemMeldung source) {
				JournalSystemMeldung target = new JournalSystemMeldung();
				target.setId(source.getId());
				target.setLevel(source.getLevel());
				target.setMessage(source.getMessage());
				target.setType(source.getType());
				target.setMillis(source.getMillis());
				target.setCheckpoint(mapCheckpointToCheckpointDetails().apply(source.getCheckpoint()));
				return target;
			}

		};
	}

	public static Function<JournalSystemMeldung, SystemMeldung> mapJournalSystemMeldungToSystemMeldung() {
		return new Function<JournalSystemMeldung, SystemMeldung>() {

			@Override
			public SystemMeldung apply(JournalSystemMeldung source) {
				SystemMeldung target = new SystemMeldung();
				target.setId(source.getId());
				target.setCheckpoint(mapCheckpointDetailsToCheckpoint().apply(source.getCheckpoint()));
				target.setLevel(source.getLevel());
				target.setMessage(source.getMessage());
				target.setType(source.getType());
				target.setMillis(source.getMillis());
				target.setLoggerClass(source.getLoggerClass());
				target.setMethod(source.getMethod());
				target.setOperator(source.getOperator());
				target.setReportedClass(source.getReportedClass());
				target.setSequence(source.getSequence());
				target.setThread(source.getThread());
				return target;
			}

		};
	}

	public static Function<BewegungsMeldung, JournalBewegungsMeldung> mapBewegungsMeldungToJournalBewegungsMeldung() {
		return new Function<BewegungsMeldung, JournalBewegungsMeldung>() {

			@Override
			public JournalBewegungsMeldung apply(BewegungsMeldung source) {
				JournalBewegungsMeldung target = new JournalBewegungsMeldung();
				target.setId(source.getId());
				target.setCheckpoint(mapCheckpointToCheckpointDetails().apply(source.getCheckpoint()));
				target.setMillis(source.getMillis());
				target.setPerson(mapPersonToPersonDetails().apply(source.getPerson()));
				target.setPraesenzStatus(source.getPraesenzStatus().name());
				return target;
			}
		};
	}

	public static Function<GefechtsMeldung, JournalGefechtsMeldung> mapGefechtsMeldungToJournalGefechtsMeldung() {
		return new Function<GefechtsMeldung, JournalGefechtsMeldung>() {

			@Override
			public JournalGefechtsMeldung apply(GefechtsMeldung source) {
				JournalGefechtsMeldung target = new JournalGefechtsMeldung();
				target.setId(source.getId());
				target.setCheckpoint(mapCheckpointToCheckpointDetails().apply(source.getCheckpoint()));
				target.setMillis(source.getMillis());
				target.setIstErledigt(source.isIstErledigt());
				target.setMassnahme(source.getMassnahme());
				target.setWerWasWoWie(source.getWerWasWoWie());
				target.setZeitpunktErledigt(source.getZeitpunktErledigt());
				target.setZeitpunktMeldungsEingang(source.getZeitpunktMeldungsEingang());
				if (source.getWeiterleitenAnPerson() != null) {
					target.setWeiterleitenAnPerson(mapPersonToPersonDetails().apply(source.getWeiterleitenAnPerson()));
				}
				return target;
			}
		};
	}

	public static Function<Person, PersonDetails> mapPersonToPersonDetails() {

		return new Function<Person, PersonDetails>() {

			@Override
			public PersonDetails apply(Person source) {
				PersonDetails target = new PersonDetails();
				target.setId(source.getId());
				target.setAhvNr(source.getAhvNr());
				target.setName(source.getName());
				target.setVorname(source.getVorname());
				target.setGrad(source.getGrad().getGradText());
				return target;
			}
		};

	}

	public static Function<Checkpoint, CheckpointDetails> mapCheckpointToCheckpointDetails() {

		return new Function<Checkpoint, CheckpointDetails>() {

			@Override
			public CheckpointDetails apply(Checkpoint source) {
				CheckpointDetails target = new CheckpointDetails();
				target.setId(source.getId());
				target.setName(source.getName());
				return target;
			}
		};

	}

	public static Function<CheckpointDetails, Checkpoint> mapCheckpointDetailsToCheckpoint() {

		return new Function<CheckpointDetails, Checkpoint>() {

			@Override
			public Checkpoint apply(CheckpointDetails source) {
				Checkpoint target = new Checkpoint();
				target.setId(source.getId());
				target.setName(source.getName());
				return target;
			}
		};

	}



}
