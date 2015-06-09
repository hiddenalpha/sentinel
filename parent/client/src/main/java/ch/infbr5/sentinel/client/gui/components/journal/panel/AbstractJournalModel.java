package ch.infbr5.sentinel.client.gui.components.journal.panel;

import javax.swing.table.AbstractTableModel;

import ch.infbr5.sentinel.client.wsgen.JournalEintrag;

public abstract class AbstractJournalModel extends AbstractTableModel {

   private static final long serialVersionUID = 1L;

   public abstract JournalEintrag getItem(int row);

   public abstract void reload();

   public abstract void removeAll();

}
