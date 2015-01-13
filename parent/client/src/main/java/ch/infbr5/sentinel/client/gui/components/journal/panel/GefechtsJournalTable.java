package ch.infbr5.sentinel.client.gui.components.journal.panel;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableCellRenderer;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;

import org.apache.log4j.Logger;

import ch.infbr5.sentinel.client.gui.util.DateTimeCellRenderer;
import ch.infbr5.sentinel.client.gui.util.MultiLineCellRenderer;
import ch.infbr5.sentinel.client.gui.util.TableColumnResizer;
import ch.infbr5.sentinel.client.util.ServiceHelper;
import ch.infbr5.sentinel.client.wsgen.JournalGefechtsMeldung;

public class GefechtsJournalTable extends JTable {

   private static final long serialVersionUID = 1L;

   private static Logger log = Logger.getLogger(GefechtsJournalTable.class);

   private final GefechtsJournalModel model;

   public GefechtsJournalTable(final GefechtsJournalModel model) {
      super(model);
      this.model = model;

      final DefaultTableCellRenderer renderer = new DefaultTableCellRenderer();
      renderer.setVerticalAlignment(DefaultTableCellRenderer.TOP);
      setDefaultRenderer(Object.class, renderer);
      setDefaultRenderer(String.class, new MultiLineCellRenderer());
      getColumnModel().getColumn(0).setCellRenderer(new DateTimeCellRenderer());
      getColumnModel().getColumn(5).setCellRenderer(new DateTimeCellRenderer());

      addMouseListener(new MouseAdapter() {

         @Override
         public void mousePressed(final MouseEvent e) {
            if (SwingUtilities.isRightMouseButton(e)) {
               final JTable table = (JTable) e.getSource();
               final int row = table.rowAtPoint(e.getPoint());

               final JPopupMenu menu = new JPopupMenu();
               final JournalGefechtsMeldung eintrag = model.getItem(row);
               if (eintrag.isIstErledigt()) {
                  menu.add(createUnerledigtItem(row));
               } else {
                  menu.add(createErledigtItem(row));
               }

               menu.add(createRemoveItem(row));

               menu.show(table, e.getX(), e.getY());
            }
         }

      });
   }

   private JMenuItem createRemoveItem(final int row) {
      final JMenuItem item = new JMenuItem("Löschen");
      item.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(final ActionEvent e) {
            final int[] selectedRows = getSelectedRows();
            for (final int selectedRow : selectedRows) {
               System.out.println(model.getItem(selectedRow).getWerWasWoWie());
            }

            // /final JournalGefechtsMeldung eintrag = model.getItem(row);
            // ServiceHelper.getJournalService().remo
            // model.fireTableDataChanged();
         }
      });
      return item;
   }

   private JMenuItem createErledigtItem(final int row) {
      final JMenuItem item = new JMenuItem("Erledigt");
      item.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(final ActionEvent e) {
            final JournalGefechtsMeldung eintrag = model.getItem(row);
            eintrag.setIstErledigt(true);
            try {
               final GregorianCalendar c = new GregorianCalendar();
               c.setTime(new Date());
               eintrag.setZeitpunktErledigt(DatatypeFactory.newInstance().newXMLGregorianCalendar(c));
            } catch (final DatatypeConfigurationException e1) {
               log.warn(e1.getMessage());
            }
            ServiceHelper.getJournalService().updateGefechtsMeldung(eintrag);
            model.fireTableDataChanged();
         }
      });
      return item;
   }

   private JMenuItem createUnerledigtItem(final int row) {
      final JMenuItem item = new JMenuItem("Noch nicht erledigt");
      item.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(final ActionEvent e) {
            final JournalGefechtsMeldung eintrag = model.getItem(row);
            eintrag.setIstErledigt(false);
            ServiceHelper.getJournalService().updateGefechtsMeldung(eintrag);
            model.fireTableDataChanged();
         }
      });
      return item;
   }

   public void adjust() {
      this.getRowSorter().toggleSortOrder(0);
      this.getRowSorter().toggleSortOrder(0);
      TableColumnResizer.resizeColumnWidth(this);
   }

}
