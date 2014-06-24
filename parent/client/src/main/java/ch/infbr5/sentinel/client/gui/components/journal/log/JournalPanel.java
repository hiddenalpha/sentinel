package ch.infbr5.sentinel.client.gui.components.journal.log;

import java.awt.BorderLayout;

import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import ch.infbr5.sentinel.client.wsgen.JournalEintrag;

public class JournalPanel<T extends JournalEintrag> extends JPanel {

	private static final long serialVersionUID = 1L;

	private JList<T> jList;

	private DefaultListModel<T> model;

	private JournalItemRenderer<T> renderer;

	public JournalPanel(DefaultListModel<T> model) {
		setLayout(new BorderLayout());

		this.model = model;

		this.renderer = new JournalItemRenderer<T>();

		this.jList = new JList<T>();
		this.jList.setModel(this.model);
		this.jList.setCellRenderer(this.renderer);

		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setViewportView(this.jList);
		add(scrollPane, BorderLayout.CENTER);
	}

}
