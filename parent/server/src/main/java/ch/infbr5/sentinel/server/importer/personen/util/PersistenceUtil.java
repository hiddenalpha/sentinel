package ch.infbr5.sentinel.server.importer.personen.util;

import java.util.Date;

import ch.infbr5.sentinel.server.db.EntityManagerHelper;
import ch.infbr5.sentinel.server.db.QueryHelper;
import ch.infbr5.sentinel.server.model.Ausweis;
import ch.infbr5.sentinel.server.model.Einheit;
import ch.infbr5.sentinel.server.model.Grad;
import ch.infbr5.sentinel.server.model.ObjectFactory;
import ch.infbr5.sentinel.server.model.Person;
import ch.infbr5.sentinel.server.ws.PersonDetails;
import ch.infbr5.sentinel.server.ws.importer.mapping.PersonenAttribute;

public class PersistenceUtil {

	private static String NAME_EINHEIT_ARCHIV = "_Archiv_";

	private static String NAME_EINHEIT_GAST = "GAST";

	/**
	 * Gibt die Einheit _Archiv_ zurÃ¼ck. Falls diese nicht existiert wird sie
	 * erzeugt.
	 * 
	 * @return _Archiv_ Einheit
	 */
	public static Einheit getArchivEinheit() {
		return createEinheit(NAME_EINHEIT_ARCHIV);
	}

	/**
	 * Erzeugt auf jedenfall die Person.
	 * 
	 * @param personDetails
	 *            PersonDetails.
	 * @param einheit
	 *            Einheit.
	 * @return Person.
	 */
	public static Person createPerson(PersonDetails personDetails, Einheit einheit) {
		return QueryHelper.createPerson(einheit,
				personDetails.getAhvNr(),
				Grad.getGrad(personDetails.getGrad()),
				personDetails.getName(),
				personDetails.getVorname(),
				personDetails.getGeburtsdatum(),
				personDetails.getFunktion());
	}


	/**
	 * Sucht die Person in der Datenbank aufgrund des Datensatzes.
	 * 
	 * Zuerst wird mit der AHVNr gesucht. Falls keine Person gefunden wird, wird mit Name, Vorname und Geburtsdatum gearbeitet.
	 * 
	 * @param PersonDetails personDetaills
	 * @return Person, falls eine gefunden wurde, anderenfalls keine.
	 */
	public static Person findPerson(PersonDetails personDetail) {
		Person p = QueryHelper.getPerson(personDetail.getAhvNr());
		if (p == null) {
			// Ich finde das nicht so schlau...?!
			p = QueryHelper.getPerson(personDetail.getName(), personDetail.getVorname(), personDetail.getGeburtsdatum());
		}
		return p;
	}
	
	public static Person findPerson(DataRow dataRow) {
		Person p = QueryHelper.getPerson(dataRow.getValue(PersonenAttribute.AHVNr));
		if (p == null) {
			p = QueryHelper.getPerson(dataRow.getValue(PersonenAttribute.Name), dataRow.getValue(PersonenAttribute.Vorname), dataRow.getGeburtstag());
		}
		return p;
	}
	
	/**
	 * Entfernt den gÃ¼ltigen Ausweis der Person, falls die Person einen hat.
	 * Ausserdem wird der Ausweis deaktiviert.
	 * 
	 * @param person
	 *            Person
	 */
	public static void removeValidAusweis(Person person) {
		Ausweis ausweis = person.getValidAusweis();
		if (ausweis != null) {
			deactivateAusweis(ausweis);
			person.setValidAusweis(null);
		}
	}

	/**
	 * Deaktiviert den Ausweis. Dannach ist der Ausweis nicht mehr gültig.
	 * 
	 * @param ausweis
	 *            Ausweis
	 */
	public static void deactivateAusweis(Ausweis ausweis) {
		ausweis.setInvalid(true);
		ausweis.setErstellt(true);
		ausweis.setGueltigBis(new Date());
	}

	/**
	 * Sucht die Einheit in der Datenbank aufgrund des Namens.
	 * 
	 * @param String
	 *            Name der Einheit.
	 * @return Falls die Einheit existiert, dann die Einheit sonst null.
	 */
	private static Einheit findEinheit(String name) {
		return QueryHelper.getEinheit(name);
	}

	/**
	 * Sucht die Einheit in der Datenbank aufgrund des Namens.
	 * 
	 * Falls keine Einheit gefunden wird, wird eine mit diesem Name erstellt und
	 * persistiert.
	 * 
	 * @param String
	 *            Name der Einheit.
	 * @return Immer eine Einheit.
	 */
	private static Einheit createEinheit(String name) {
		Einheit einheit = findEinheit(name);
		if (einheit == null) {
			einheit = ObjectFactory.createEinheit(name);
			EntityManagerHelper.getEntityManager().persist(einheit);
		}
		return einheit;
	}

	/**
	 * Prüft ob die Einheit mit diesem Namen existiert.
	 * 
	 * @param name
	 *            Name der Einheit
	 * @return True, falls die Einheit existiert, anderenfalls false.
	 */
	private static boolean existsEinheit(String name) {
		Einheit einheit = findEinheit(name);
		return (einheit != null);
	}

	/**
	 * Gibt die Einheit zum Namen zurück. Falls die Einheit nicht existiert wird
	 * sie erzeugt.
	 * 
	 * @param einheitName
	 *            Name der Einheit
	 * @param isKompletterBestand
	 *            Gibt an ob der Bestand Komplett ist. Falls ja dann wird die
	 *            Einheit definitv erzeugt, falls sie nicht existiert.
	 *            Anderenfalls wird die Gast Einheit geladen falls die Einheit
	 *            nicht existiert.
	 * @return Einheit (nie NULL).
	 */
	public static Einheit createEinheitKompletterBestand(String einheitName,
			boolean isKompletterBestand) {
		Einheit einheit;
		if (isKompletterBestand) {
			einheit = createEinheit(einheitName);
		} else {
			if (existsEinheit(einheitName)) {
				einheit = findEinheit(einheitName);
			} else {
				einheit = createEinheit(NAME_EINHEIT_GAST);
			}
		}
		return einheit;
	}
	
	public static void updatePerson(Person person, PersonDetails details, boolean isKompletterBestand) {
		person.setAhvNr(details.getAhvNr());
		person.setName(details.getName());
		person.setVorname(details.getVorname());
		person.setFunktion(details.getFunktion());
		person.setGrad(Grad.getGrad(details.getGrad()));
		person.setGeburtsdatum(details.getGeburtsdatum());
		Einheit einheit = PersistenceUtil.createEinheitKompletterBestand(details.getEinheitText(), isKompletterBestand);
		person.setEinheit(einheit);
	}
	
}
