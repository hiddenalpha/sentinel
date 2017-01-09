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

   private static final long serialVersionUID = 1L;

   public ZoneConfigPanel() {
      super();
      setDefaultSort(0, true);
   }

   @Override
   protected AbstractAdminTableModel<ZoneDetails> getTableModel() {
      return new MyTableModel();
   }

   public class MyTableModel extends AbstractAdminTableModel<ZoneDetails> {

      private static final long serialVersionUID = 1L;

      private final String[] headerNames = { "Name" };

      @Override
      public Object getValueAt(final int rowIndex, final int columnIndex) {
         return getDataRecord(rowIndex).getName();
      }

      @Override
      public ZoneDetails getNewDataRecord() {
         final ZoneDetails detail = new ZoneDetails();
         detail.setName("");
         detail.setUndOpRegeln(false);

         return detail;
      }

      @Override
      public void removeBackendObject(final ZoneDetails object) {
         // todo
         // ServiceHelper.getConfigurationsService().removeZone(object.getId());
      }

      @Override
      public void updateBackendObject(final ZoneDetails object) {
         // todo
         // ServiceHelper.getConfigurationsService().updateZone(object);
      }

      @Override
      public List<ZoneDetails> getBackendObjects() {
         // todo
         final ConfigurationResponse response = ServiceHelper.getConfigurationsService().getZonen();
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
      private final JTextField fieldName;
      private final JCheckBox undOpRegelnCheckBox;

      public MyDetailPanel() {
         setLayout(new MigLayout("inset 20"));

         SwingHelper.addSeparator(this, "Titel");

         fieldName = createField("Name");
         undOpRegelnCheckBox = createCheckbox("UndOpRegeln");
      }

      @Override
      public void getFieldValues() {
         data.setName(fieldName.getText());
         data.setUndOpRegeln(undOpRegelnCheckBox.isSelected());
      }

      @Override
      public void setFieldValues() {
         fieldName.setText(data.getName());
         undOpRegelnCheckBox.setSelected(data.isUndOpRegeln());
      }

      @Override
      public void clearFieldValues() {
         fieldName.setText("");
         undOpRegelnCheckBox.setSelected(false);
      }

      @Override
      public void setEditable(final boolean mode) {
         fieldName.setEditable(mode);
         undOpRegelnCheckBox.setEnabled(mode);
      }
   }
}
