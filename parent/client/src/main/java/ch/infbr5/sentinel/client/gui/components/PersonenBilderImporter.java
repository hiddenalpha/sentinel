package ch.infbr5.sentinel.client.gui.components;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.Border;

import net.miginfocom.swing.MigLayout;
import ch.infbr5.sentinel.client.config.ConfigurationLocalHelper;
import ch.infbr5.sentinel.client.gui.components.configuration.ImageEditorDialog;
import ch.infbr5.sentinel.client.util.AhvNrParser;
import ch.infbr5.sentinel.client.util.ServiceHelper;
import ch.infbr5.sentinel.client.wsgen.ConfigurationResponse;
import ch.infbr5.sentinel.client.wsgen.PersonDetails;
import ch.infbr5.sentinel.common.gui.util.ImageLoader;
import ch.infbr5.sentinel.common.gui.util.SwingHelper;

public class PersonenBilderImporter {

   private final JFrame parentFrame;

   private JTextField txtFolderPath;

   private JButton btnSaveImages;

   private JButton btnSaveImagesAndCreateAusweise;

   private JTextArea resultTextArea;

   private JDialog dialog;

   public PersonenBilderImporter(final JFrame parentFrame) {
      this.parentFrame = parentFrame;
   }

   public void showDialog() {
      dialog = createDialog();
      dialog.setVisible(true);
   }

   private JDialog createDialog() {
      final JDialog dialog = new JDialog(parentFrame);
      dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
      dialog.setSize(800, 600);
      dialog.setTitle("Personenbilder importieren");
      dialog.setIconImage(ImageLoader.loadSentinelIcon());
      dialog.setLocationRelativeTo(null);
      dialog.setModal(true);
      dialog.setLayout(new MigLayout());

      dialog.add(createLabelInfoText(), "growx, wrap");
      dialog.add(createPanelChooseFolder(), "growx, wrap");
      dialog.add(createPanelStartImport(), "growx, wrap");
      dialog.add(createResultPane(), "growx, growy, pushy, wrap");
      dialog.add(createCloseButton(), "align right");

      return dialog;
   }

   private JButton createCloseButton() {
      final JButton btn = new JButton("Schliessen");
      btn.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(final ActionEvent e) {
            dialog.setVisible(false);
            dialog.dispose();
         }
      });
      return btn;
   }

   private JPanel createPanelChooseFolder() {
      final JPanel panel = new JPanel();
      panel.setLayout(new MigLayout());
      panel.add(createTxtFolderPath(), "w 80%, h 26");
      panel.add(createButtonFolderPath(), "w 20%");
      return panel;
   }

   private JPanel createPanelStartImport() {
      final JPanel panel = new JPanel();
      panel.setLayout(new MigLayout());
      panel.add(createButtonSaveImages(), "w 50%");
      panel.add(createButtonSaveImagesAndCreateAusweise(), "w 50%");
      return panel;
   }

   private JLabel createLabelInfoText() {
      final JLabel lbl = new JLabel();
      lbl.setText("<html>Nachfolgend können Personenbilder importiert werden. "
            + "Die Bilder müssen in einem <b>Verzeichnis</b> liegen und von der Dateieindung <b>jpg</b> oder <b>jpeg</b> sein. "
            + "Damit erkennt werden kann, welches Bild zu welcher Person gehört muss die <b>AHV</b> im Dateinamen enthalten sein. "
            + "Falls die AHV Nr nicht vorhanden ist, muss die Person im Dialog selektiert werden."
            + "Sie werden im Verlauf des Imports aufgefordert die Bilder zu <b>akzeptieren</b> und anschliessend <b>zuzuschneiden</b>.<br /><br />"
            + "<b>Beispiele:</b><br /> thomas-75648günter48464622gestern.jpg > 756.4848.4646.22 <br/>"
            + "thomas-756.4848.4646.22gestern.jpg > 756.4848.4646.22 <br />"
            + "thomas-75648günter48464622gestern.jpg > 756.4848.4646.22</html>");
      lbl.setBorder(createBorder());
      return lbl;
   }

   private JTextField createTxtFolderPath() {
      txtFolderPath = new JTextField("Kein Pfad ausgewählt");
      txtFolderPath.setEnabled(false);
      return txtFolderPath;
   }

   private JButton createButtonFolderPath() {
      final JButton btn = new JButton("Pfad auswählen");
      btn.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(final ActionEvent e) {
            final JFileChooser fileChooser = new JFileChooser();
            fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            final String lastPath = ConfigurationLocalHelper.getConfig().getFileChooserLastPath();
            if (lastPath != null) {
               fileChooser.setCurrentDirectory(new File(lastPath));
            }

            final int option = fileChooser.showOpenDialog(parentFrame);

            if (option == JFileChooser.APPROVE_OPTION) {
               final File file = fileChooser.getSelectedFile();
               if (file != null) {
                  ConfigurationLocalHelper.getConfig().setFileChooserLastPath(file.getAbsolutePath());
                  if (file.isDirectory()) {
                     txtFolderPath.setText(file.getAbsolutePath());
                     btnSaveImages.setEnabled(true);
                     btnSaveImagesAndCreateAusweise.setEnabled(true);
                  } else {
                     txtFolderPath.setText("Kein Pfad ausgewählt");
                     btnSaveImages.setEnabled(false);
                     btnSaveImagesAndCreateAusweise.setEnabled(false);
                  }
               }

            }

         }
      });
      return btn;
   }

   private JButton createButtonSaveImages() {
      btnSaveImages = new JButton("Bilder speichern");
      btnSaveImages.setEnabled(false);
      btnSaveImages.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(final ActionEvent e) {
            processDir(new File(txtFolderPath.getText()), false);
         }
      });
      return btnSaveImages;
   }

   private JButton createButtonSaveImagesAndCreateAusweise() {
      btnSaveImagesAndCreateAusweise = new JButton("Bilder speichern und Ausweise erstellen");
      btnSaveImagesAndCreateAusweise.setEnabled(false);
      btnSaveImagesAndCreateAusweise.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(final ActionEvent e) {
            processDir(new File(txtFolderPath.getText()), true);
         }
      });
      return btnSaveImagesAndCreateAusweise;
   }

   private JScrollPane createResultPane() {
      resultTextArea = SwingHelper.createTextArea(15, 50);
      resultTextArea.setEditable(false);
      final JScrollPane scrollPane = new JScrollPane(resultTextArea);
      resultTextArea.setLineWrap(true);
      resultTextArea.setWrapStyleWord(true);
      scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
      return scrollPane;
   }

   private Border createBorder() {
      return BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(Color.black),
            BorderFactory.createEmptyBorder(10, 10, 10, 10));
   }

   private void processDir(final File dir, final boolean createAusweise) {
      final FilenameFilter fileFilter = new FilenameFilter() {
         @Override
         public boolean accept(final File dir, String name) {
            name = name.toLowerCase();
            return (name.endsWith(".jpg") || name.endsWith(".jpeg"));
         }
      };

      final AhvNrParser parser = new AhvNrParser();

      for (final File file : dir.listFiles(fileFilter)) {
         final String filename = file.getName();
         final List<String> nrs = parser.getPossibleAHVNrsFromFilename(filename);

         addMessage("Datei: " + filename);

         PersonDetails person = null;
         if (!nrs.isEmpty()) {
            String ahvNr = null;
            ConfigurationResponse response = null;
            for (final String nr : nrs) {
               response = ServiceHelper.getConfigurationsService().getPersonByAhvNr(nr);
               if (response.getPersonDetails().size() > 0) {
                  ahvNr = nr;
                  break;
               }
            }
            if (ahvNr != null && response != null) {
               addMessage("Die AHV Nr wurde erkannt: " + ahvNr);
               person = response.getPersonDetails().get(0);
            } else {
               addMessage("Es wurde keine Person mit AHV Nr " + ahvNr + "gefunden. Person muss selektiert werden..");
            }
         } else {
            addMessage("Im Dateinamen wurde keine AHV Nr gefunden. Person muss selektiert werden.");
         }

         try {
            // Bild laden
            BufferedImage newImage = ImageIO.read(file);

            BufferedImage oldImage = null;
            if (person == null || person.getImageId() == null) {
               oldImage = ImageLoader.loadNobodyImage();
            } else {
               oldImage = ch.infbr5.sentinel.client.image.ImageLoader.loadImage(person.getImageId());
            }

            final ChoicePersonBildDialog choicer = new ChoicePersonBildDialog(parentFrame, oldImage, newImage, person);
            choicer.showDialog();

            if (!choicer.discardImage()) {
               person = choicer.getPerson();
               if (person != null) {

                  boolean holdOldImage = false;
                  if (choicer.holdingOldImage()) {
                     addMessage("Datei wurde nicht importiert, da die Option 'Altes Bild behalten' gewählt wurde.");
                     holdOldImage = true;
                  } else {
                     addMessage("Die Option 'Neues Bild verwenden' wurde gewählt.");
                  }

                  if (!holdOldImage) {
                     // Bild zuschneiden
                     final ImageEditorDialog imageEditorDialog = new ImageEditorDialog(parentFrame, newImage);
                     imageEditorDialog.setVisible(true);
                     newImage = imageEditorDialog.getImage();

                     if (newImage == null) {
                        addMessage("Bild wurde nicht import, da beim Zuschneiden abgebrochen wurde.");
                     } else {
                        // Bild speichern
                        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        ImageIO.write(newImage, "jpg", baos);
                        baos.flush();
                        final byte fileContent[] = baos.toByteArray();
                        person.setImage(fileContent);
                        ServiceHelper.getConfigurationsService().updatePerson(person);

                        addMessage("Datei wurde importiert.");
                        if (createAusweise) {
                           ServiceHelper.getSentinelService().neuerAusweis(person.getId());
                           addMessage("Zudem wurde ein neuer Ausweis erzeugt.");
                        }
                     }
                  }
               } else {
                  addMessage("Die Datei wurde nicht importiert. Es konnte keine Person zugewiesen werden.");
               }
            } else {
               addMessage("Die Datei wurde nicht importiert. Es wurde die Option Bild verwerfen gewählt.");
            }

         } catch (final IOException e) {
            addMessage("Die Datei wurde nicht importiert, da ein Fehler aufgetreten ist: " + e.getMessage());
         }

         addMessage("");
      }

      addMessage("Der Import wurde abgeschlossen.");
   }

   public void addMessage(final String msg) {
      resultTextArea.setText(resultTextArea.getText() + msg + "\r\n");
      resultTextArea.repaint();
   }

}
