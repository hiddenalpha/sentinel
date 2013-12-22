package ch.infbr5.sentinel.client.gui.components.journal;

import java.awt.BorderLayout;

import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import ch.infbr5.sentinel.client.wsgen.JournalEintragDetails;

public class JournalPanel extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JList<JournalEintragDetails> list;
	private JournalListModel model;
	private JournalItemRenderer renderer;
	
	public JournalPanel() {
		
		intiComponent();
		
	}
	
	private void intiComponent(){
		setLayout(new BorderLayout());
		
	
		list = new JList<JournalEintragDetails>();
		model = new JournalListModel();
		renderer = new JournalItemRenderer();
		
		list.setModel(model);
		list.setCellRenderer(renderer);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setViewportView(list);
		add(scrollPane, BorderLayout.CENTER);
		
	}

}
