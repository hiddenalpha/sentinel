package ch.infbr5.sentinel.client.gui.util;

import java.awt.Component;
import java.util.Date;

import javax.swing.JTable;
import javax.xml.datatype.XMLGregorianCalendar;

import ch.infbr5.sentinel.common.util.DateFormater;

public class DateTimeCellRenderer extends javax.swing.table.DefaultTableCellRenderer {

   private static final long serialVersionUID = 1L;

   @Override
   public Component getTableCellRendererComponent(final JTable table, Object value, final boolean isSelected,
         final boolean hasFocus, final int row, final int column) {
      if (value != null && value instanceof Date) {
         value = DateFormater.formatToDateWithDetailTime((Date) value);
      }
      if (value != null && value instanceof XMLGregorianCalendar) {
         value = DateFormater.formatToDateWithDetailTime((XMLGregorianCalendar) value);
      }
      return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
   }

}
