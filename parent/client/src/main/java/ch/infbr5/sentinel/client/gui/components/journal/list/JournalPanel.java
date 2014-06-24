package ch.infbr5.sentinel.client.gui.components.journal.list;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;

import ch.infbr5.sentinel.client.util.ServiceHelper;
import ch.infbr5.sentinel.client.wsgen.JournalEintrag;
import ch.infbr5.sentinel.client.wsgen.JournalGefechtsMeldung;

public class JournalPanel<T extends JournalEintrag> extends JPanel {

	private static final long serialVersionUID = 1L;

	private JList<T> jList;

	private DefaultListModel<T> model;

	private JournalItemRenderer<T> renderer;

	public JournalPanel(final DefaultListModel<T> model) {
		setLayout(new BorderLayout());

		this.model = model;

		this.renderer = new JournalItemRenderer<T>();

		this.jList = new JList<T>();
		this.jList.setModel(this.model);
		this.jList.setCellRenderer(this.renderer);

		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setViewportView(this.jList);
		add(scrollPane, BorderLayout.CENTER);

		jList.addMouseListener( new MouseAdapter()
    	{
    	    @Override
			public void mousePressed(MouseEvent e)
    	    {
    	        if ( SwingUtilities.isRightMouseButton(e) )
    	        {
    	            JList list = (JList)e.getSource();
    	            final int row = list.locationToIndex(e.getPoint());

    	            JPopupMenu menu = new JPopupMenu();
	                JMenuItem item = new JMenuItem("Erledigt");
	                item.addActionListener(new ActionListener() {
	                    @Override
						public void actionPerformed(ActionEvent e) {
	                    	JournalGefechtsMeldung eintrag = (JournalGefechtsMeldung) model.get(row);
	                    	eintrag.setIstErledigt(true);
	                    	ServiceHelper.getJournalService().updateGefechtsMeldung(eintrag);
	                    	jList.repaint();
	                    }
	                });
	                menu.add(item);
	                JMenuItem item2 = new JMenuItem("Noch nicht erledigt");
	                item2.addActionListener(new ActionListener() {
	                    @Override
						public void actionPerformed(ActionEvent e) {
	                        JournalGefechtsMeldung eintrag = (JournalGefechtsMeldung) model.get(row);
	                    	eintrag.setIstErledigt(false);
	                    	ServiceHelper.getJournalService().updateGefechtsMeldung(eintrag);
	                    	jList.repaint();
	                    }
	                });
	                menu.add(item2);
	                menu.show(jList, e.getX(), e.getY());
    	        }
    	    }

    	});

	}

}
