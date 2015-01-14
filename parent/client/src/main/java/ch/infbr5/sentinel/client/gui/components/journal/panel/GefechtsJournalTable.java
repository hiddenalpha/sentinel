package ch.infbr5.sentinel.client.gui.components.journal.panel;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import javax.swing.JMenuItem;
import javax.swing.table.DefaultTableCellRenderer;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;

import org.apache.log4j.Logger;

import ch.infbr5.sentinel.client.gui.util.DateTimeCellRenderer;
import ch.infbr5.sentinel.client.gui.util.MultiLineCellRenderer;
import ch.infbr5.sentinel.client.gui.util.TableColumnResizer;
import ch.infbr5.sentinel.client.util.ServiceHelper;
import ch.infbr5.sentinel.client.wsgen.JournalEintrag;
import ch.infbr5.sentinel.client.wsgen.JournalGefechtsMeldung;

public class GefechtsJournalTable extends AbstractJournalTable {

   private static final long serialVersionUID = 1L;

   private static Logger log = Logger.getLogger(GefechtsJournalTable.class);

   private final GefechtsJournalModel model;

   public GefechtsJournalTable(final GefechtsJournalModel model, final boolean adminMode) {
      super(model, adminMode);
      this.model = model;

      final DefaultTableCellRenderer renderer = new DefaultTableCellRenderer();
      renderer.setVerticalAlignment(DefaultTableCellRenderer.TOP);
      setDefaultRenderer(Object.class, renderer);
      getColumnModel().getColumn(0).setCellRenderer(new DateTimeCellRenderer());
      getColumnModel().getColumn(2).setCellRenderer(new MultiLineCellRenderer());
      getColumnModel().getColumn(4).setCellRenderer(new DateTimeCellRenderer());
   }

   @Override
   protected List<JMenuItem> getContextMenuItems(final JournalEintrag eintrag) {
      final List<JMenuItem> items = new ArrayList<>();

      JournalGefechtsMeldung meldung;
      if (eintrag instanceof JournalGefechtsMeldung) {
         meldung = (JournalGefechtsMeldung) eintrag;
      } else {
         return items;
      }

      if (meldung.isIstErledigt()) {
         items.add(createUnerledigtItem(meldung));
      } else {
         items.add(createErledigtItem(meldung));
      }
      return items;
   }

   private JMenuItem createErledigtItem(final JournalGefechtsMeldung meldung) {
      final JMenuItem item = new JMenuItem("Erledigt");
      item.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(final ActionEvent e) {
            meldung.setIstErledigt(true);
            try {
               final GregorianCalendar c = new GregorianCalendar();
               c.setTime(new Date());
               meldung.setZeitpunktErledigt(DatatypeFactory.newInstance().newXMLGregorianCalendar(c));
            } catch (final DatatypeConfigurationException e1) {
               log.warn(e1.getMessage());
            }
            ServiceHelper.getJournalService().updateGefechtsMeldung(meldung);
            model.fireTableDataChanged();
         }
      });
      return item;
   }

   private JMenuItem createUnerledigtItem(final JournalGefechtsMeldung meldung) {
      final JMenuItem item = new JMenuItem("Noch nicht erledigt");
      item.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(final ActionEvent e) {
            meldung.setIstErledigt(false);
            ServiceHelper.getJournalService().updateGefechtsMeldung(meldung);
            model.fireTableDataChanged();
         }
      });
      return item;
   }

   public void adjust() {
      setDefaultSort(0, false);
      TableColumnResizer.resizeColumnWidth(this);
      columnModel.getColumn(2).setPreferredWidth(250);
   }

}
