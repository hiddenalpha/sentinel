package ch.infbr5.sentinel.client.gui.components.journal.panel;

import java.sql.Date;
import java.util.List;

import ch.infbr5.sentinel.client.util.ServiceHelper;
import ch.infbr5.sentinel.client.wsgen.JournalEintrag;
import ch.infbr5.sentinel.client.wsgen.JournalResponse;
import ch.infbr5.sentinel.client.wsgen.JournalSystemMeldung;

public class SystemJournalModel extends AbstractJournalModel {

   private static final long serialVersionUID = 1L;

   private final String[] columnNames = { "Datum", "Checkpoint", "Level", "Nachricht" };

   private List<JournalSystemMeldung> meldungen;

   private final Object lock = new Object();

   public SystemJournalModel(final List<JournalSystemMeldung> meldungen) {
      this.meldungen = meldungen;
   }

   @Override
   public JournalEintrag getItem(final int row) {
      synchronized (lock) {
         return meldungen.get(row);
      }
   }

   @Override
   public void reload() {
      synchronized (lock) {
         final JournalResponse response = ServiceHelper.getJournalService().getSystemJournal();
         meldungen.clear();
         meldungen = response.getSystemMeldungen();
      }
      fireTableDataChanged();
   }

   @Override
   public String getColumnName(final int column) {
      return columnNames[column];
   }

   @Override
   public int getRowCount() {
      synchronized (lock) {
         return meldungen.size();
      }
   }

   @Override
   public int getColumnCount() {
      return columnNames.length;
   }

   public void add(final JournalSystemMeldung m) {
      synchronized (lock) {
         this.meldungen.add(0, m);
      }
      fireTableRowsInserted(0, 0);
   }

   @Override
   public Object getValueAt(final int rowIndex, final int columnIndex) {
      final JournalSystemMeldung meldung;
      synchronized (lock) {
         meldung = meldungen.get(rowIndex);
      }
      if (columnIndex == 0) {
         return new Date(meldung.getMillis());
      }
      if (columnIndex == 1) {
         if (meldung.getCheckpoint() == null) {
            return "";
         }
         return meldung.getCheckpoint().getName();
      }
      if (columnIndex == 2) {
         return meldung.getLevel();
      }
      if (columnIndex == 3) {
         return meldung.getMessage();
      }
      return "na";
   }

   @Override
   public void removeAll() {
      ServiceHelper.getJournalService().removeAllSystemMeldungen();
      meldungen.clear();
      fireTableDataChanged();
   }

}
