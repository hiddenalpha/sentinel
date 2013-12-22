package ch.infbr5.sentinel.client.gui.components.journal.old;

import javax.xml.datatype.XMLGregorianCalendar;

public interface JournalEintragModel {
	public Object get(int col);

	public String getCause();

	public String getCreator();

	public XMLGregorianCalendar getDate();

	public void set(int col, Object value);

	public void setCause(String cause);

	public void setCreator(String creator);

	public void setDate(XMLGregorianCalendar date);
}
