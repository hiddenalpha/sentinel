package ch.infbr5.sentinel.client.gui.components.journal.old;

import java.util.Date;
import java.util.GregorianCalendar;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

public abstract class JournalEintragModelImpl implements JournalEintragModel {
	private String creator;
	private XMLGregorianCalendar date;
	private String cause;

	public JournalEintragModelImpl() {
		GregorianCalendar c = new GregorianCalendar();
		c.setTime(new Date());

		try {
			this.setDate(DatatypeFactory.newInstance().newXMLGregorianCalendar(c));
		} catch (DatatypeConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// TODO: mk, 20111116: nur eine übergangslösung wegen vector und object[][]
	public Object get(int col) {
		if (col == 0) {
			return this.getCreator();
		} else if (col == 1) {
			return this.getDate();
		} else if (col == 2) {
			return this.getCause();
		} else {
			return "error, wrong col: " + col;
		}
	}

	public String getCause() {
		return this.cause;
	}

	public String getCreator() {
		return this.creator;
	}

	public XMLGregorianCalendar getDate() {
		return this.date;
	}

	public void set(int col, Object value) {
		if (col == 0) {
			this.setCreator((String) value);
		} else if (col == 1) {
			this.setDate((XMLGregorianCalendar) value);
		} else if (col == 2) {
			this.setCause((String) value);
		} else {
			System.out.println("error, setting value: " + value + ", col: "
					+ col);
		}
	}

	public void setCause(String cause) {
		this.cause = cause;
	}

	public void setCreator(String creator) {
		this.creator = creator;
	}

	public void setDate(XMLGregorianCalendar date) {
		this.date = date;
	}
}
