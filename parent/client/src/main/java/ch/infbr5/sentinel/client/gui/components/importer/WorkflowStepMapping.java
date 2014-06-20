package ch.infbr5.sentinel.client.gui.components.importer;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import net.miginfocom.swing.MigLayout;
import ch.infbr5.sentinel.client.util.ServiceHelper;
import ch.infbr5.sentinel.client.wsgen.ColumnMappingResponse;
import ch.infbr5.sentinel.client.wsgen.PersonenAttribute;
import ch.infbr5.sentinel.client.wsgen.PersonenImportColumn;
import ch.infbr5.sentinel.client.wsgen.PersonenImportColumnMapping;
import ch.infbr5.sentinel.client.wsgen.PersonenImportColumnMappingArray;

public class WorkflowStepMapping extends WorkflowStep {

	private ColumnMappingResponse response;
	
	private String fileHasMinimalRequirements;
	
	private JLabel lblHeader1;
	
	private JLabel lblHeader2;
	
	private List<JLabel> lblsPersonenAttribute = new ArrayList<>();
	
	private List<JComboBox<CmbItem>> cmbsColumns = new ArrayList<>();
	
	private List<CmbItem> cmbItems = new ArrayList<>();
	
	private CmbItem emptyCmbItem = new CmbItem();
	
	public WorkflowStepMapping(Frame parent, WorkflowData data, WorkflowInterceptor interceptor) {
		super(parent, data, interceptor);
	}

	@Override
	public String getName() {
		return "Spaltenzuordnung";
	}
	
	@Override
	public String getUserInfo() {
		return "Sie können nun die Spalten den Personenattributen zuordnen. Es wird eine mögliche Zu"
				+ "ordnung vorgegeben. Es ist nicht erlaubt keine Zuordnung zu definieren oder die selbe Spalte für mehrere Attribute zu verwenden.";
	}

	@Override
	public JPanel getPanel() {
		JPanel panel = new JPanel(new MigLayout());
		if (fileHasMinimalRequirements == null) {
			lblsPersonenAttribute.clear();
			cmbsColumns.clear();
			cmbItems.clear();
			
			createLabels();
			createDropdowns();
			
			panel.add(lblHeader1, "cell 0 0");
			panel.add(lblHeader2, "cell 1 0");
			
			lblHeader1.setPreferredSize(new Dimension(180, 20));
			
			for (int i = 0; i < lblsPersonenAttribute.size(); i++) {
				panel.add(lblsPersonenAttribute.get(i), "cell 0 " + (i+1));
			}
			for (int i = 0; i < cmbsColumns.size(); i++) {
				panel.add(cmbsColumns.get(i), "cell 1 " + (i+1));
			}
			
			checkEmptySelection();
			checkDoubleSelection(cmbsColumns.get(0));
		} else {
			panel.add(new JLabel(fileHasMinimalRequirements));
		}
			
		return panel;
	}

	@Override
	public void init() {
		fileHasMinimalRequirements = ServiceHelper.getPersonenImporterService().fileHasMinimalRequirements(getData().getSessionKey());
		if ("".equals(fileHasMinimalRequirements)) {
			fileHasMinimalRequirements = null;
		}
		if (fileHasMinimalRequirements == null) {
			response = ServiceHelper.getPersonenImporterService().getColumnMappings(getData().getSessionKey());
			getInterceptor().activateNext();
		}
	}

	@Override
	public void finishReturn() {
		finishNext();
	}
	
	@Override
	public void abort() {
		if (getData().getSessionKey() != null) {
			try {
				ServiceHelper.getPersonenImporterService().abortImport(getData().getSessionKey());
			} catch (RuntimeException e) {
				e.printStackTrace();
			}
		}
	}
	
	@Override
	public void finishNext() {
		PersonenImportColumnMappingArray array = new PersonenImportColumnMappingArray();
		for (JComboBox<CmbItem> cmb : cmbsColumns) {
			for (PersonenImportColumnMapping mapping : response.getMappings()) {
				PersonenAttribute attribute = PersonenAttribute.fromValue(cmb.getActionCommand());
				if (attribute.equals(mapping.getPersonenAttribute())) {
					CmbItem cmbItem = (CmbItem) cmb.getSelectedItem();
					mapping.setColumn(cmbItem.column);
					array.getItem().add(mapping);
				}
			}
		}
		ServiceHelper.getPersonenImporterService().setColumnMappings(getData().getSessionKey(), array);
	}
	
	private void createLabels() {
		lblHeader1 = new JLabel("Personenattribute");
		lblHeader2 = new JLabel("Spaltenüberschriften");
		changeToBold(lblHeader1);
		changeToBold(lblHeader2);
	}
	
	private void createDropdowns() {
		for (PersonenImportColumnMapping mapping : response.getMappings()) {
			lblsPersonenAttribute.add(new JLabel(mapping.getPersonenAttribute().name()));
			
			JComboBox<CmbItem> cmb = new JComboBox<>();
			cmb.setActionCommand(mapping.getPersonenAttribute().value()); // Set the name of the person attribute associated with this combobox
			
			ComboBoxModel<CmbItem> model = createComboBoxModel(mapping.getColumn(), mapping.getPossibleColumns());
			cmb.setModel(model);
			
			cmb.addItemListener(new ItemListener() {
				@Override
				public void itemStateChanged(ItemEvent evt) {
					checkEmptySelection();
					checkDoubleSelection((JComboBox<CmbItem>) evt.getSource());
				}
			});

			cmbsColumns.add(cmb);
		}
	}
	
	private void checkEmptySelection() {
		boolean emptySelected = false;
		for (JComboBox<CmbItem> box : cmbsColumns) {
    		if (box.getSelectedItem() == emptyCmbItem) {
    			emptySelected = true;
    			getInterceptor().deactivateNext();
    		}
    	}
		if (!emptySelected) {
			getInterceptor().activateNext();
		}
	}
	
	private void checkDoubleSelection(JComboBox<CmbItem> sourceBox) {
		for (JComboBox<CmbItem> box : cmbsColumns) {
			if (box != sourceBox) {
				if (box.getSelectedItem() == sourceBox.getSelectedItem() && box.getSelectedItem() != emptyCmbItem) {
					box.setSelectedItem(emptyCmbItem);
    			}
			}
    	}
	}

	private ComboBoxModel<CmbItem> createComboBoxModel(PersonenImportColumn selectedColumn, List<PersonenImportColumn> possibleColumns) {
		CmbItem items[] = new CmbItem[1 + possibleColumns.size()];
		items[0] = emptyCmbItem;
		int i = 1;
		for (PersonenImportColumn column : possibleColumns) {
			CmbItem item = getCmbItem(column);
			if (item == null) {
				item = new CmbItem();
				item.column = column;
				cmbItems.add(item);
			}
			items[i] = item;
			i++;
		}
		
		DefaultComboBoxModel<CmbItem> model = new DefaultComboBoxModel<>(items);
		model.setSelectedItem(getCmbItem(selectedColumn));
		return model;
	}
	
	private void changeToBold(JLabel label) {
		Font font = label.getFont();
		Font boldFont = new Font(font.getFontName(), Font.BOLD, font.getSize());
		label.setFont(boldFont);
	}
	
	private CmbItem getCmbItem(PersonenImportColumn column) {
		if (column == null) {
			return emptyCmbItem;
		}
		for (CmbItem i : cmbItems) {
			if (i.column.getIndex() == column.getIndex()) {
				return i;
			}
		}
		return null;
	}
	
	class CmbItem {
		
		PersonenImportColumn column;
		
		@Override
		public String toString() {
			if (column == null) {
				return "";
			}
			return column.getName();
		}
		
	}
}
