package ch.infbr5.sentinel.client.gui.components.journal.list;

import java.awt.BorderLayout;

import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import ch.infbr5.sentinel.client.wsgen.JournalBewegungsMeldung;

public class JournalBewegungsMeldungsPanel<T extends JournalBewegungsMeldung> extends JPanel {

	private static final long serialVersionUID = 1L;

	private JList<T> jList;

	private DefaultListModel<T> model;

	public JournalBewegungsMeldungsPanel(final DefaultListModel<T> model) {
		setLayout(new BorderLayout());

		this.model = model;

		createList();
	}

	private void createList() {
		this.jList = new JList<T>();
		this.jList.setModel(this.model);
		this.jList.setCellRenderer(new ListCellJournalBewegungsMeldungRenderer());

		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setViewportView(this.jList);
		add(scrollPane, BorderLayout.CENTER);
	}



}
