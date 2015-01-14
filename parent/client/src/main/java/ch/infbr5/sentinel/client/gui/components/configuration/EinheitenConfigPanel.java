package ch.infbr5.sentinel.client.gui.components.configuration;

import java.util.List;

import javax.swing.JTextField;

import net.miginfocom.swing.MigLayout;
import ch.infbr5.sentinel.client.gui.util.ColorChooserLabel;
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

      private final ColorChooserLabel fieldRgbColorGsVb;
      private final ColorChooserLabel fieldRgbColorTrpK;
      private final ColorChooserLabel fieldRgbColorEinh;

      private final JTextField fieldTextGsVb;
      private final JTextField fieldTextTrpK;
      private final JTextField fieldTextEinh;

      public MyDetailPanel() {
         setLayout(new MigLayout("inset 20"));

         SwingHelper.addSeparator(this, "Einheit");

         fieldName = createField("Name");

         fieldRgbColorGsVb = createColorChooser("Farbe Gs Vb");
         fieldRgbColorTrpK = createColorChooser("Farbe Trp K");
         fieldRgbColorEinh = createColorChooser("Farbe Einh");

         fieldTextGsVb = createField("Text Gs Vb");
         fieldTextTrpK = createField("Text Trp K");
         fieldTextEinh = createField("Text Einh");
      }

      @Override
      public void getFieldValues() {
         data.setName(fieldName.getText());

         data.setRgbColorGsVb(fieldRgbColorGsVb.getBackgroundHtmlColor());
         data.setRgbColorTrpK(fieldRgbColorTrpK.getBackgroundHtmlColor());
         data.setRgbColorEinh(fieldRgbColorEinh.getBackgroundHtmlColor());

         data.setTextGsVb(fieldTextGsVb.getText());
         data.setTextTrpK(fieldTextTrpK.getText());
         data.setTextEinh(fieldTextEinh.getText());
      }

      @Override
      public void setFieldValues() {
         fieldName.setText(data.getName());

         fieldRgbColorGsVb.setBackgroundHtmlColor(data.getRgbColorGsVb());
         fieldRgbColorTrpK.setBackgroundHtmlColor(data.getRgbColorTrpK());
         fieldRgbColorEinh.setBackgroundHtmlColor(data.getRgbColorEinh());

         fieldTextGsVb.setText(data.getTextGsVb());
         fieldTextTrpK.setText(data.getTextTrpK());
         fieldTextEinh.setText(data.getTextEinh());
      }

      @Override
      public void clearFieldValues() {
         fieldName.setText("");

         fieldRgbColorGsVb.setBackgroundHtmlColor("#000000");
         fieldRgbColorTrpK.setBackgroundHtmlColor("#000000");
         fieldRgbColorEinh.setBackgroundHtmlColor("#000000");

         fieldTextGsVb.setText("");
         fieldTextTrpK.setText("");
         fieldTextEinh.setText("");
      }

      @Override
      public void setEditable(final boolean mode) {
         fieldName.setEditable(mode);
         fieldRgbColorGsVb.setEnabled(mode);
         fieldRgbColorTrpK.setEnabled(mode);
         fieldRgbColorEinh.setEnabled(mode);
         fieldTextGsVb.setEditable(mode);
         fieldTextTrpK.setEditable(mode);
         fieldTextEinh.setEditable(mode);
      }
   }
}
