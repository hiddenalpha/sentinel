package ch.infbr5.sentinel.client.gui.components.journal.panel;

import java.util.Date;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import ch.infbr5.sentinel.client.util.PersonDetailsFormater;
import ch.infbr5.sentinel.client.util.XMLGregorianCalendarConverter;
import ch.infbr5.sentinel.client.wsgen.JournalGefechtsMeldung;
import ch.infbr5.sentinel.common.util.DateFormater;

public class GefechtsJournalModel extends AbstractTableModel {

   private static final long serialVersionUID = 1L;

   private final String[] columnNames = { "Datum", "Checkpoint", "Wer/Was/Wie/Wo", "Massnahmen", "Für wen?", "Erledigt" };

   private final List<JournalGefechtsMeldung> meldungen;

   public GefechtsJournalModel(final List<JournalGefechtsMeldung> meldungen) {
      this.meldungen = meldungen;
   }

   public JournalGefechtsMeldung getItem(final int row) {
      return meldungen.get(row);
   }

   @Override
   public String getColumnName(final int column) {
      return columnNames[column];
   }

   @Override
   public int getRowCount() {
      return meldungen.size();
   }

   @Override
   public int getColumnCount() {
      return columnNames.length;
   }

   public void add(final JournalGefechtsMeldung m) {
      this.meldungen.add(0, m);
      fireTableRowsInserted(0, 0);
   }

   @Override
   public Class<?> getColumnClass(final int columnIndex) {
      return String.class;
   }

   @Override
   public Object getValueAt(final int rowIndex, final int columnIndex) {
      final JournalGefechtsMeldung meldung = meldungen.get(rowIndex);
      if (columnIndex == 0) {
         return new Date(meldung.getMillis());
      }
      if (columnIndex == 1) {
         return meldung.getCheckpoint().getName();
      }
      if (columnIndex == 4) {
         return PersonDetailsFormater.getFullName(meldung.getWeiterleitenAnPerson());
      }
      if (columnIndex == 2) {
         return meldung.getWerWasWoWie();
      }
      if (columnIndex == 3) {
         return meldung.getMassnahme();
      }
      if (columnIndex == 5) {
         if (meldung.isIstErledigt()) {
            return DateFormater.formatToDateWithTime(meldung.getZeitpunktErledigt());
         }
         return "";
      }
      return "na";
   }

   public void setGefechtsMeldungToDone(final JournalGefechtsMeldung meldung) {
      for (final JournalGefechtsMeldung m : meldungen) {
         if (m.getId() == meldung.getId()) {
            m.setIstErledigt(true);
            m.setZeitpunktErledigt(XMLGregorianCalendarConverter.dateToXMLGregorianCalendar(new Date()));
            fireTableDataChanged();
         }
      }
   }

}
