package ch.infbr5.sentinel.client.gui.components.journal.list;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.RowFilter;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import net.miginfocom.swing.MigLayout;
import ch.infbr5.sentinel.client.gui.util.SwingHelper;

public class FilterTablePanel extends JPanel {

	private static final long serialVersionUID = 1L;

	private JTable table;

	private JTextField txtFilter;

	private JButton btnReset;

	private JButton additionalButton;

	public FilterTablePanel(JTable table, JButton additionalButton) {
		this.table = table;
		this.additionalButton = additionalButton;

		final TableRowSorter<TableModel> sorter = new TableRowSorter<TableModel>(table.getModel());
	    table.setRowSorter(sorter);

		txtFilter = new JTextField();
		txtFilter.addKeyListener(new KeyListener() {

			@Override
			public void keyTyped(KeyEvent e) {
				sorter.setRowFilter(RowFilter.regexFilter("(?i)" + txtFilter.getText()));
			}

			@Override
			public void keyReleased(KeyEvent e) {

			}

			@Override
			public void keyPressed(KeyEvent e) {

			}
		});
		btnReset = new JButton("Zur�cksetzen");
		btnReset.setFont(SwingHelper.smaller(btnReset.getFont()));
		btnReset.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				txtFilter.setText("");
				sorter.setRowFilter(RowFilter.regexFilter(txtFilter.getText()));
			}
		});

		setLayout(new MigLayout());
		add(txtFilter, "growx, aligny center");
		if (additionalButton == null) {
			add(btnReset, "wrap");
		} else {
			additionalButton.setFont(SwingHelper.smaller(additionalButton.getFont()));
			add(btnReset);
			add(additionalButton, "wrap");
		}

		add(new JScrollPane(table), "spanx,push,grow");
	}

}
