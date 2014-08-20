package ch.infbr5.sentinel.client.gui.components.importer;

import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import net.miginfocom.swing.MigLayout;
import ch.infbr5.sentinel.client.gui.components.FileUpAndDownload;
import ch.infbr5.sentinel.client.util.ServiceHelper;
import ch.infbr5.sentinel.client.wsgen.StringArray;

import com.google.common.io.Files;

public class WorkflowStepFile extends WorkflowStep {

	private JPanel panel;

	private JButton btnOpenFile;

	private JLabel lblInfo;

	private JLabel lblInfoArt;

	private JLabel lblInfoBestand;

	private JLabel lblInfoEinrueckung;

	private JLabel lblFilename;

	private String sessionKey = null;

	private String currentFilename;

	private String lastUploadedFilename;

	private JRadioButton bestandedsImport;

	private JRadioButton einrueckungsImport;

	private String[] allowedExtensions;

	public WorkflowStepFile(Frame parent, WorkflowData data, WorkflowInterceptor interceptor) {
		super(parent, data, interceptor);
	}

	@Override
	public String getName() {
		return "Datei";
	}

	@Override
	public String getUserInfo() {
		return "Wählen Sie die zu importierende Datei aus. Es sind nur CSV, XLS und XLSX Dateien erlaubt. Die Datei muss eine Überschrift haben, 7 Spalten besitzen und Daten beinhalten.";
	}

	@Override
	public JPanel getPanel() {
		if (panel == null) {
			panel = new JPanel(new MigLayout());

			// Extensions
			StringArray array = ServiceHelper.getPersonenImporterService().getSupportedExtensions();
			allowedExtensions = array.getItem().toArray(new String[array.getItem().size()]);

			lblInfo = new JLabel("<html>Nachfolgend können Sie eine Datei mit <b>Personendaten</b> auswählen:</html>");
			lblInfoArt = new JLabel("<html>Wählen Sie die Importierungs-Art aus.</html>");
			lblInfoBestand = new JLabel("<html>Betrachtet die Personen in der Datei als kompletten Bestand der Truppe. Bestehende Personen im System, welche in der Datei nicht mehr vorhanden sind, werden der Einheit 'ArchivEinheit' zugewiesen.</html>");
			lblInfoEinrueckung = new JLabel("<html>Die Personen in der Datei sind 'nur' Personen, welche einrücken werden. Personen mit unbekannten Einheiten werden der Einheit 'GastEinheit' zugewiesen (Personen, welche einen Gast-WK machen).</html>");
			lblFilename = new JLabel("<html><b>Datei:</b> keine Datei ausgewählt</html>");

			btnOpenFile = new JButton("Datei auswählen");
			btnOpenFile.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					File file = new FileUpAndDownload(getParent()).showJFileChooser(allowedExtensions);
					if (file != null && file.exists()) {
						currentFilename = file.getAbsolutePath();
						updateLblFilename();
						validateDialog();
					}
				}
			});

			bestandedsImport = new JRadioButton();
			bestandedsImport.setText("Bestandesliste");
			einrueckungsImport = new JRadioButton();
			einrueckungsImport.setText("Einrückungsliste");

			einrueckungsImport.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					validateDialog();
				}
			});
			bestandedsImport.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					validateDialog();
				}
			});

			ButtonGroup group = new ButtonGroup();
			group.add(bestandedsImport);
			group.add(einrueckungsImport);

			panel.add(lblInfoArt, "wrap, spanx, gaptop 10");
			panel.add(bestandedsImport, "wrap, gaptop 10");
			panel.add(lblInfoBestand, "wrap, gaptop 10");
			panel.add(einrueckungsImport, "wrap, gaptop 10");
			panel.add(lblInfoEinrueckung, "wrap, gaptop 10");

			panel.add(lblInfo, "wrap, spanx, gaptop 10");
			panel.add(btnOpenFile, "wrap, spanx, gaptop 10");
			panel.add(lblFilename, "wrap, spanx, gaptop 10");
		}
		return panel;
	}

	@Override
	public void init() {
		// Falls Zurück
		if (sessionKey != null) {
			validateDialog();
			// btnOpenFile.setEnabled(false);
		}
	}

	@Override
	public void finishReturn() {
		finishNext();
	}

	@Override
	public void finishNext() {
		if (sessionKey != null) {
			abort();
		}

		lastUploadedFilename = currentFilename;
		getData().setKompletterBestand(bestandedsImport.isSelected());
		byte[] byteData = loadFile(currentFilename);
		sessionKey = ServiceHelper.getPersonenImporterService().initiatImport((new File(currentFilename)).getName(), byteData, getData().isKompletterBestand());
		getData().setSessionKey(sessionKey);
	}

	@Override
	public void abort() {
		if (sessionKey != null) {
			try {
				ServiceHelper.getPersonenImporterService().abortImport(sessionKey);
			} catch (RuntimeException e) {
				e.printStackTrace();
			}
		}
	}

	private byte[] loadFile(String filename) {
		try {
			return Files.toByteArray(new File(filename));
		} catch (IOException e) {
			// TODO Exception
			e.printStackTrace();
		}
		return null;
	}

	private void updateLblFilename() {
		lblFilename.setText("<html><b>Datei:</b> " + currentFilename + "</html>");
	}

	private void validateDialog() {
		if (currentFilename != null && new File(currentFilename).exists() && (einrueckungsImport.isSelected() || bestandedsImport.isSelected())) {
			getInterceptor().activateNext();
		} else {
			getInterceptor().deactivateNext();
		}
	}

}
