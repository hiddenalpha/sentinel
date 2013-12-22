package ch.infbr5.sentinel.server.importer;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import au.com.bytecode.opencsv.CSVReader;
import ch.infbr5.sentinel.server.db.EntityManagerHelper;
import ch.infbr5.sentinel.server.db.QueryHelper;
import ch.infbr5.sentinel.server.model.Ausweis;
import ch.infbr5.sentinel.server.model.Einheit;
import ch.infbr5.sentinel.server.model.Grad;
import ch.infbr5.sentinel.server.model.ObjectFactory;
import ch.infbr5.sentinel.server.model.Person;
import ch.infbr5.sentinel.server.utils.DateHelper;
import ch.infbr5.sentinel.server.utils.FileHelper;

public class PisaCsvReader {
	private CSVReader reader;
	private int ahvNr;
	private int name;
	private int vorname;
	private int funktion;
	private int einheit;
	private int geburtsdatum;
	private int grad;

	private Set<Einheit> importedEinheiten;
	private Set<Person> importedPersonen;
	private static final String CSV_FILENAME = "pisaImportData.xml";

	private boolean isKompletteEinheit;

	public PisaCsvReader(byte[] data, boolean isKompletteEinheit) {

		FileHelper.saveAsFile(CSV_FILENAME, data);

		try {
			reader = new CSVReader(new InputStreamReader(new FileInputStream(
					CSV_FILENAME), "ISO-8859-1"), ';', '"');
			String[] headerLine = reader.readNext();

			ahvNr = findColumn(headerLine, ".*ahv.*");
			if (ahvNr < 0) {
				ahvNr = findColumn(headerLine, ".*versicherung.*");
			}
			name = findColumn(headerLine, ".*name.*");
			vorname = findColumn(headerLine, ".*vorname.*");
			funktion = findColumn(headerLine, ".*funktion.*");
			einheit = findColumn(headerLine, ".*einheit.*");
			if (einheit < 0) {
				einheit = findColumn(headerLine, ".*einteilung.*");
			}
			geburtsdatum = findColumn(headerLine, ".*geburtsdatum.*");
			if (geburtsdatum < 0) {
				geburtsdatum = findColumn(headerLine, ".*geb.*");
			}
			grad = findColumn(headerLine, ".*grad.*");

			importedEinheiten = new HashSet<Einheit>();
			importedPersonen = new HashSet<Person>();

			this.isKompletteEinheit = isKompletteEinheit;

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public boolean isFileValid() {
		return ((ahvNr > -1) & (name > -1) & (vorname > -1) & (funktion > -1)
				& (einheit > -1) & (geburtsdatum > -1));
	}

	public void read() {
		try {
			String[] nextLine;
			while ((nextLine = reader.readNext()) != null) {
				Person p = findPerson(nextLine);
				String logOld;
				boolean createNewAusweis = false;

				if (p == null) {
					logOld = "";
					try {
						Einheit e;
						if (isKompletteEinheit) {
							e = findEinheit(nextLine[einheit]);
						} else {
							e = findEinheit("GAST");
						}
						p = QueryHelper.createPerson(e, nextLine[ahvNr],
								Grad.getGrad(nextLine[grad]), nextLine[name],
								nextLine[vorname],
								DateHelper.getCalendar(nextLine[geburtsdatum]),
								nextLine[funktion]);
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} else {
					logOld = p.toString();
				}

				if (!p.getAhvNr().equals(nextLine[ahvNr])) {
					p.setAhvNr(nextLine[ahvNr]);
					createNewAusweis = true;
				}
				if (!p.getName().equals(nextLine[name])) {
					p.setName(nextLine[name]);
					createNewAusweis = true;
				}
				if (!p.getVorname().equals(nextLine[vorname])) {
					p.setVorname(nextLine[vorname]);
					createNewAusweis = true;
				}
				if (!p.getFunktion().equals(nextLine[funktion])) {
					p.setFunktion(nextLine[funktion]);
					createNewAusweis = true;
				}
				if ((p.getGrad() == null)
						|| (!p.getGrad().equals(Grad.getGrad(nextLine[grad])))) {
					p.setGrad(Grad.getGrad(nextLine[grad]));
					createNewAusweis = true;
				}

				try {
					if ((p.getGeburtsdatum() == null)
							|| (!DateHelper.getFormatedString(
									p.getGeburtsdatum()).equals(
									nextLine[geburtsdatum]))) {
						p.setGeburtsdatum(DateHelper
								.getCalendar(nextLine[geburtsdatum]));
						createNewAusweis = true;
					}
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				if ((p.getEinheit() == null)
						|| (!p.getEinheit().getName().equals(nextLine[einheit]))) {
					Einheit e;
					if (isKompletteEinheit) {
						e = findEinheit(nextLine[einheit]);
					} else {
						e = findEinheit("GAST");
					}
					p.setEinheit(e);
					createNewAusweis = true;
				}

				Ausweis oldA = p.getValidAusweis();
				if (oldA != null) {
					if (createNewAusweis) {
						oldA.setGueltigBis(new Date());
						oldA.setInvalid(true);
						oldA.setErstellt(true);
						QueryHelper.createAusweis(p.getId());
						System.out.println("[" + logOld + "] to ["
								+ p.toString() + "]");
					}
				}

				importedPersonen.add(p);
				importedEinheiten.add(p.getEinheit());

			}

			if (isKompletteEinheit) {
				cleanup();
			}

		} catch (IOException ioEx) {
			// TODO Auto-generated catch block
			ioEx.printStackTrace();

		}
	}

	private void cleanup() {

		List<Person> personen = QueryHelper.getPersonen();
		Einheit archivEinheit = QueryHelper.getEinheit("_Archiv_");

		for (Iterator<Person> iterator = personen.iterator(); iterator
				.hasNext();) {
			Person p = iterator.next();

			if (!importedPersonen.contains(p)) {
				if ((p.getEinheit() != null)
						&& (importedEinheiten.contains(p.getEinheit()))) {
					if (archivEinheit == null) {
						archivEinheit = ObjectFactory.createEinheit("_Archiv_");
						EntityManagerHelper.getEntityManager().persist(
								archivEinheit);
					}

					p.setEinheit(archivEinheit);
					if (p.getValidAusweis() != null) {
						p.getValidAusweis().setInvalid(true);
						p.getValidAusweis().setErstellt(true);
						p.getValidAusweis().setGueltigBis(new Date());
						p.setValidAusweis(null);
						// TODO
						System.out.println("Ausweis gesperrt: " + p.toString());
					}

					// TODO
					System.out.println("Nicht gefunden: " + p.toString());
				}
			}

		}

	}

	private Person findPerson(String[] data) {
		Person p = QueryHelper.getPerson(data[ahvNr]);
		if (p == null) {
			try {
				p = QueryHelper.getPerson(data[name], data[vorname],
						DateHelper.getCalendar(data[geburtsdatum]));
			} catch (ParseException e) {

			}
		}
		return p;
	}

	private Einheit findEinheit(String name) {
		Einheit e = QueryHelper.getEinheit(name);
		if (e == null) {
			e = ObjectFactory.createEinheit(name);
			EntityManagerHelper.getEntityManager().persist(e);
		}
		return e;
	}

	private int findColumn(String[] headerLine, String pattern) {
		for (int i = 0; i < headerLine.length; i++) {
			if (headerLine[i].toLowerCase().matches(pattern))
				return i;
		}
		return -1;
	}

}
