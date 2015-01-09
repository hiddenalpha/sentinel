package ch.infbr5.sentinel.client.gui.components.configuration;

import java.util.List;

import javax.swing.JTextField;

import net.miginfocom.swing.MigLayout;
import ch.infbr5.sentinel.client.util.ServiceHelper;
import ch.infbr5.sentinel.client.wsgen.ConfigurationDetails;
import ch.infbr5.sentinel.client.wsgen.ConfigurationResponse;
import ch.infbr5.sentinel.common.gui.util.SwingHelper;

public class ConfigurationValuePanel extends AbstractAdminOverviewPanel<ConfigurationDetails> {

   /**
    *
    */
   private static final long serialVersionUID = 1L;

   public ConfigurationValuePanel(final boolean adminMode) {
      super(adminMode);
   }

   @Override
   protected AbstractAdminTableModel<ConfigurationDetails> getTableModel() {
      return new MyTableModel();
   }

   public class MyTableModel extends AbstractAdminTableModel<ConfigurationDetails> {

      /**
       *
       */
      private static final long serialVersionUID = 1L;
      private final String[] headerNames = { "Key", "String Value", "Long Value", "Gültig für" };

      @Override
      public Object getValueAt(final int rowIndex, final int columnIndex) {
         if (columnIndex == 0) {
            return getDataRecord(rowIndex).getKey();
         } else if (columnIndex == 1) {
            return getDataRecord(rowIndex).getStringValue();
         } else if (columnIndex == 2) {
            return getDataRecord(rowIndex).getLongValue();
         } else if (columnIndex == 3) {
            return getDataRecord(rowIndex).getValidFor();
         }
         return null;
      }

      @Override
      public String[] getHeaderNames() {
         return headerNames;
      }

      @Override
      public ConfigurationDetails getNewDataRecord() {
         final ConfigurationDetails c = new ConfigurationDetails();
         c.setKey("");
         c.setValidFor("");

         return c;
      }

      @Override
      public void removeBackendObject(final ConfigurationDetails object) {
         ServiceHelper.getConfigurationsService().removeConfiguration(object.getId());
      }

      @Override
      public void updateBackendObject(final ConfigurationDetails object) {
         ServiceHelper.getConfigurationsService().updateConfigurationValue(object);
      }

      @Override
      public List<ConfigurationDetails> getBackendObjects() {
         final ConfigurationResponse response = ServiceHelper.getConfigurationsService().getConfigurationValues();
         return response.getConfigurationDetails();
      }

   }

   @Override
   protected AbstractAdminDetailPanel<ConfigurationDetails> getDetailPanel() {
      return new MyDetailPanel();
   }

   public class MyDetailPanel extends AbstractAdminDetailPanel<ConfigurationDetails> {

      /**
       *
       */
      private static final long serialVersionUID = 1L;
      private final JTextField fieldKey;
      private final JTextField fieldValidFor;
      private final JTextField fieldValueString;
      private final JTextField fieldValueLong;

      public MyDetailPanel() {
         setLayout(new MigLayout("inset 20"));

         SwingHelper.addSeparator(this, "Configuration");

         fieldKey = createField("Key");
         fieldValidFor = createField("Gültig");

         SwingHelper.addSeparator(this, "Werte");

         fieldValueString = createField("String Value");
         fieldValueLong = createField("Long Value");
      }

      @Override
      public void getFieldValues() {
         data.setKey(fieldKey.getText());
         data.setStringValue(fieldValueString.getText());
         data.setLongValue(Long.valueOf(fieldValueLong.getText()));
         data.setValidFor(fieldValidFor.getText());
      }

      @Override
      public void setFieldValues() {
         fieldKey.setText(data.getKey());
         fieldValueString.setText(data.getStringValue());
         fieldValueLong.setText(String.valueOf(data.getLongValue()));
         fieldValidFor.setText(data.getValidFor());
      }

      @Override
      public void clearFieldValues() {
         fieldKey.setText("");
         fieldValueString.setText("");
         fieldValueLong.setText("");
         fieldValidFor.setText("");
      }

      @Override
      public void setEditable(final boolean mode) {
         fieldKey.setEditable(mode);
         fieldValueString.setEditable(mode);
         fieldValueLong.setEditable(mode);
         fieldValidFor.setEditable(mode);
      }
   }
}
