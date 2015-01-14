package ch.infbr5.sentinel.client.gui.components.journal.panel;

import ch.infbr5.sentinel.client.gui.util.DateTimeCellRenderer;
import ch.infbr5.sentinel.client.gui.util.TableColumnResizer;

public class SystemJournalTable extends AbstractJournalTable {

   private static final long serialVersionUID = 1L;

   public SystemJournalTable(final SystemJournalModel model, final boolean adminMode) {
      super(model, adminMode);
      setCellRenderer(0, new DateTimeCellRenderer());
   }

   public void adjust() {
      setDefaultSort(0, false);
      TableColumnResizer.resizeColumnWidth(this);
   }

}
