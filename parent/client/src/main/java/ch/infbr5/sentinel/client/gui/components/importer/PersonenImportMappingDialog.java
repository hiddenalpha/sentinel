package ch.infbr5.sentinel.client.gui.components.importer;

import java.awt.Font;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import net.miginfocom.swing.MigLayout;
import ch.infbr5.sentinel.client.util.ServiceHelper;
import ch.infbr5.sentinel.client.wsgen.Column;
import ch.infbr5.sentinel.client.wsgen.ColumnMappingResponse;
import ch.infbr5.sentinel.client.wsgen.MappingPersonenAttributeToColumn;
import ch.infbr5.sentinel.client.wsgen.MappingPersonenAttributeToColumnArray;
import ch.infbr5.sentinel.client.wsgen.PersonenAttribute;

public class PersonenImportMappingDialog {

	private Frame parent;
	
	private String sessionKey;
	
	private ColumnMappingResponse response;
	
	private JLabel lblHeader1;
	
	private JLabel lblHeader2;
	
	private List<JLabel> lblsPersonenAttribute = new ArrayList<>();
	
	private List<JComboBox<CmbItem>> cmbsColumns = new ArrayList<>();
	
	private List<CmbItem> cmbItems = new ArrayList<>();
	
	public PersonenImportMappingDialog(Frame parent, String sessionKey, ColumnMappingResponse response) {
		this.parent = parent;
		this.sessionKey = sessionKey;
		this.response = response;
	}
	
	private void createLabels() {
		lblHeader1 = new JLabel("Personenattribute");
		lblHeader2 = new JLabel("Spaltenüberschriften");
		changeToBold(lblHeader1);
		changeToBold(lblHeader2);
		
		for (PersonenAttribute personenAttribute : PersonenAttribute.values()) {
			lblsPersonenAttribute.add(new JLabel(personenAttribute.name()));
		}
	}
	
	private void createDropdowns() {
		for (PersonenAttribute personenAttribute : PersonenAttribute.values()) {
			for (MappingPersonenAttributeToColumn mapping : response.getMappings()) {
				if (mapping.getPersonenAttribute().equals(personenAttribute)) {
					JComboBox<CmbItem> cmb = new JComboBox<>();
					cmb.setActionCommand(personenAttribute.value()); // Set the name of the person attribute associated with this combobox
					
					ComboBoxModel<CmbItem> model = createComboBoxModel();
					cmb.setModel(model);
					model.setSelectedItem(getCmbItem(mapping.getColumn()));
					
					cmbsColumns.add(cmb);
				}
			}
		}
	}
	
	private ComboBoxModel<CmbItem> createComboBoxModel() {
		if (cmbItems.isEmpty()) {
			for (Column column : response.getColumns()) {
				CmbItem item = new CmbItem();
				item.column = column;
				cmbItems.add(item);
			}
		}
		return new DefaultComboBoxModel<>(cmbItems.toArray(new CmbItem[cmbItems.size()]));
	}
	
	private void changeToBold(JLabel label) {
		Font font = label.getFont();
		Font boldFont = new Font(font.getFontName(), Font.BOLD, font.getSize());
		label.setFont(boldFont);
	}
	
	public void show() {
		
		final JDialog dialog = new JDialog(parent);
		dialog.setTitle("Spaltenzuordnung");
		dialog.setModal(true);
		dialog.setLayout(new MigLayout());
		
		JPanel panelZuordnungen = new JPanel();
		panelZuordnungen.setLayout(new MigLayout());
		
		createLabels();
		createDropdowns();
		
		panelZuordnungen.add(lblHeader1, "cell 0 0");
		panelZuordnungen.add(lblHeader2, "cell 1 0");
		for (int i = 0; i < lblsPersonenAttribute.size(); i++) {
			panelZuordnungen.add(lblsPersonenAttribute.get(i), "cell 0 " + (i+1));
		}
		for (int i = 0; i < cmbsColumns.size(); i++) {
			panelZuordnungen.add(cmbsColumns.get(i), "cell 1 " + (i+1));
		}
		dialog.add(panelZuordnungen, "cell 0 0");
		
		JButton buttonOK = new JButton("OK");
		dialog.add(buttonOK, "cell 1 0");
		buttonOK.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				dialog.setVisible(false);
				MappingPersonenAttributeToColumnArray array = new MappingPersonenAttributeToColumnArray();
				for (JComboBox<CmbItem> cmb : cmbsColumns) {
					PersonenAttribute attribute = PersonenAttribute.fromValue(cmb.getActionCommand());
					CmbItem cmbItem = (CmbItem) cmb.getSelectedItem();
					
					MappingPersonenAttributeToColumn mapping = new MappingPersonenAttributeToColumn();
					mapping.setColumn(cmbItem.column);
					mapping.setPersonenAttribute(attribute);
					array.getItem().add(mapping);
				}
				
				ServiceHelper.getPersonenImporterService().setColumnMappings(sessionKey, array);
				boolean result = ServiceHelper.getPersonenImporterService().startImport(sessionKey);
				
				if (result) {
					JOptionPane.showMessageDialog(null,
							"Die Datei wurde gespeichert.",
							"Pisadaten importieren", JOptionPane.OK_OPTION);
				} else {
					JOptionPane
							.showMessageDialog(
									null,
									"Die Datei konnte nicht erfolgreich gespeichert werden.",
									"Pisadaten importieren",
									JOptionPane.CANCEL_OPTION);
				}
		
			}
		});
		
		dialog.pack();
		dialog.setVisible(true);
	}
	
	private CmbItem getCmbItem(Column column) {
		for (CmbItem i : cmbItems) {
			if (i.column.getIndex() == column.getIndex()) {
				return i;
			}
		}
		return null;
	}
	
	class CmbItem {
		
		Column column;
		
		@Override
		public String toString() {
			return column.getName();
		}
		
	}
	
}
