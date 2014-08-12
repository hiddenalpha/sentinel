package ch.infbr5.sentinel.client.gui.components.configuration;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
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

public class PrintConfigPanel extends AbstractAdminOverviewPanel<PrintJobDetails> {

	private static final long serialVersionUID = 1L;

	public PrintConfigPanel(boolean adminMode) {
		super(adminMode);
	}

	@Override
	protected AbstractAdminTableModel<PrintJobDetails> getTableModel() {
		return new MyTableModel();
	}

	public class MyTableModel extends AbstractAdminTableModel<PrintJobDetails> {

		private static final long serialVersionUID = 1L;

		private final String[] headerNames = { "Datum", "Printjob", "File" };

		@Override
		public Object getValueAt(int rowIndex, int columnIndex) {
			if (columnIndex == 0) {
				return getDataRecord(rowIndex).getPrintJobDate();
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
		public void removeBackendObject(PrintJobDetails object) {
			// ServiceHelper.getConfigurationsService().removeEinheit(object.getId());
		}

		@Override
		public void updateBackendObject(PrintJobDetails object) {
			// ServiceHelper.getConfigurationsService().updateEinheit(object);
		}

		@Override
		public List<PrintJobDetails> getBackendObjects() {
			ConfigurationResponse response = ServiceHelper.getConfigurationsService().getPrintJobs();
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

	public class MyDetailPanel extends AbstractAdminDetailPanel<PrintJobDetails> implements ActionListener {

		private static final String CMD_BUTTON_PDF_OEFFNEN = "CMD_BUTTON_PDF_OEFFNEN";
		private static final String CMD_BUTTON_DRUCKE_AUSWEISE = "CMD_BUTTON_DRUCKE_AUSWEISE";
		private static final String CMD_BUTTON_DRUCKE_AUSWEIS_LISTE_NACH_NAME = "CMD_BUTTON_DRUCKE_AUSWEIS_LISTE_NAME";
		private static final String CMD_BUTTON_DRUCKE_AUSWEIS_LISTE_NACH_EINH = "CMD_BUTTON_DRUCKE_AUSWEIS_LISTE_EINH";
		private static final String CMD_BUTTON_DRUCKE_PERSONEN_LISTE_NACH_NAME = "CMD_BUTTON_DRUCKE_PERSONEN_LISTE_NACH_NAME";
		private static final String CMD_BUTTON_DRUCKE_PERSONEN_LISTE_NACH_EINH = "CMD_BUTTON_DRUCKE_PERSONEN_LISTE_NACH_EINH";
		private static final String CMD_BUTTON_DRUCKE_AUSWEISBOX_INVENTAR = "CMD_BUTTON_DRUCKE_AUSWEISBOX_INVENTAR";
		/**
		 *
		 */
		private static final long serialVersionUID = 1L;
		private JTextField druckdatum;
		private JTextField beschreibung;
		private JTextField dateiname;

		private JButton ausweiseDruckenButton;
		private JButton ausweisListeDruckenNachNameButton;
		private JButton ausweisListeDruckenNachEinhButton;
		private JButton personenListeDruckenNachNameButton;
		private JButton personenListeDruckenNachEinhButton;
		private JButton ausweisboxInventarDruckenButton;
		private JButton pdfOeffnenButton;

		public MyDetailPanel() {
			setLayout(new MigLayout("inset 20"));

			druckdatum = createField("Druckdatum");
			druckdatum.setEditable(false);
			beschreibung = createField("Beschreibung");
			beschreibung.setEditable(false);
			dateiname = createField("Dateiname");
			dateiname.setEditable(false);

			pdfOeffnenButton = new JButton("PDF öffnen");
			pdfOeffnenButton.addActionListener(this);
			pdfOeffnenButton.setActionCommand(CMD_BUTTON_PDF_OEFFNEN);
			this.add(SwingHelper.createLabel(""), "gap para");
			this.add(pdfOeffnenButton, "span, growx");

			SwingHelper.addSeparator(this, "Druckauftrag starten");

			this.add(SwingHelper.createLabel("Ausweise"), "gap para");

			ausweiseDruckenButton = new JButton("neu erstellt");
			ausweiseDruckenButton.addActionListener(this);
			ausweiseDruckenButton.setActionCommand(CMD_BUTTON_DRUCKE_AUSWEISE);
			this.add(ausweiseDruckenButton);

			this.add(SwingHelper.createLabel("Ausweisliste"), "newline, gap para");

			ausweisListeDruckenNachNameButton = new JButton("nach Name");
			ausweisListeDruckenNachNameButton.addActionListener(this);
			ausweisListeDruckenNachNameButton.setActionCommand(CMD_BUTTON_DRUCKE_AUSWEIS_LISTE_NACH_NAME);
			this.add(ausweisListeDruckenNachNameButton);

			ausweisListeDruckenNachEinhButton = new JButton("nach Einheit");
			ausweisListeDruckenNachEinhButton.addActionListener(this);
			ausweisListeDruckenNachEinhButton.setActionCommand(CMD_BUTTON_DRUCKE_AUSWEIS_LISTE_NACH_EINH);
			this.add(ausweisListeDruckenNachEinhButton);

			ausweisboxInventarDruckenButton = new JButton("Ausweisboxen");
			ausweisboxInventarDruckenButton.addActionListener(this);
			ausweisboxInventarDruckenButton.setActionCommand(CMD_BUTTON_DRUCKE_AUSWEISBOX_INVENTAR);
			this.add(ausweisboxInventarDruckenButton);

			this.add(SwingHelper.createLabel("Personenliste"), "newline, gap para");

			personenListeDruckenNachNameButton = new JButton("nach Name");
			personenListeDruckenNachNameButton.addActionListener(this);
			personenListeDruckenNachNameButton.setActionCommand(CMD_BUTTON_DRUCKE_PERSONEN_LISTE_NACH_NAME);
			this.add(personenListeDruckenNachNameButton);

			personenListeDruckenNachEinhButton = new JButton("nach Einheit");
			personenListeDruckenNachEinhButton.addActionListener(this);
			personenListeDruckenNachEinhButton.setActionCommand(CMD_BUTTON_DRUCKE_PERSONEN_LISTE_NACH_EINH);
			this.add(personenListeDruckenNachEinhButton);

		}

		@Override
		public void getFieldValues() {
		}

		@Override
		public void setFieldValues() {

			if (data.getPrintJobDate() != null) {
				SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
				druckdatum.setText(sdf.format(data.getPrintJobDate().toGregorianCalendar().getTime()));
			} else {
				druckdatum.setText("");
			}

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
		public void setEditable(boolean mode) {
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			ConfigurationResponse response = null;

			if (e.getActionCommand().equals(CMD_BUTTON_DRUCKE_AUSWEISE)) {
				response = ServiceHelper.getConfigurationsService().printAusweise();
			} else if (e.getActionCommand().equals(CMD_BUTTON_DRUCKE_AUSWEIS_LISTE_NACH_NAME)) {
				response = ServiceHelper.getConfigurationsService().printAusweisListe(true, false, "");

			} else if (e.getActionCommand().equals(CMD_BUTTON_DRUCKE_AUSWEIS_LISTE_NACH_EINH)) {
				response = ServiceHelper.getConfigurationsService().printAusweisListe(true, true, selectEinheit());

			} else if (e.getActionCommand().equals(CMD_BUTTON_DRUCKE_AUSWEISBOX_INVENTAR)) {
				response = ServiceHelper.getConfigurationsService().printAusweisboxInventar(selectEinheit());

			} else if (e.getActionCommand().equals(CMD_BUTTON_DRUCKE_PERSONEN_LISTE_NACH_NAME)) {
				response = ServiceHelper.getConfigurationsService().printAusweisListe(false, false, "");

			} else if (e.getActionCommand().equals(CMD_BUTTON_DRUCKE_PERSONEN_LISTE_NACH_EINH)) {
				response = ServiceHelper.getConfigurationsService().printAusweisListe(false, true, selectEinheit());
			} else if (e.getActionCommand().equals(CMD_BUTTON_PDF_OEFFNEN)) {
				if (data != null) {
					response = ServiceHelper.getConfigurationsService().getPrintJob(data.getPrintJobId());
				}
			}

			if (response != null) {
				if (response.getPrintJobDetails() != null && response.getPrintJobDetails().size() > 0) {
					PrintJobDetails job = response.getPrintJobDetails().get(0);
					byte[] pdf = job.getPdf();
					DesktopHelper.openPdfFile(job.getPintJobFile(), pdf);
				} else {
					JOptionPane.showMessageDialog(null, "Keine ausstehende Daten zum Drucken.", "Keine Daten",
							JOptionPane.WARNING_MESSAGE);
				}
			}
		}

		private String selectEinheit() {
			ConfigurationResponse configurationResponse = ServiceHelper.getConfigurationsService().getEinheiten();
			List<EinheitDetails> ed = configurationResponse.getEinheitDetails();
			String[] values = new String[ed.size()];
			for (int i = 0; i < values.length; i++) {
				values[i] = ed.get(i).getName();
			}

			if (values.length > 0) {
				String selected = (String) JOptionPane.showInputDialog(this, "WÃ¤hle Checkpoint", "Checkpoint Auswahl",
						JOptionPane.WARNING_MESSAGE, null, values, values[0]);
				return selected;
			} else {
				return "";
			}
		}

	}

}
