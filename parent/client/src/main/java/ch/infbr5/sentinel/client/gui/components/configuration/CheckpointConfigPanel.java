package ch.infbr5.sentinel.client.gui.components.configuration;

import java.util.List;

import javax.swing.JTextField;

import net.miginfocom.swing.MigLayout;
import ch.infbr5.sentinel.client.gui.util.SwingHelper;
import ch.infbr5.sentinel.client.util.ServiceHelper;
import ch.infbr5.sentinel.client.wsgen.CheckpointDetails;
import ch.infbr5.sentinel.client.wsgen.ConfigurationResponse;

public class CheckpointConfigPanel extends
		AbstractAdminOverviewPanel<CheckpointDetails> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public CheckpointConfigPanel(boolean adminMode) {
		super(adminMode);
	}

	@Override
	protected AbstractAdminTableModel<CheckpointDetails> getTableModel() {
		return new MyTableModel();
	}

	public class MyTableModel extends
			AbstractAdminTableModel<CheckpointDetails> {

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
		public CheckpointDetails getNewDataRecord() {
			CheckpointDetails detail = new CheckpointDetails();
			detail.setName("");

			return detail;
		}

		@Override
		public void removeBackendObject(CheckpointDetails object) {
			ServiceHelper.getConfigurationsService().removeCheckpoint(
					object.getId());
		}

		@Override
		public void updateBackendObject(CheckpointDetails object) {
			ServiceHelper.getConfigurationsService().updateCheckpoint(object);

		}

		@Override
		public List<CheckpointDetails> getBackendObjects() {
			ConfigurationResponse response = ServiceHelper
					.getConfigurationsService().getCheckpoints();
			return response.getCheckpointDetails();
		}

		@Override
		public String[] getHeaderNames() {
			return headerNames;
		}
	}

	@Override
	protected AbstractAdminDetailPanel<CheckpointDetails> getDetailPanel() {
		return new MyDetailPanel();
	}

	public class MyDetailPanel extends
			AbstractAdminDetailPanel<CheckpointDetails> {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private JTextField fieldName;

		public MyDetailPanel() {
			setLayout(new MigLayout("inset 20"));

			SwingHelper.addSeparator(this, "Titel");

			fieldName = createField("Name");
		}

		public void getFieldValues() {
			data.setName(fieldName.getText());
		}

		public void setFieldValues() {
			fieldName.setText(data.getName());
		}

		public void clearFieldValues() {
			fieldName.setText("");
		}

		@Override
		public void setEditable(boolean mode) {
			fieldName.setEditable(mode);
		}
	}
}
