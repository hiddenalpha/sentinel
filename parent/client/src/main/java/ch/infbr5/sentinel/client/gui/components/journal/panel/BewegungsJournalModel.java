package ch.infbr5.sentinel.client.gui.components.journal.panel;

import java.sql.Date;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import ch.infbr5.sentinel.client.util.PersonDetailsFormater;
import ch.infbr5.sentinel.client.wsgen.JournalBewegungsMeldung;

public class BewegungsJournalModel extends AbstractTableModel {

   private static final long serialVersionUID = 1L;

   private final String[] columnNames = { "Datum", "Checkpoint", "Name", "Aktion" };

   private final List<JournalBewegungsMeldung> meldungen;

   public BewegungsJournalModel(final List<JournalBewegungsMeldung> meldungen) {
      this.meldungen = meldungen;
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

   public void add(final JournalBewegungsMeldung m) {
      this.meldungen.add(0, m);
      fireTableRowsInserted(0, 0);
   }

   @Override
   public Object getValueAt(final int rowIndex, final int columnIndex) {
      final JournalBewegungsMeldung meldung = meldungen.get(rowIndex);
      if (columnIndex == 0) {
         return new Date(meldung.getMillis());
      }
      if (columnIndex == 1) {
         return meldung.getCheckpoint().getName();
      }
      if (columnIndex == 2) {
         return PersonDetailsFormater.getFullName(meldung.getPerson());
      }
      if (columnIndex == 3) {
         return meldung.getPraesenzStatus();
      }
      return "na";
   }

}
