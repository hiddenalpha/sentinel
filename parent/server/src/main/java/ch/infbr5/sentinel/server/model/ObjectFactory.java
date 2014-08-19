package ch.infbr5.sentinel.server.model;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import ch.infbr5.sentinel.server.mapper.Mapper;
import ch.infbr5.sentinel.server.model.journal.SystemMeldung;
import ch.infbr5.sentinel.server.ws.journal.JournalSystemMeldung;

public class ObjectFactory {

	public static final String BARCODE_PREFIX = "A";
	public static final int BARCODE_LAENGE = 6;

	public static Ausweis createAusweis(Person person, String barcode) {
		Ausweis ausweis = new Ausweis();
		ausweis.setBarcode(barcode);
		ausweis.setPerson(person);
		ausweis.setErstellt(false);
		ausweis.setInvalid(false);
		ausweis.setGueltigVon(new Date());
		ausweis.setPasswort("");
		return ausweis;
	}

	public static Checkpoint createCheckpoint(String name, List<Zone> checkInZonen, List<Zone> checkOutZonen) {
		Checkpoint c = new Checkpoint();
		c.setName(name);
		c.setCheckInZonen(checkInZonen);
		c.setCheckOutZonen(checkOutZonen);

		return c;
	}

	public static Einheit createEinheit(String name) {
		Einheit e = new Einheit();
		e.setName(name);

		return e;
	}

	public static Person createPerson(Einheit einheit, String ahvNr, Grad grad, String name, String vorname,
			Calendar geburtsdatum, String funktion) {
		Person person = new Person();
		person.setAhvNr(ahvNr);
		person.setGrad(grad);
		person.setName(name);
		person.setVorname(vorname);
		person.setGeburtsdatum(geburtsdatum);
		person.setFunktion(funktion);
		person.setEinheit(einheit);

		return person;
	}

	public static ZonenPraesenz createPraesenzInZone(Zone zone, Person person, PraesenzStatus status) {
		ZonenPraesenz p = new ZonenPraesenz();
		p.setZone(zone);
		p.setPerson(person);
		p.setStatus(status);
		p.setVon(Calendar.getInstance());
		// Calendar calMax = Calendar.getInstance();
		// calMax.set(2100, 0, 1, 0, 0, 0);
		// p.setBis(calMax);

		return p;
	}

	public static SystemMeldung createSystemEintrag() {
		return new SystemMeldung();
	}

	public static Zone createZone(String name, List<Zutrittsregel> regeln, boolean undOpRegeln) {
		Zone z = new Zone();
		z.setName(name);
		z.setRegeln(regeln);
		z.setUndOpRegeln(undOpRegeln);

		return z;
	}

	public static Zutrittsregel createZutrittsregel() {
		return new Zutrittsregel();
	}

	public static ConfigurationValue createConfigurationValue(String key, String stringValue, long longValue, String validFor) {
		ConfigurationValue v = new ConfigurationValue();
		v.setKey(key);
		v.setStringValue(stringValue);
		v.setLongValue(longValue);
		v.setValidFor(validFor);
		return v;
	}

	public static SystemMeldung createEintragLog(JournalSystemMeldung log) {
		SystemMeldung r = new SystemMeldung();
		r.setCheckpoint(Mapper.mapCheckpointDetailsToCheckpoint().apply(log.getCheckpoint()));
		r.setLevel(log.getLevel());
		r.setMessage(log.getMessage());
		r.setMillis(log.getMillis());
		return r;
	}

	public static PrintJob createPrintJob(String desc, String file) {
		PrintJob j = new PrintJob();
		j.setPrintJobDesc(desc);
		j.setPintJobFile(file);
		j.setPrintJobDate(new Date());
		return j;
	}


}
