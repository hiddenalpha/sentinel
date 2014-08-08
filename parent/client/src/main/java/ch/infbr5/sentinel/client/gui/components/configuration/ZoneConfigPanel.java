package ch.infbr5.sentinel.client.gui.components.configuration;

import java.util.List;

import javax.swing.JCheckBox;
import javax.swing.JTextField;

import net.miginfocom.swing.MigLayout;
import ch.infbr5.sentinel.client.util.ServiceHelper;
import ch.infbr5.sentinel.client.wsgen.ConfigurationResponse;
import ch.infbr5.sentinel.client.wsgen.ZoneDetails;
import ch.infbr5.sentinel.common.gui.util.SwingHelper;

public class ZoneConfigPanel extends AbstractAdminOverviewPanel<ZoneDetails> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ZoneConfigPanel(boolean adminMode) {
		super(adminMode);
	}

	@Override
	protected AbstractAdminTableModel<ZoneDetails> getTableModel() {
		return new MyTableModel();
	}

	public class MyTableModel extends AbstractAdminTableModel<ZoneDetails> {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private final String[] headerNames = { "Name" };

		@Override
		public Object getValueAt(int rowIndex, int columnIndex) {
			return getDataRecord(rowIndex).getName();
		}

		@Override
		public ZoneDetails getNewDataRecord() {
			ZoneDetails detail = new ZoneDetails();
			detail.setName("");
			detail.setUndOpRegeln(false);

			return detail;
		}

		@Override
		public void removeBackendObject(ZoneDetails object) {
			// todo
			// ServiceHelper.getConfigurationsService().removeZone(object.getId());
		}

		@Override
		public void updateBackendObject(ZoneDetails object) {
			// todo
			// ServiceHelper.getConfigurationsService().updateZone(object);
		}

		@Override
		public List<ZoneDetails> getBackendObjects() {
			// todo
			ConfigurationResponse response = ServiceHelper
					.getConfigurationsService().getZonen();
			return response.getZoneDetails();
		}

		@Override
		public String[] getHeaderNames() {
			return headerNames;
		}
	}

	@Override
	protected AbstractAdminDetailPanel<ZoneDetails> getDetailPanel() {
		return new MyDetailPanel();
	}

	public class MyDetailPanel extends AbstractAdminDetailPanel<ZoneDetails> {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private JTextField fieldName;
		private JCheckBox undOpRegelnCheckBox;

		public MyDetailPanel() {
			setLayout(new MigLayout("inset 20"));

			SwingHelper.addSeparator(this, "Titel");

			fieldName = createField("Name");
			undOpRegelnCheckBox = createCheckbox("UndOpRegeln");
		}

		public void getFieldValues() {
			data.setName(fieldName.getText());
			data.setUndOpRegeln(undOpRegelnCheckBox.isSelected());
		}

		public void setFieldValues() {
			fieldName.setText(data.getName());
			undOpRegelnCheckBox.setSelected(data.isUndOpRegeln());
		}

		public void clearFieldValues() {
			fieldName.setText("");
			undOpRegelnCheckBox.setSelected(false);
		}

		@Override
		public void setEditable(boolean mode) {
			fieldName.setEditable(mode);
			undOpRegelnCheckBox.setEnabled(mode);
		}
	}
}
