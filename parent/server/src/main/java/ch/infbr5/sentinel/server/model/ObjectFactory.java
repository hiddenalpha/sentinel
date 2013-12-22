package ch.infbr5.sentinel.server.model;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import ch.infbr5.sentinel.server.db.QueryHelper;
import ch.infbr5.sentinel.server.model.journal.OperatorEintrag;

public class ObjectFactory {

	private static final String BARCODE_PREFIX = "A";
	private static final int BARCODE_LAENGE = 6;

	public static Ausweis createAusweis(Person person) {
		Ausweis ausweis = new Ausweis();
		ausweis.setBarcode(QueryHelper.createUniqueBarcode(ObjectFactory.BARCODE_LAENGE, ObjectFactory.BARCODE_PREFIX));
		ausweis.setPerson(person);
		ausweis.setErstellt(false);
		ausweis.setInvalid(false);
		ausweis.setGueltigVon(new Date());

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

	public static OperatorEintrag createOperatorEintrag() {
		return new OperatorEintrag();
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

	public static LogEintrag createSystemEintrag() {
		return new LogEintrag();
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

	public static LogEintrag createLogEintrag(long checkpointId, String level, String message, long millis, int type,
			String loggerClass, String method, String operator, String reportedClass, long sequence, int thread) {
		LogEintrag r = createLogEintrag(checkpointId, level, message, millis, type);

		r.setLoggerClass(loggerClass);
		r.setMethod(method);
		r.setMillis(millis);
		r.setOperator(operator);
		r.setReportedClass(reportedClass);
		r.setSequence(sequence);
		r.setThread(thread);

		return r;
	}

	public static LogEintrag createLogEintrag(long checkpointId, String level, String message, long millis, int type) {
		LogEintrag r = new LogEintrag();
		r.setCheckpointId(checkpointId);
		r.setLevel(level);
		r.setMessage(message);
		r.setType(type);
		r.setMillis(millis);
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
