package ch.infbr5.sentinel.client.gui.components;

import java.awt.Frame;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import ch.infbr5.sentinel.client.util.ServiceHelper;
import ch.infbr5.sentinel.client.wsgen.ConfigurationResponse;
import ch.infbr5.sentinel.client.wsgen.PersonDetails;
import ch.infbr5.sentinel.common.gui.util.SwingHelper;

public class BulkFotoImporter {

	private Frame frame;

	public BulkFotoImporter(Frame parent) {
		frame = parent;
	}

	public void importFotos() {

		// Create a file chooser
		final JFileChooser fc = new JFileChooser();
		fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

		int returnVal = fc.showOpenDialog(frame);

		if (returnVal == JFileChooser.APPROVE_OPTION) {
			File file = fc.getSelectedFile();
			if (file.isDirectory()) {
				processDir(file);
			}
		}
	}

	private void processDir(File dir) {

		StringBuffer sb = new StringBuffer();

		for (File child : dir.listFiles()) {
			if (isValidFilename(child.getName())) {

				sb.append(child.getName() + ": ");

				String ahvNr = child.getName().substring(0, 16);
				ConfigurationResponse resp = ServiceHelper.getConfigurationsService().getPersonByAhvNr(ahvNr);
				if (resp.getPersonDetails().size() == 1) {
					// eine Person gefunden
					PersonDetails p = resp.getPersonDetails().get(0);
					if (p.getImageId() != null) {
						sb.append("Ist bereist in der Datenbank vorhanden und wurde nicht importiert.\n");

					} else {
						try {
							FileInputStream fin = new FileInputStream(child);
							byte fileContent[] = new byte[(int) child.length()];
							fin.read(fileContent);
							fin.close();

							BufferedImage img = ImageIO.read(new ByteArrayInputStream(fileContent));

							if ((img.getWidth() == 300) && (img.getHeight() == 400)) {
								p.setImage(fileContent);
								ServiceHelper.getConfigurationsService().updatePerson(p);
								ServiceHelper.getSentinelService().neuerAusweis(p.getId());
								sb.append("Wurde importiert.\n");
							} else {
								// TODO
								p.setImage(fileContent);
								ServiceHelper.getConfigurationsService().updatePerson(p);
								ServiceHelper.getSentinelService().neuerAusweis(p.getId());
								sb.append("Hat die falsche Grösse (" + img.getWidth() + " x " + img.getHeight()
										+ " anstatt 300 x 400).\n");
							}
						} catch (IOException e) {
							sb.append(e.getMessage());
						}
					}
				} else {
					// Person nicht gefunden
					sb.append("Person mit dieser AVH Nummer wurde nicht gefunden.\n");
				}
			}
		}

		showReportDialog(sb.toString());
	}

	private boolean isValidFilename(String filename) {
		return filename.matches("[0-9]{3}\\.[0-9]{4}\\.[0-9]{4}\\.[0-9]{2}\\.jpg");
	}

	private void showReportDialog(String report) {
		JTextArea fieldMessage = SwingHelper.createTextArea(15, 50);
		fieldMessage.setText(report);
		fieldMessage.setEditable(false);
		JScrollPane scrollPane = new JScrollPane(fieldMessage);

		fieldMessage.setLineWrap(true);
		fieldMessage.setWrapStyleWord(true);
		scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

		JOptionPane.showMessageDialog(frame, scrollPane, "Resultat der Verarbeitung", JOptionPane.INFORMATION_MESSAGE);
	}

}
