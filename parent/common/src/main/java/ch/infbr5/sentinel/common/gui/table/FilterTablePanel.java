package ch.infbr5.sentinel.common.gui.table;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.RowFilter;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import net.miginfocom.swing.MigLayout;
import ch.infbr5.sentinel.common.gui.util.SwingHelper;

public class FilterTablePanel extends JPanel {

	private static final long serialVersionUID = 1L;

	private JTextField txtFilter;

	private JButton btnFilter;

	private JButton btnReset;
	
	private final TableRowSorter<TableModel> sorter;

	public FilterTablePanel(JTable table, JButton additionalButton) {

		sorter = new TableRowSorter<TableModel>(table.getModel());
	    table.setRowSorter(sorter);

		txtFilter = new JTextField();
		txtFilter.addKeyListener(new KeyListener() {

			@Override
			public void keyTyped(KeyEvent e) {

			}

			@Override
			public void keyReleased(KeyEvent e) {

			}

			@Override
			public void keyPressed(KeyEvent e) {
				int key = e.getKeyCode();
                if (key == KeyEvent.VK_ENTER) {
                	applyFilter();
                }

			}
		});

		btnFilter = new JButton("Filter");
		btnFilter.setFont(SwingHelper.smaller(btnFilter.getFont()));
		btnFilter.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				applyFilter();
			}
		});

		btnReset = new JButton("Zurücksetzen");
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
			add(btnFilter);
			add(btnReset, "wrap");
		} else {
			additionalButton.setFont(SwingHelper.smaller(additionalButton.getFont()));
			add(btnFilter);
			add(btnReset);
			add(additionalButton, "wrap");
		}

		add(new JScrollPane(table), "spanx,push,grow");
	}
	
	private void applyFilter() {
		txtFilter.setText(txtFilter.getText().trim());
		
		String search = txtFilter.getText();
    	String[] searches = search.split(" ");
    	
    	List<RowFilter<Object,Object>> filters = new ArrayList<RowFilter<Object,Object>>();
    	for (String s : searches) {
    		// (?i) => case insenstive!
    		filters.add(RowFilter.regexFilter("(?i)" + s));
    	}
   	   	
    	RowFilter<Object,Object> filter = RowFilter.andFilter(filters);
    	sorter.setRowFilter(filter);
	}

}
