package ch.infbr5.sentinel.server.importer.personen;

import java.text.ParseException;
import java.util.Calendar;
import java.util.List;
import java.util.logging.Logger;

import ch.infbr5.sentinel.server.model.Grad;
import ch.infbr5.sentinel.server.model.Person;
import ch.infbr5.sentinel.server.utils.DateHelper;
import ch.infbr5.sentinel.server.ws.importer.MappingPersonenAttributeToColumn;
import ch.infbr5.sentinel.server.ws.importer.PersonenAttribute;

class DataRow {
	
	private static Logger log = Logger.getLogger(DataRow.class.getName());
	
	private String[] data;
	
	private List<MappingPersonenAttributeToColumn> mappings;
	
	private Calendar geburtstag;
	
	public DataRow(String[] data, List<MappingPersonenAttributeToColumn> mappings) {
		this.data = data;
		this.mappings = mappings;
		try {
			this.geburtstag = DateHelper.getCalendar(this.getValue(PersonenAttribute.Geburtstag));
		} catch (ParseException e) {
			// TODO Mit Pascal besprechen
			log.warning("Geburtsdatum von " + getValue(PersonenAttribute.AHVNr) + " konnte nicht geparst werden.");
			this.geburtstag = Calendar.getInstance();
		}
	}
	
	public Calendar getGeburtstag() {
		return this.geburtstag;
	}
	
	public String getValue(PersonenAttribute attribute) {
		for (MappingPersonenAttributeToColumn mapping : mappings) {
			if (mapping.getPersonenAttribute().equals(attribute)) {
				return data[mapping.getColumn().getIndex()];
			}
		}
		return null;
	}
	
	/**
	 * Berechnet ob ein neue Ausweis benötigt wird. Der Abgleich wird zwischen dem Datensatz und der übergebenen Person gemacht.
	 * @param oldPerson Alte Personendaten.
	 * @return True, falls ein neuer Ausweis benötigt wird, anderenfalls false.
	 */
	public boolean wirdNeuerAusweisBenötigt(Person oldPerson) {
		if (!oldPerson.getAhvNr().equals(this.getValue(PersonenAttribute.AHVNr))) {
			return true;
		}
		if (!oldPerson.getName().equals(this.getValue(PersonenAttribute.Name))) {
			return true;
		}
		if (!oldPerson.getVorname().equals(this.getValue(PersonenAttribute.Vorname))) {
			return true;
		}
		if (!oldPerson.getFunktion().equals(this.getValue(PersonenAttribute.Funktion))) {
			return true;
		}
		if ((oldPerson.getGrad() == null)
				|| (!oldPerson.getGrad().equals(Grad.getGrad(this.getValue(PersonenAttribute.Grad))))) {
			return true;
		}
		if ((oldPerson.getGeburtsdatum() == null) || (!DateHelper.getFormatedString(oldPerson.getGeburtsdatum()).equals(this.getValue(PersonenAttribute.Geburtstag)))) {
			return true;
		}
		if ((oldPerson.getEinheit() == null)
				|| (!oldPerson.getEinheit().getName().equals(this.getValue(PersonenAttribute.Einheit)))) {
			return true;
		}

		return false;
	}
	
}
