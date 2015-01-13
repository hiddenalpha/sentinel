package ch.infbr5.sentinel.client.gui.components.configuration;

import java.util.List;

import javax.swing.JTextField;

import net.miginfocom.swing.MigLayout;
import ch.infbr5.sentinel.client.util.ServiceHelper;
import ch.infbr5.sentinel.client.wsgen.ConfigurationResponse;
import ch.infbr5.sentinel.client.wsgen.EinheitDetails;
import ch.infbr5.sentinel.common.gui.util.SwingHelper;

public class EinheitenConfigPanel extends AbstractAdminOverviewPanel<EinheitDetails> {

   private static final long serialVersionUID = 1L;

   public EinheitenConfigPanel() {
      super();
      setDefaultSort(0, true);
   }

   @Override
   protected AbstractAdminTableModel<EinheitDetails> getTableModel() {
      return new MyTableModel();
   }

   public class MyTableModel extends AbstractAdminTableModel<EinheitDetails> {

      private static final long serialVersionUID = 1L;

      private final String[] headerNames = { "Name" };

      @Override
      public Object getValueAt(final int rowIndex, final int columnIndex) {
         return getDataRecord(rowIndex).getName();
      }

      @Override
      public EinheitDetails getNewDataRecord() {
         final EinheitDetails detail = new EinheitDetails();
         detail.setName("");
         return detail;
      }

      @Override
      public void removeBackendObject(final EinheitDetails object) {
         ServiceHelper.getConfigurationsService().removeEinheit(object.getId());
      }

      @Override
      public void updateBackendObject(final EinheitDetails object) {
         ServiceHelper.getConfigurationsService().saveEinheit(object);
      }

      @Override
      public List<EinheitDetails> getBackendObjects() {
         final ConfigurationResponse response = ServiceHelper.getConfigurationsService().getEinheiten();
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

      private static final long serialVersionUID = 1L;
      private final JTextField fieldName;
      private final JTextField fieldRgbColorGsVb;
      private final JTextField fieldRgbColorTrpK;
      private final JTextField fieldRgbColorEinh;
      private final JTextField fieldTextGsVb;
      private final JTextField fieldTextTrpK;
      private final JTextField fieldTextEinh;

      public MyDetailPanel() {
         setLayout(new MigLayout("inset 20"));

         SwingHelper.addSeparator(this, "Titel");

         fieldName = createField("Name");
         fieldRgbColorGsVb = createField("RGB Farbe Gs Vb");
         fieldRgbColorTrpK = createField("RGB Farbe Trp K");
         fieldRgbColorEinh = createField("RGB Farbe Einh");
         fieldTextGsVb = createField("Text Gs Vb");
         fieldTextTrpK = createField("Text Trp K");
         fieldTextEinh = createField("Text Einh");
      }

      @Override
      public void getFieldValues() {
         data.setName(fieldName.getText());
         data.setRgbColorGsVb(fieldRgbColorGsVb.getText());
         data.setRgbColorTrpK(fieldRgbColorTrpK.getText());
         data.setRgbColorEinh(fieldRgbColorEinh.getText());
         data.setTextGsVb(fieldTextGsVb.getText());
         data.setTextTrpK(fieldTextTrpK.getText());
         data.setTextEinh(fieldTextEinh.getText());
      }

      @Override
      public void setFieldValues() {
         fieldName.setText(data.getName());
         fieldRgbColorGsVb.setText(data.getRgbColorGsVb());
         fieldRgbColorTrpK.setText(data.getRgbColorTrpK());
         fieldRgbColorEinh.setText(data.getRgbColorEinh());
         fieldTextGsVb.setText(data.getTextGsVb());
         fieldTextTrpK.setText(data.getTextTrpK());
         fieldTextEinh.setText(data.getTextEinh());
      }

      @Override
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
      public void setEditable(final boolean mode) {
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
