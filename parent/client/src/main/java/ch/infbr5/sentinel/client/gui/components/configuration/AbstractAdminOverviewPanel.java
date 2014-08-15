package ch.infbr5.sentinel.client.gui.components.configuration;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.DefaultListSelectionModel;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import net.miginfocom.swing.MigLayout;
import ch.infbr5.sentinel.common.gui.table.FilterTablePanel;
import ch.infbr5.sentinel.common.gui.util.SwingHelper;

public abstract class AbstractAdminOverviewPanel<T> extends JPanel implements ActionListener {

	public static final String BUTTON_ADMINPANEL_CANCEL = "ADMINPANEL_CANCEL";

	public static final String BUTTON_ADMINPANEL_DELETE = "ADMINPANEL_DELETE";

	public static final String BUTTON_ADMINPANEL_SAVE = "ADMINPANEL_SAVE";

	public static final String BUTTON_ADMINPANEL_EDIT = "ADMINPANEL_EDIT";

	public static final String BUTTON_ADMINPANEL_NEW = "ADMINPANEL_NEW";

	private static final long serialVersionUID = 1L;

	private AbstractAdminTableModel<T> model;

	private FilterTablePanel tablePanel;

	private AbstractAdminDetailPanel<T> detailPanel;

	private JPanel buttonPanel;

	private JTable table;

	private JButton saveButton;
	private JButton newButton;
	private JButton editButton;
	private JButton deleteButton;
	private JButton cancelButton;

	private boolean editMode;

	private int lastSelectedRow;

	private boolean isAdminMode;

	public AbstractAdminOverviewPanel(boolean adminMode) {
		isAdminMode = adminMode;
		initComponents();
	}

	public void updateModel() {
		model.updateData();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand().equals(BUTTON_ADMINPANEL_SAVE)) {
			boolean isValid = true;
			// TODO
			// validieren
			for (Component c : detailPanel.getComponents()) {
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
				model.updateDataRecord(detailPanel.getDataRecord());
				setEditable(false);
				detailPanel.setDataRecord(null);
			} else {
				JOptionPane.showMessageDialog(null, "Die eingegebenen Daten sind nicht g�ltig.", "Validierung",
						JOptionPane.CANCEL_OPTION);
			}
		} else if (e.getActionCommand().equals(BUTTON_ADMINPANEL_NEW)) {
			detailPanel.setDataRecord(model.getNewDataRecord());
			setEditable(true);
		} else if (e.getActionCommand().equals(BUTTON_ADMINPANEL_CANCEL)) {
			setEditable(false);
			if (table.getSelectedRow() >= 0) {
				int modelRow = table.convertRowIndexToModel(table.getSelectedRow());
				detailPanel.setDataRecord(model.getDataRecord(modelRow));
			} else {
				detailPanel.clearFieldValues();
			}
		} else if (e.getActionCommand().equals(BUTTON_ADMINPANEL_DELETE)) {
			if (isRowSelected()) {
				int result = JOptionPane.showConfirmDialog(null, "M�chten Sie den Eintrag wirklich l�schen?", "Best�tigung", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
				if (result == JOptionPane.YES_OPTION) {
					model.removeDataRecord(detailPanel.getDataRecord());
					setEditable(false);
					detailPanel.setDataRecord(null);
				}
			}
		} else if (e.getActionCommand().equals(BUTTON_ADMINPANEL_EDIT)) {
			if (isRowSelected()) {
				int modelRow = table.convertRowIndexToModel(table.getSelectedRow());
				detailPanel.setDataRecord(model.getDataRecord(modelRow));
				setEditable(true);
			}
		}
	}

	private void initComponents() {
		model = getTableModel();
		detailPanel = getDetailPanel();

		createAndIntitializeTable();
		createButtons();

		SwingHelper.attachLabledBorder("Details", detailPanel);

		// Right Panel
		JPanel rightPanel = new JPanel(new MigLayout("", "[fill, grow]", "[fill, grow][]"));
		rightPanel.add(detailPanel, "wrap");
		rightPanel.add(buttonPanel);

		// Main - Layout
		setLayout(new MigLayout("", "[fill, grow]", "[fill, grow]"));
		JSplitPane splitpane = new JSplitPane();
		splitpane.setLeftComponent(tablePanel);
		splitpane.setRightComponent(rightPanel);
		//splitpane.setOneTouchExpandable(true);
		splitpane.setResizeWeight(1.0d); // erst nach pack aufrufen eigentlich...
		splitpane.setDividerLocation(0.75d); // erst nach pack aufrufen eigentlich...
		add(splitpane);

		setEditable(false);
	}


	protected AbstractAdminDetailPanel<T> getInstalledDetailPanel() {
		return detailPanel;
	}

	private void createAndIntitializeTable() {
		DefaultListSelectionModel selectionModel = new DefaultListSelectionModel();
		selectionModel.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		selectionModel.addListSelectionListener(createSelectionListener());
		table = new JTable(model);
		table.setSelectionModel(selectionModel);
		table.setRowSelectionAllowed(true);
		tablePanel = new FilterTablePanel(table, null);
	}

	private ListSelectionListener createSelectionListener() {
		return new ListSelectionListener() {

			@Override
			public void valueChanged(ListSelectionEvent e) {
				if (!e.getValueIsAdjusting() && isRowSelected()) {
					if (isEditable()) {
						if (lastSelectedRow != table.getSelectedRow()) {
							table.setRowSelectionInterval(lastSelectedRow, lastSelectedRow);
						}
					} else {
						lastSelectedRow = table.getSelectedRow();
						int modelRow = table.convertRowIndexToModel(lastSelectedRow);
						detailPanel.setDataRecord(model.getDataRecord(modelRow));

						editButton.setEnabled(isRowSelected());
						deleteButton.setEnabled(isRowSelected());
					}
				} else {
					editButton.setEnabled(isRowSelected());
					deleteButton.setEnabled(isRowSelected());
				}
			}
		};
	}

	private boolean isRowSelected() {
		return (table.getSelectedRow() > -1);
	}

	private boolean isEditable() {
		return editMode;
	}

	private void setEditable(boolean mode) {
		editMode = mode;
		detailPanel.setEditable(mode);

		table.setEnabled(!mode);

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

		if (!isRowSelected()) {
			editButton.setEnabled(false);
			deleteButton.setEnabled(false);
		}
	}

	private void createButtons() {
		buttonPanel = new JPanel(new MigLayout());

		newButton = new JButton("Neu");
		newButton.setName(BUTTON_ADMINPANEL_NEW);
		newButton.addActionListener(this);
		newButton.setActionCommand(BUTTON_ADMINPANEL_NEW);
		buttonPanel.add(newButton);

		editButton = new JButton("Bearbeiten");
		editButton.setName(BUTTON_ADMINPANEL_EDIT);
		editButton.addActionListener(this);
		editButton.setActionCommand(BUTTON_ADMINPANEL_EDIT);
		buttonPanel.add(editButton);

		saveButton = new JButton("Speichern");
		saveButton.setName(BUTTON_ADMINPANEL_SAVE);
		saveButton.addActionListener(this);
		saveButton.setActionCommand(BUTTON_ADMINPANEL_SAVE);
		buttonPanel.add(saveButton);

		deleteButton = new JButton("L�schen");
		deleteButton.setName(BUTTON_ADMINPANEL_DELETE);
		deleteButton.addActionListener(this);
		deleteButton.setActionCommand(BUTTON_ADMINPANEL_DELETE);
		buttonPanel.add(deleteButton);

		cancelButton = new JButton("Abbrechen");
		cancelButton.setName(BUTTON_ADMINPANEL_CANCEL);
		cancelButton.addActionListener(this);
		cancelButton.setActionCommand(BUTTON_ADMINPANEL_CANCEL);
		buttonPanel.add(cancelButton);
	}

	protected abstract AbstractAdminTableModel<T> getTableModel();

	protected abstract AbstractAdminDetailPanel<T> getDetailPanel();
}