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

import ch.infbr5.sentinel.client.gui.util.MultiLineCellRenderer;
import ch.infbr5.sentinel.client.util.ServiceHelper;
import ch.infbr5.sentinel.client.wsgen.JournalGefechtsMeldung;

public class GefechtsJournalTable extends JTable {

	private static final long serialVersionUID = 1L;

	private static Logger log = Logger.getLogger(GefechtsJournalTable.class);

	private GefechtsJournalModel model;

	public GefechtsJournalTable(final GefechtsJournalModel model) {
		super(model);
		this.model = model;
		setAutoCreateRowSorter(true);
		getRowSorter().toggleSortOrder(0);
		getRowSorter().toggleSortOrder(0);
		//tableGefecht.setAutoResizeMode( JTable.AUTO_RESIZE_OFF );

		DefaultTableCellRenderer renderer = new DefaultTableCellRenderer();
		renderer.setVerticalAlignment(DefaultTableCellRenderer.TOP);
		setDefaultRenderer(Object.class, renderer);
		setDefaultRenderer(String.class, new MultiLineCellRenderer());
		adjustColumnWidthGefecht();

		addMouseListener(new MouseAdapter() {

			@Override
			public void mousePressed(MouseEvent e) {
				if (SwingUtilities.isRightMouseButton(e)) {
					JTable table = (JTable) e.getSource();
					int row = table.rowAtPoint(e.getPoint());

					JPopupMenu menu = new JPopupMenu();
					JournalGefechtsMeldung eintrag = model.getItem(row);
					if (eintrag.isIstErledigt()) {
						menu.add(createUnerledigtItem(row));
					} else {
						menu.add(createErledigtItem(row));
					}
					menu.show(table, e.getX(), e.getY());
				}
			}

		});
	}

	private JMenuItem createErledigtItem(final int row) {
		JMenuItem item = new JMenuItem("Erledigt");
		item.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JournalGefechtsMeldung eintrag = model.getItem(row);
				eintrag.setIstErledigt(true);
				try {
					GregorianCalendar c = new GregorianCalendar();
					c.setTime(new Date());
					eintrag.setZeitpunktErledigt(DatatypeFactory.newInstance().newXMLGregorianCalendar(c));
				} catch (DatatypeConfigurationException e1) {
					log.warn(e1.getMessage());
				}
				ServiceHelper.getJournalService().updateGefechtsMeldung(eintrag);
				model.fireTableDataChanged();
			}
		});
		return item;
	}

	private JMenuItem createUnerledigtItem(final int row) {
		JMenuItem item = new JMenuItem("Noch nicht erledigt");
		item.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JournalGefechtsMeldung eintrag = model.getItem(row);
				eintrag.setIstErledigt(false);
				ServiceHelper.getJournalService().updateGefechtsMeldung(eintrag);
				model.fireTableDataChanged();
			}
		});
		return item;
	}

	private void adjustColumnWidthGefecht() {
		getColumnModel().getColumn(0).setWidth(70);
		getColumnModel().getColumn(0).setPreferredWidth(70);
		getColumnModel().getColumn(0).setMaxWidth(70);

		getColumnModel().getColumn(1).setPreferredWidth(100);
		getColumnModel().getColumn(1).setMaxWidth(100);

		getColumnModel().getColumn(4).setPreferredWidth(100);
		getColumnModel().getColumn(4).setMaxWidth(100);

		getColumnModel().getColumn(5).setWidth(70);
		getColumnModel().getColumn(5).setPreferredWidth(70);
		getColumnModel().getColumn(5).setMaxWidth(70);
	}

}
