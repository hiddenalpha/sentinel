package ch.infbr5.sentinel.client.gui.components.configuration;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.DefaultListSelectionModel;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.RowFilter;
import javax.swing.RowSorter.SortKey;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableRowSorter;

import net.miginfocom.swing.MigLayout;

public abstract class AbstractAdminOverviewPanel<T> extends JPanel implements
		ListSelectionListener, ActionListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static String FILTER_CHANGE_EVENT_NAME = "filterUpdate";
	private static int TEXTFIELD_SPACE = 5;

	private JTable table;

	private AbstractAdminTableModel<T> model;

	private JScrollPane scroll;

	private AbstractAdminDetailPanel<T> details;

	private JButton saveButton;
	private JButton newButton;
	private JButton editButton;
	private JButton deleteButton;
	private JButton cancelButton;

	private boolean editMode;

	private int lastSelectedRow;

	private TableRowSorter<AbstractAdminTableModel<T>> sorter;

	private JPanel leftPanel;

	private ArrayList<ColumnSearcher<T>> columnSearchers;

	private boolean isAdminMode;

	public AbstractAdminOverviewPanel(boolean adminMode) {
		isAdminMode = adminMode;
		initComponents();
	}

	public void updateModel() {
		model.updateData();
	}

	public void clearAndHideFilter() {
		clearFilters();

		sorter.setRowFilter(null);
	}

	@Override
	public void valueChanged(ListSelectionEvent e) {
		if (!e.getValueIsAdjusting() && isRowSelected()) {
			if (isEditable()) {
				if (lastSelectedRow != table.getSelectedRow()) {
					table.setRowSelectionInterval(lastSelectedRow,
							lastSelectedRow);
				}
			} else {
				lastSelectedRow = table.getSelectedRow();
				int modelRow = table.convertRowIndexToModel(lastSelectedRow);

				details.setDataRecord(model.getDataRecord(modelRow));
			}
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand().equals("ADMINPANEL_SAVE")) {
			boolean isValid = true;
			// TODO
			// validieren
			for (Component c : details.getComponents()) {
				if (c instanceof JTextField) {
					JTextField textfield = (JTextField) c;
					Border b = textfield.getBorder();
					if (b instanceof LineBorder) {
						if (((LineBorder) b).getLineColor().equals(Color.RED)) {
							isValid = false;
						}
					}
				}
			}
			if (isValid) {
				model.updateDataRecord(details.getDataRecord());
				setEditable(false);
				details.setDataRecord(null);
			} else {
				JOptionPane.showMessageDialog(null,
						"Die eingegebenen Daten sind nicht gültig.",
						"Validierung", JOptionPane.CANCEL_OPTION);
			}
		} else if (e.getActionCommand().equals("ADMINPANEL_NEW")) {
			details.setDataRecord(model.getNewDataRecord());
			setEditable(true);
		} else if (e.getActionCommand().equals("ADMINPANEL_CANCEL")) {
			setEditable(false);
			if (table.getSelectedRow() >= 0) {
				int modelRow = table.convertRowIndexToModel(table
						.getSelectedRow());
				details.setDataRecord(model.getDataRecord(modelRow));
			} else {
				details.clearFieldValues();
			}
		} else if (e.getActionCommand().equals("ADMINPANEL_DELETE")) {
			if (isRowSelected()) {
				model.removeDataRecord(details.getDataRecord());
				setEditable(false);
				details.setDataRecord(null);
			}
		} else if (e.getActionCommand().equals("ADMINPANEL_EDIT")) {
			if (isRowSelected()) {
				int modelRow = table.convertRowIndexToModel(table
						.getSelectedRow());
				details.setDataRecord(model.getDataRecord(modelRow));
				setEditable(true);
			}
		}
	}

	private void initComponents() {
		setLayout(new MigLayout("", "[fill, grow][fill, grow]",
				"[fill, grow][]"));

		model = getTableModel();
		createAndIntitializeTable();

		scroll = new JScrollPane();
		scroll.setViewportView(table);

		leftPanel = new JPanel();
		leftPanel.setLayout(new MigLayout("", "[fill, grow]",
				"[fill, grow][grow][fill, grow]"));
		add(leftPanel, "spany, top");
		leftPanel.add(scroll, "wrap");

		JPanel filterPanel = new JPanel();
		leftPanel.add(filterPanel, "growx");

		details = getDetailPanel();
		details.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createTitledBorder("Details"),
				BorderFactory.createEmptyBorder(5, 5, 5, 5)));
		add(details, "spanx, wrap");

		createButtons();

		setEditable(false);

		buildColumnSearchers(filterPanel);

		sorter.setRowFilter(createRowFilter());
	}

	private void createAndIntitializeTable() {
		DefaultListSelectionModel selectionModel = new DefaultListSelectionModel();
		selectionModel.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		selectionModel.addListSelectionListener(this);

		table = new JTable(model);
		table.setSelectionModel(selectionModel);
		table.setRowSelectionAllowed(true);
		table.setAutoCreateRowSorter(true);
		sorter = new TableRowSorter<AbstractAdminTableModel<T>>(model);
		table.setRowSorter(sorter);
		addKeyListenerToTable();
		addComponentListenerToTable();

		PropertyChangeListener filterPropertyChangeListener = createFilterPropertyChangeListener();
		table.addPropertyChangeListener(filterPropertyChangeListener);
	}

	private void addKeyListenerToTable() {
		table.addKeyListener(new KeyAdapter() {
			public void keyReleased(KeyEvent e) {
				showFilterBoxAndAddKey(e.getKeyCode(), e.getKeyChar());
			}
		});
	}

	private void addComponentListenerToTable() {
		table.addComponentListener(new ComponentListener() {
			@Override
			public void componentShown(ComponentEvent e) {
			}

			@Override
			public void componentResized(ComponentEvent e) {
				resizeFilterFields();
			}

			@Override
			public void componentMoved(ComponentEvent e) {
			}

			@Override
			public void componentHidden(ComponentEvent e) {
			}
		});
	}

	private void buildColumnSearchers(JPanel filterPanel) {
		columnSearchers = new ArrayList<ColumnSearcher<T>>();

		for (int i = 0; i < table.getColumnCount(); ++i) {
			JTextField filterField = new JTextField() {
				/**
				 * 
				 */
				private static final long serialVersionUID = 1L;

				@Override
				public void setBorder(Border border) {
					// No border
				}
			};

			filterField.addKeyListener(new KeyAdapter() {
				public void keyReleased(KeyEvent e) {
					if (isAbortOnSpecialKey(e.getKeyCode())) {
						clearAndHideFilter();
						return;
					}
					// this.firePropertyChange is compiling, but not working.
					// it has to be fired on a class object
					table.firePropertyChange(FILTER_CHANGE_EVENT_NAME, false,
							true);
				}
			});

			filterPanel.add(filterField);
			ColumnSearcher<T> columnSearcher = new ColumnSearcher<T>(i,
					filterField);
			columnSearchers.add(columnSearcher);
		}
	}

	private PropertyChangeListener createFilterPropertyChangeListener() {
		// Define PropertyChangeListener
		PropertyChangeListener propertyChangeListener = new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent propertyChangeEvent) {
				String propertyName = propertyChangeEvent.getPropertyName();
				if (propertyName.equals(FILTER_CHANGE_EVENT_NAME)) {
					sorter.setRowFilter(createRowFilter());
				}
			}
		};

		return propertyChangeListener;
	}

	private void resizeFilterFields() {
		for (int i = 0; i < columnSearchers.size(); ++i) {
			ColumnSearcher<T> searcher = columnSearchers.get(i);

			Dimension dimension = new Dimension(table.getColumn(
					model.getColumnName(i)).getWidth()
					- TEXTFIELD_SPACE, 20);
			searcher.getTextField().setPreferredSize(dimension);
			searcher.getTextField().setSize(dimension);
			searcher.getTextField().setMargin(new Insets(0, 0, 0, 0));
		}
	}

	private RowFilter<AbstractAdminTableModel<T>, Object> createRowFilter() {
		RowFilter<AbstractAdminTableModel<T>, Object> ret;
		List<RowFilter<AbstractAdminTableModel<T>, Object>> filters = new ArrayList<RowFilter<AbstractAdminTableModel<T>, Object>>(
				columnSearchers.size());

		for (ColumnSearcher<T> cs : columnSearchers) {
			RowFilter<AbstractAdminTableModel<T>, Object> filter = cs
					.createFilter();
			if (filter != null) {
				filters.add(filter);
			}
		}

		if (filters.isEmpty()) {
			ret = null;
		} else {
			ret = RowFilter.andFilter(filters);
		}

		return ret;
	}

	private void showFilterBoxAndAddKey(int keyCode, char keyChar) {
		if (isAbortOnSpecialKey(keyCode)) {
			clearAndHideFilter();

			return;
		}

		int sortedColumn = getSortedColumn();

		JTextField filterTextField = columnSearchers.get(sortedColumn)
				.getTextField();
		filterTextField.setText("" + keyChar);
		filterTextField.grabFocus();
	}

	private boolean isAbortOnSpecialKey(int keyCode) {
		if (keyCode == KeyEvent.VK_ESCAPE
				|| (keyCode >= KeyEvent.VK_F1 && keyCode <= KeyEvent.VK_F19)) {
			return true;
		} else {
			return false;
		}
	}

	private int getSortedColumn() {
		List<? extends SortKey> sortKeys = sorter.getSortKeys();
		int sortedColumn = 0;
		if (!sortKeys.isEmpty()) {
			sortedColumn = sortKeys.get(0).getColumn();
		}

		return sortedColumn;
	}

	private void clearFilters() {
		for (ColumnSearcher<T> columnSearcher : columnSearchers) {
			columnSearcher.getTextField().setText("");
		}
	}

	private boolean isRowSelected() {
		return (table.getSelectedRow() > -1);
	}

	private boolean isEditable() {
		return editMode;
	}

	private void setEditable(boolean mode) {
		editMode = mode;
		details.setEditable(mode);

		if (mode) {
			editButton.setEnabled(false);
			newButton.setEnabled(false);
			deleteButton.setEnabled(false);
			saveButton.setEnabled(true);
			cancelButton.setEnabled(true);
		} else {
			editButton.setEnabled(true);
			newButton.setEnabled(true);
			if (isAdminMode) {
				deleteButton.setEnabled(true);
			} else {
				deleteButton.setEnabled(false);
			}
			saveButton.setEnabled(false);
			cancelButton.setEnabled(false);
		}
	}

	private void createButtons() {
		newButton = new JButton("Neu");
		newButton.addActionListener(this);
		newButton.setActionCommand("ADMINPANEL_NEW");
		add(newButton);

		editButton = new JButton("Bearbeiten");
		editButton.addActionListener(this);
		editButton.setActionCommand("ADMINPANEL_EDIT");
		add(editButton);

		saveButton = new JButton("Speichern");
		saveButton.addActionListener(this);
		saveButton.setActionCommand("ADMINPANEL_SAVE");
		add(saveButton);

		deleteButton = new JButton("Löschen");
		deleteButton.addActionListener(this);
		deleteButton.setActionCommand("ADMINPANEL_DELETE");
		add(deleteButton);

		cancelButton = new JButton("Abbrechen");
		cancelButton.addActionListener(this);
		cancelButton.setActionCommand("ADMINPANEL_CANCEL");
		add(cancelButton);
	}

	protected abstract AbstractAdminTableModel<T> getTableModel();

	protected abstract AbstractAdminDetailPanel<T> getDetailPanel();
}