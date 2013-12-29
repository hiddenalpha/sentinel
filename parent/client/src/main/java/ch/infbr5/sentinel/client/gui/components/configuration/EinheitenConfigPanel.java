package ch.infbr5.sentinel.client.gui.components.configuration;

import java.util.List;

import javax.swing.JTextField;

import net.miginfocom.swing.MigLayout;
import ch.infbr5.sentinel.client.gui.util.SwingHelper;
import ch.infbr5.sentinel.client.util.ServiceHelper;
import ch.infbr5.sentinel.client.wsgen.ConfigurationResponse;
import ch.infbr5.sentinel.client.wsgen.EinheitDetails;

public class EinheitenConfigPanel extends
		AbstractAdminOverviewPanel<EinheitDetails> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public EinheitenConfigPanel(boolean adminMode) {
		super(adminMode);
	}

	@Override
	protected AbstractAdminTableModel<EinheitDetails> getTableModel() {
		return new MyTableModel();
	}

	public class MyTableModel extends AbstractAdminTableModel<EinheitDetails> {

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
		public EinheitDetails getNewDataRecord() {
			EinheitDetails detail = new EinheitDetails();
			detail.setName("");

			return detail;
		}

		@Override
		public void removeBackendObject(EinheitDetails object) {
			ServiceHelper.getConfigurationsService().removeEinheit(
					object.getId());
		}

		@Override
		public void updateBackendObject(EinheitDetails object) {
			ServiceHelper.getConfigurationsService().updateEinheit(object);
		}

		@Override
		public List<EinheitDetails> getBackendObjects() {
			ConfigurationResponse response = ServiceHelper
					.getConfigurationsService().getEinheiten();
			return response.getEinheitDetails();
		}

		@Override
		public String[] getHeaderNames() {
			return headerNames;
		}
	}

	@Override
	protected AbstractAdminDetailPanel<EinheitDetails> getDetailPanel() {
		return new MyDetailPanel();
	}

	public class MyDetailPanel extends AbstractAdminDetailPanel<EinheitDetails> {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private JTextField fieldName;
		private JTextField fieldRgbColorGsVb;
		private JTextField fieldRgbColorTrpK;
		private JTextField fieldRgbColorEinh;
		private JTextField fieldTextGsVb;
		private JTextField fieldTextTrpK;
		private JTextField fieldTextEinh;

		public MyDetailPanel() {
			setLayout(new MigLayout("inset 20"));

			SwingHelper.addSeparator(this, "Titel");

			fieldName = createField("Name");
			fieldRgbColorGsVb = createField("RGB Color Gs Vb");
			fieldRgbColorTrpK = createField("RGB Color Trp K");
			fieldRgbColorEinh = createField("RGB Color Einh");
			fieldTextGsVb = createField("Text Gs Vb");
			fieldTextTrpK = createField("Text Trp K");
			fieldTextEinh = createField("Text Einh");
		}

		public void getFieldValues() {
			data.setName(fieldName.getText());
			data.setRgbColorGsVb(fieldRgbColorGsVb.getText());
			data.setRgbColorTrpK(fieldRgbColorTrpK.getText());
			data.setRgbColorEinh(fieldRgbColorEinh.getText());
			data.setTextGsVb(fieldTextGsVb.getText());
			data.setTextTrpK(fieldTextTrpK.getText());
			data.setTextEinh(fieldTextEinh.getText());
		}

		public void setFieldValues() {
			fieldName.setText(data.getName());
			fieldRgbColorGsVb.setText(data.getRgbColorGsVb());
			fieldRgbColorTrpK.setText(data.getRgbColorTrpK());
			fieldRgbColorEinh.setText(data.getRgbColorEinh());
			fieldTextGsVb.setText(data.getTextGsVb());
			fieldTextTrpK.setText(data.getTextTrpK());
			fieldTextEinh.setText(data.getTextEinh());
		}

		public void clearFieldValues() {
			fieldName.setText("");
			fieldRgbColorGsVb.setText("");
			fieldRgbColorTrpK.setText("");
			fieldRgbColorEinh.setText("");
			fieldTextGsVb.setText("");
			fieldTextTrpK.setText("");
			fieldTextEinh.setText("");
		}

		@Override
		public void setEditable(boolean mode) {
			fieldName.setEditable(mode);
			fieldRgbColorGsVb.setEditable(mode);
			fieldRgbColorTrpK.setEditable(mode);
			fieldRgbColorEinh.setEditable(mode);
			fieldTextGsVb.setEditable(mode);
			fieldTextTrpK.setEditable(mode);
			fieldTextEinh.setEditable(mode);
		}
	}
}
