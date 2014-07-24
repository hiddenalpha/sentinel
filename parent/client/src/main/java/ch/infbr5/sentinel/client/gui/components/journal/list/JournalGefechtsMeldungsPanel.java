package ch.infbr5.sentinel.client.gui.components.journal.list;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.logging.Logger;

import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;

import ch.infbr5.sentinel.client.util.ServiceHelper;
import ch.infbr5.sentinel.client.wsgen.JournalEintrag;
import ch.infbr5.sentinel.client.wsgen.JournalGefechtsMeldung;

public class JournalGefechtsMeldungsPanel<T extends JournalEintrag> extends JPanel {

	private static final long serialVersionUID = 1L;

	private static Logger log = Logger.getLogger(JournalGefechtsMeldungsPanel.class.getName());

	private JList<T> jList;

	private DefaultListModel<T> model;

	public JournalGefechtsMeldungsPanel(final DefaultListModel<T> model) {
		setLayout(new BorderLayout());

		this.model = model;

		createList();

		jList.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				if (SwingUtilities.isRightMouseButton(e)) {
					JList<T> list = (JList<T>) e.getSource();
					final int row = list.locationToIndex(e.getPoint());
					JPopupMenu menu = new JPopupMenu();
					menu.add(createErledigtItem(row));
					menu.add(createUnerledigtItem(row));
					menu.show(jList, e.getX(), e.getY());
				}
			}
		});
	}

	private void createList() {
		this.jList = new JList<T>();
		this.jList.setModel(this.model);
		this.jList.setCellRenderer(new ListCellJournalGefechtsMeldungRenderer());

		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setViewportView(this.jList);
		add(scrollPane, BorderLayout.CENTER);
	}

	private JMenuItem createErledigtItem(final int row) {
		JMenuItem item = new JMenuItem("Erledigt");
		item.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JournalGefechtsMeldung eintrag = (JournalGefechtsMeldung) model.get(row);
				eintrag.setIstErledigt(true);
				try {
					GregorianCalendar c = new GregorianCalendar();
					c.setTime(new Date());
					eintrag.setZeitpunktErledigt(DatatypeFactory.newInstance().newXMLGregorianCalendar(c));
				} catch (DatatypeConfigurationException e1) {
					log.warning(e1.getMessage());
				}
				ServiceHelper.getJournalService().updateGefechtsMeldung(eintrag);
				jList.repaint();
			}
		});
		return item;
	}

	private JMenuItem createUnerledigtItem(final int row) {
		JMenuItem item = new JMenuItem("Noch nicht erledigt");
		item.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JournalGefechtsMeldung eintrag = (JournalGefechtsMeldung) model.get(row);
				eintrag.setIstErledigt(false);
				ServiceHelper.getJournalService().updateGefechtsMeldung(eintrag);
				jList.repaint();
			}
		});
		return item;
	}

}
