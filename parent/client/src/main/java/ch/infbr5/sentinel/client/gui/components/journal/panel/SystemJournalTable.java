package ch.infbr5.sentinel.client.gui.components.journal.panel;

import javax.swing.JTable;

import ch.infbr5.sentinel.client.gui.util.DateTimeCellRenderer;
import ch.infbr5.sentinel.client.gui.util.TableColumnResizer;

public class SystemJournalTable extends JTable {

   private static final long serialVersionUID = 1L;

   public SystemJournalTable(final SystemJournalModel model) {
      super(model);
   }

   public void adjust() {
      this.getColumnModel().getColumn(0).setCellRenderer(new DateTimeCellRenderer());
      this.getRowSorter().toggleSortOrder(0);
      this.getRowSorter().toggleSortOrder(0);
      TableColumnResizer.resizeColumnWidth(this);
   }

}
