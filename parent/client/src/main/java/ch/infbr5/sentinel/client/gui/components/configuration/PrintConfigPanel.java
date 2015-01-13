package ch.infbr5.sentinel.client.gui.components.configuration;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

import net.miginfocom.swing.MigLayout;
import ch.infbr5.sentinel.client.gui.util.DesktopHelper;
import ch.infbr5.sentinel.client.util.ServiceHelper;
import ch.infbr5.sentinel.client.wsgen.ConfigurationResponse;
import ch.infbr5.sentinel.client.wsgen.EinheitDetails;
import ch.infbr5.sentinel.client.wsgen.PrintJobDetails;
import ch.infbr5.sentinel.common.gui.util.SwingHelper;
import ch.infbr5.sentinel.common.util.DateFormater;

public class PrintConfigPanel extends AbstractAdminOverviewPanel<PrintJobDetails> {

   private static final long serialVersionUID = 1L;

   private MyTableModel tableModel;

   @Override
   protected AbstractAdminTableModel<PrintJobDetails> getTableModel() {
      tableModel = new MyTableModel();
      return tableModel;
   }

   @Override
   protected boolean isSaveButtonAvailable() {
      return false;
   }

   @Override
   protected boolean isNewButtonAvailable() {
      return false;
   }

   @Override
   protected boolean isEditButtonAvailable() {
      return false;
   }

   @Override
   protected boolean isCancelButtonAvailable() {
      return false;
   }

   public class MyTableModel extends AbstractAdminTableModel<PrintJobDetails> {

      private static final long serialVersionUID = 1L;

      private final String[] headerNames = { "Datum", "Beschreibung", "Datei" };

      @Override
      public Object getValueAt(final int rowIndex, final int columnIndex) {
         if (columnIndex == 0) {
            return DateFormater.formatToDateWithTime(getDataRecord(rowIndex).getPrintJobDate());
         } else if (columnIndex == 1) {
            return getDataRecord(rowIndex).getPrintJobDesc();
         } else if (columnIndex == 2) {
            return getDataRecord(rowIndex).getPintJobFile();
         }
         return null;
      }

      @Override
      public PrintJobDetails getNewDataRecord() {
         return null;
      }

      @Override
      public void removeBackendObject(final PrintJobDetails object) {
         ServiceHelper.getConfigurationsService().removePrintJob(object.getPrintJobId());
      }

      @Override
      public void updateBackendObject(final PrintJobDetails object) {

      }

      @Override
      public List<PrintJobDetails> getBackendObjects() {
         final ConfigurationResponse response = ServiceHelper.getConfigurationsService().getPrintJobs();
         if (getInstalledDetailPanel() != null) {
            ((MyDetailPanel) getInstalledDetailPanel()).updateAusweisDruckenButtonName();
         }
         return response.getPrintJobDetails();
      }

      @Override
      public String[] getHeaderNames() {
         return headerNames;
      }
   }

   @Override
   protected AbstractAdminDetailPanel<PrintJobDetails> getDetailPanel() {
      return new MyDetailPanel();
   }

   public class MyDetailPanel extends AbstractAdminDetailPanel<PrintJobDetails> {

      private static final long serialVersionUID = 1L;

      private final JTextField druckdatum;
      private final JTextField beschreibung;
      private final JTextField dateiname;

      private final JButton btnAusweiseDrucken;

      public MyDetailPanel() {
         setLayout(new MigLayout("inset 20"));

         // Details pro PrintJob
         druckdatum = createFieldNotEditable("Druckdatum");
         beschreibung = createFieldNotEditable("Beschreibung");
         dateiname = createFieldNotEditable("Dateiname");

         final JButton btnPdfOeffnen = new JButton("PDF öffnen");
         btnPdfOeffnen.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
               if (data != null) {
                  final ConfigurationResponse response = ServiceHelper.getConfigurationsService().getPrintJob(
                        data.getPrintJobId());
                  showPrintJob(response);
               }
            }
         });
         this.add(SwingHelper.createLabel(""), "gap para");
         this.add(btnPdfOeffnen, "span, growx");

         // Druckaufträge starten
         SwingHelper.addSeparator(this, "Druckauftrag starten");

         // Ausweise
         btnAusweiseDrucken = new JButton("neu erstellt");
         btnAusweiseDrucken.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
               final ConfigurationResponse response = ServiceHelper.getConfigurationsService().printAusweise();
               handleResponse(response);
               updateAusweisDruckenButtonName();
            }
         });
         addPrintSectionLabel("Ausweise");
         this.add(btnAusweiseDrucken, "span, growx");
         updateAusweisDruckenButtonName();

         // Ausweisboxen
         addPrintSectionLabel("Ausweisboxen");
         addPrintSectionButton("Alle", new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
               final ConfigurationResponse response = ServiceHelper.getConfigurationsService().printAusweisboxAlle();
               handleResponse(response);
            }
         });
         addPrintSectionButton("nach Einheit", new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
               final String einheit = selectEinheit();
               if (einheit != null && !"".equals(einheit)) {
                  final ConfigurationResponse response = ServiceHelper.getConfigurationsService()
                        .printAusweisboxNachEinheit(einheit);
                  handleResponse(response);
               }
            }
         });

         // Ausweislisten
         addPrintSectionLabel("Ausweisliste");
         addPrintSectionButton("Alle", new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
               final ConfigurationResponse response = ServiceHelper.getConfigurationsService().printAusweisListeAlle();
               handleResponse(response);
            }
         });
         addPrintSectionButton("nach Einheit", new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
               final String einheit = selectEinheit();
               if (einheit != null && !"".equals(einheit)) {
                  final ConfigurationResponse response = ServiceHelper.getConfigurationsService()
                        .printAusweisListeNachEinheit(einheit);
                  handleResponse(response);
               }
            }
         });

         // Personen Liste
         addPrintSectionLabel("Personenliste");
         addPrintSectionButton("Alle", new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
               final ConfigurationResponse response = ServiceHelper.getConfigurationsService().printPersonenListeAlle();
               handleResponse(response);
            }
         });
         addPrintSectionButton("nach Einheit", new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
               final String einheit = selectEinheit();
               if (einheit != null && !"".equals(einheit)) {
                  final ConfigurationResponse response = ServiceHelper.getConfigurationsService()
                        .printPersonenListeNachEinheit(einheit);
                  handleResponse(response);
               }
            }
         });
      }

      @Override
      public void getFieldValues() {

      }

      @Override
      public void setFieldValues() {
         druckdatum.setText(DateFormater.formatToDateWithDetailTime(data.getPrintJobDate()));
         beschreibung.setText(data.getPrintJobDesc());
         dateiname.setText(data.getPintJobFile());
      }

      @Override
      public void clearFieldValues() {
         beschreibung.setText("");
         dateiname.setText("");
         druckdatum.setText("");
      }

      @Override
      public void setEditable(final boolean mode) {

      }

      private void handleResponse(final ConfigurationResponse response) {
         showPrintJob(response);
         tableModel.updateData();
      }

      private void showPrintJob(final ConfigurationResponse response) {
         if (response != null) {
            if (response.getPrintJobDetails() != null && response.getPrintJobDetails().size() > 0) {
               final PrintJobDetails job = response.getPrintJobDetails().get(0);
               final byte[] pdf = job.getPdf();
               DesktopHelper.openPdfFile(job.getPintJobFile(), pdf);
            } else {
               JOptionPane.showMessageDialog(null, "Keine ausstehende Daten zum Drucken.", "Keine Daten",
                     JOptionPane.WARNING_MESSAGE);
            }
         }
      }

      public void updateAusweisDruckenButtonName() {
         final int no = ServiceHelper.getConfigurationsService().anzahlAusstehendeZuDruckendeAusweise();
         if (no > 0) {
            btnAusweiseDrucken.setText(no + " Ausweis(e) drucken");
            btnAusweiseDrucken.setEnabled(true);
         } else {
            btnAusweiseDrucken.setText("Keine ausstehende Ausweise");
            btnAusweiseDrucken.setEnabled(false);
         }
      }

      private JTextField createFieldNotEditable(final String name) {
         final JTextField field = createField(name);
         field.setEditable(false);
         return field;
      }

      private JButton addPrintSectionButton(final String text, final ActionListener actionListener) {
         final JButton button = new JButton(text);
         button.addActionListener(actionListener);
         this.add(button);
         return button;
      }

      private void addPrintSectionLabel(final String text) {
         this.add(SwingHelper.createLabel(text), "newline, gap para");
      }

      private String selectEinheit() {
         final ConfigurationResponse configurationResponse = ServiceHelper.getConfigurationsService().getEinheiten();
         final List<EinheitDetails> ed = configurationResponse.getEinheitDetails();
         final String[] values = new String[ed.size()];
         for (int i = 0; i < values.length; i++) {
            values[i] = ed.get(i).getName();
         }

         if (values.length > 0) {
            final String selected = (String) JOptionPane.showInputDialog(this, "Wähle eine Einheit aus",
                  "Einheit Auswahl", JOptionPane.QUESTION_MESSAGE, null, values, values[0]);
            return selected;
         } else {
            return "";
         }
      }

   }

}
