package ch.infbr5.sentinel.client.gui.components.journal.panel;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.table.TableCellRenderer;

import ch.infbr5.sentinel.client.util.ServiceHelper;
import ch.infbr5.sentinel.client.wsgen.JournalEintrag;
import ch.infbr5.sentinel.client.wsgen.LongArray;

public abstract class AbstractJournalTable extends JTable {

   private static final long serialVersionUID = 1L;

   private final AbstractJournalModel model;

   private boolean adminModus;

   protected AbstractJournalTable(final AbstractJournalModel model, final boolean adminMode) {
      super(model);
      this.model = model;
      this.adminModus = adminMode;
      installMouseListener();
   }

   private void installMouseListener() {
      addMouseListener(new MouseAdapter() {
         @Override
         public void mousePressed(final MouseEvent e) {
            if (SwingUtilities.isRightMouseButton(e)) {
               final JTable table = (JTable) e.getSource();
               final int row = table.rowAtPoint(e.getPoint());
               final JournalEintrag journalEintrag = model.getItem(row);

               final JPopupMenu menu = new JPopupMenu();
               final List<JMenuItem> items = getContextMenuItems(journalEintrag);
               for (final JMenuItem item : items) {
                  menu.add(item);
               }
               if (!items.isEmpty() && adminModus) {
                  menu.addSeparator();
               }
               if (adminModus) {
                  menu.add(createRemoveItem());
               }
               menu.show(table, e.getX(), e.getY());
            }
         }

      });
   }

   public void setAdminMode(final boolean mode) {
      this.adminModus = mode;
   }

   protected List<JMenuItem> getContextMenuItems(final JournalEintrag eintrag) {
      return new ArrayList<>();
   }

   protected void setDefaultSort(final int columnIndex, final boolean asc) {
      getRowSorter().toggleSortOrder(columnIndex);
      if (!asc) {
         getRowSorter().toggleSortOrder(columnIndex);
      }
   }

   protected void setCellRenderer(final int columnIndex, final TableCellRenderer renderer) {
      getColumnModel().getColumn(columnIndex).setCellRenderer(renderer);
   }

   protected JMenuItem createRemoveItem() {
      final JMenuItem item = new JMenuItem("Löschen");
      item.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(final ActionEvent e) {
            final int[] selectedRows = getSelectedRows();

            if (selectedRows.length > 0) {
               final LongArray ids = new LongArray();
               for (final int selectedRow : selectedRows) {
                  ids.getItem().add(model.getItem(selectedRow).getId());
               }

               final int answer = JOptionPane.showConfirmDialog(null, "Wollen Sie die Daten endgültig löschen?",
                     "Daten löschen", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
               if (answer == JOptionPane.YES_OPTION) {
                  ServiceHelper.getJournalService().removeJournalEintrage(ids);
                  model.reload();
               }
            }
         }
      });
      return item;
   }

}
