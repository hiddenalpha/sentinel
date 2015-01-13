package ch.infbr5.sentinel.client.gui.util;

import java.awt.Component;

import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;

public class TableColumnResizer {

   public static void resizeColumnWidth(final JTable table) {
      final TableColumnModel columnModel = table.getColumnModel();
      for (int column = 0; column < table.getColumnCount(); column++) {
         int width = 50; // Min width
         for (int row = 0; row < table.getRowCount(); row++) {
            final TableCellRenderer renderer = table.getCellRenderer(row, column);
            final Component comp = table.prepareRenderer(renderer, row, column);
            width = Math.max(comp.getPreferredSize().width, width);
         }
         columnModel.getColumn(column).setPreferredWidth(width);
      }
   }

}
