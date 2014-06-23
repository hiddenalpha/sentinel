package ch.infbr5.sentinel.client.gui.components.importer;

import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import net.miginfocom.swing.MigLayout;
import ch.infbr5.sentinel.client.gui.components.FileUpAndDownload;
import ch.infbr5.sentinel.client.util.ServiceHelper;
import ch.infbr5.sentinel.client.wsgen.StringArray;

import com.google.common.io.Files;

public class WorkflowStepFile extends WorkflowStep {

	private JPanel panel;
	
	private JButton btnOpenFile;
	
	private JLabel lblInfo;
	
	private JLabel lblFilename;
	
	private String sessionKey = null;
	
	private String currentFilename;
	
	private String lastUploadedFilename;
	
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
			lblFilename = new JLabel("<html><b>Datei:</b> keine Datei ausgewählt</html>");
			
			btnOpenFile = new JButton("Datei auswählen");
			btnOpenFile.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					File file = new FileUpAndDownload(getParent()).showJFileChooser(allowedExtensions);
					if (file != null && file.exists()) {
						currentFilename = file.getAbsolutePath();
						updateLblFilename();
						checkFile();
					}
				}
			});
			
			panel.add(lblInfo, "wrap");
			panel.add(btnOpenFile, "wrap, gaptop 10");
			panel.add(lblFilename, "wrap, gaptop 10");
		}
		return panel;
	}
	
	@Override
	public void init() {
		// Falls Zurück
		if (sessionKey != null) {
			checkFile();
			// btnOpenFile.setEnabled(false);
		}
	}

	@Override
	public void finishReturn() {
		finishNext();
	}
	
	@Override
	public void finishNext() {
		if (lastUploadedFilename == null || !lastUploadedFilename.equals(currentFilename)) {
			lastUploadedFilename = currentFilename;
			byte[] byteData = loadFile(currentFilename);
			sessionKey = ServiceHelper.getPersonenImporterService().initiatImport((new File(currentFilename)).getName(), byteData, getData().isKompletterBestand());
			getData().setSessionKey(sessionKey);
		}
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
	
	private void checkFile() {
		if (currentFilename != null && new File(currentFilename).exists()) {
			getInterceptor().activateNext();
		} else {
			getInterceptor().deactivateNext();
		}
	}
	
}
