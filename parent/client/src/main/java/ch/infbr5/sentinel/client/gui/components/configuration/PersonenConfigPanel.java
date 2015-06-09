package ch.infbr5.sentinel.client.gui.components.configuration;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import net.miginfocom.swing.MigLayout;
import ch.infbr5.sentinel.client.image.ImageLoader;
import ch.infbr5.sentinel.client.util.EinheitDetailsClient;
import ch.infbr5.sentinel.client.util.ServiceHelper;
import ch.infbr5.sentinel.client.util.XMLGregorianCalendarConverter;
import ch.infbr5.sentinel.client.wsgen.ConfigurationResponse;
import ch.infbr5.sentinel.client.wsgen.EinheitDetails;
import ch.infbr5.sentinel.client.wsgen.OperationResponse;
import ch.infbr5.sentinel.client.wsgen.OperationResponseStatus;
import ch.infbr5.sentinel.client.wsgen.PersonDetails;
import ch.infbr5.sentinel.common.gui.util.SwingHelper;
import ch.infbr5.sentinel.common.util.DateFormater;

public class PersonenConfigPanel extends AbstractAdminOverviewPanel<PersonDetails> {

   public static final String BUTTON_NEUER_AUSWEIS = "BUTTON_NEUER_AUSWEIS";

   private static final long serialVersionUID = 1L;

   private static final int IMAGE_WIDTH = 135;
   private static final int IMAGE_HEIGHT = 180;

   private final JFrame parentFrame;

   public PersonenConfigPanel(final JFrame parentFrame) {
      this.parentFrame = parentFrame;
      setDefaultSort(1, true);
   }

   @Override
   protected AbstractAdminTableModel<PersonDetails> getTableModel() {
      return new MyTableModel();
   }

   public class MyTableModel extends AbstractAdminTableModel<PersonDetails> {

      private static final long serialVersionUID = 1L;

      private final String[] headerNames = { "Grad", "Name", "Vorname", "Funktion", "Ausweis", "Einheit" };

      @Override
      public Object getValueAt(final int rowIndex, final int columnIndex) {
         if (columnIndex == 0) {
            return getDataRecord(rowIndex).getGrad();
         } else if (columnIndex == 1) {
            return getDataRecord(rowIndex).getName();
         } else if (columnIndex == 2) {
            return getDataRecord(rowIndex).getVorname();
         } else if (columnIndex == 3) {
            return getDataRecord(rowIndex).getFunktion();
         } else if (columnIndex == 4) {
            return getDataRecord(rowIndex).getBarcode();
         } else if (columnIndex == 5) {
            return getDataRecord(rowIndex).getEinheitText();
         }

         return null;
      }

      @Override
      public PersonDetails getNewDataRecord() {
         final PersonDetails detail = new PersonDetails();
         detail.setGrad("");
         detail.setName("");
         detail.setVorname("");
         detail.setBarcode("");
         detail.setEinheitId(-1l);
         detail.setFunktion("");
         detail.setImageId("");
         detail.setAhvNr("");
         detail.setImage(null);

         return detail;
      }

      @Override
      public void removeBackendObject(final PersonDetails object) {
         ServiceHelper.getConfigurationsService().removePerson(object.getId());
      }

      @Override
      public void updateBackendObject(final PersonDetails object) {
         ServiceHelper.getConfigurationsService().updatePerson(object);
      }

      @Override
      public List<PersonDetails> getBackendObjects() {
         final ConfigurationResponse response = ServiceHelper.getConfigurationsService().getPersonen();
         return response.getPersonDetails();
      }

      @Override
      public String[] getHeaderNames() {
         return headerNames;
      }
   }

   @Override
   protected AbstractAdminDetailPanel<PersonDetails> getDetailPanel() {
      return new MyDetailPanel();
   }

   public class MyDetailPanel extends AbstractAdminDetailPanel<PersonDetails> {

      private static final long serialVersionUID = 1L;

      private final JTextField fieldAhvNr;
      private final JTextField fieldFunktion;
      private final JTextField fieldGeburtsdatum;
      private final JComboBox<String> comboBoxGrad;
      private final JTextField fieldName;
      private final JTextField fieldVorname;
      private final JComboBox<EinheitDetailsClient> comboBoxEinheit;

      private JButton neuerAusweisButton;
      private JButton ausweisSperrenButton;
      private AbstractAction abstractActionAusweisSperren;
      private AbstractAction abstractActionNeuerAusweis;

      private JLabel fotoLabel;
      private BufferedImage noFotoIcon;

      public MyDetailPanel() {
         setLayout(new MigLayout("inset 20"));

         SwingHelper.addSeparator(this, "Person");

         final List<String> gradValues = ServiceHelper.getConfigurationsService().getGradValues().getItem();
         comboBoxGrad = createDropdown("Grad", gradValues.toArray(new String[0]));
         fieldName = createField("Name");
         fieldVorname = createField("Vorname");

         comboBoxEinheit = createDropdown("Einheit");

         fieldGeburtsdatum = createField("Geburtsdatum", "\\d{2}\\.\\d{2}\\.\\d{4}");
         fieldFunktion = createField("Funktion", ".{2,}.*");
         fieldAhvNr = createField("AhvNr", "[ahvnr]");

         SwingHelper.addSeparator(this, "Ausweis");

         final JPanel ausweisPanel = new JPanel(new MigLayout("", "[][]", "[]"));
         createFotoLabel();

         ausweisPanel.add(fotoLabel, "spany");

         final JPanel ausweisButtonPanel = new JPanel(new MigLayout("", "[]", "[]"));
         ausweisButtonPanel.add(getAusweisSperrenButton(), "wrap, growx, aligny top");
         ausweisButtonPanel.add(getNeuerAusweisButton(), "growx, aligny top");

         ausweisPanel.add(ausweisButtonPanel, "push, growy");

         add(ausweisPanel, "spanx, push, growx, aligny top");
      }

      private JButton getAusweisSperrenButton() {
         if (ausweisSperrenButton == null) {
            ausweisSperrenButton = new JButton("Ausweis sperren");

            ausweisSperrenButton.setAction(getAbstractActionAusweisSperren());
         }

         return ausweisSperrenButton;
      }

      private AbstractAction getAbstractActionAusweisSperren() {
         if (abstractActionAusweisSperren == null) {
            abstractActionAusweisSperren = new AbstractAction("Ausweis sperren", null) {
               private static final long serialVersionUID = 1L;

               @Override
               public void actionPerformed(final ActionEvent evt) {
                  ausweisSperren();
               }
            };
         }

         return abstractActionAusweisSperren;
      }

      private void ausweisSperren() {
         final OperationResponse operationResponse = ServiceHelper.getSentinelService()
               .invalidateAusweise(data.getId());
         if (operationResponse.getStatus() == OperationResponseStatus.SUCESS) {
            JOptionPane.showMessageDialog(this, "Ausweis gesperrt");
         } else {
            JOptionPane.showMessageDialog(this, "Konnte Ausweis nicht sperren");
         }
      }

      private JButton getNeuerAusweisButton() {
         if (neuerAusweisButton == null) {
            neuerAusweisButton = new JButton("Neuer Ausweis");
            neuerAusweisButton.setName(BUTTON_NEUER_AUSWEIS);
            neuerAusweisButton.setAction(getAbstractActionNeuerAusweis());
         }

         return neuerAusweisButton;
      }

      private AbstractAction getAbstractActionNeuerAusweis() {
         if (abstractActionNeuerAusweis == null) {
            abstractActionNeuerAusweis = new AbstractAction("Neuer Ausweis", null) {
               private static final long serialVersionUID = 1L;

               @Override
               public void actionPerformed(final ActionEvent evt) {
                  neuerAusweis();
               }
            };
         }

         return abstractActionNeuerAusweis;
      }

      private void neuerAusweis() {
         final OperationResponse operationResponse = ServiceHelper.getSentinelService().neuerAusweis(data.getId());
         if (operationResponse.getStatus() == OperationResponseStatus.SUCESS) {
            JOptionPane.showMessageDialog(this, "Neuer Ausweis wird erstellt");
         } else {
            JOptionPane.showMessageDialog(this, "Konnte Ausweiserstellung nicht in Auftrag geben");
         }
      }

      private BufferedImage getCurrentImage() {
         if (data.getImage() == null) {
            // Current Image is not edited so we try to get
            return ImageLoader.loadImage(data.getAhvNr());
         }
         try {
            return ImageIO.read(new ByteArrayInputStream(data.getImage()));
         } catch (final IOException e) {
            return null;
         }
      }

      private void setCurrentImage(final BufferedImage image) {
         if (image == null) {
            data.setImage(null);
         } else {
            data.setImage(convertBufferedImageToByte(image));
         }
      }

      private void openEditImagePanel() {
         final ImageEditorDialog imageEditor = new ImageEditorDialog(parentFrame, getCurrentImage());
         imageEditor.setVisible(true);
         setCurrentImage(imageEditor.getImage());
         refreshImage();
      }

      private List<EinheitDetailsClient> copyEinheitModel(final List<EinheitDetails> einheitDetails) {
         final List<EinheitDetailsClient> einheiten = new ArrayList<EinheitDetailsClient>();
         for (final EinheitDetails einheit : einheitDetails) {
            einheiten.add(new EinheitDetailsClient(einheit.getId(), einheit.getName()));
         }

         return einheiten;
      }

      private JComboBox<EinheitDetailsClient> createDropdown(final String caption) {
         final JComboBox<EinheitDetailsClient> combo = new JComboBox<EinheitDetailsClient>();

         addComboBox(caption, combo);

         return combo;
      }

      private JComboBox<String> createDropdown(final String caption, final String[] values) {
         final JComboBox<String> combo = new JComboBox<String>(values);
         addComboBox(caption, combo);
         return combo;
      }

      private void addComboBox(final String caption, final JComboBox<?> combo) {
         add(SwingHelper.createLabel(caption), "gap para");
         add(combo, "span, growx");
         combo.setEditable(false);
      }

      private void reloadComboBoxEinheit() {
         comboBoxEinheit.removeAllItems();

         final ConfigurationResponse response = ServiceHelper.getConfigurationsService().getEinheiten();
         final List<EinheitDetailsClient> einheitDetails = copyEinheitModel(response.getEinheitDetails());
         for (final EinheitDetailsClient einheit : einheitDetails) {
            comboBoxEinheit.addItem(einheit);
         }
      }

      private void createFotoLabel() {
         noFotoIcon = ch.infbr5.sentinel.common.gui.util.ImageLoader.loadNobodyImage();

         fotoLabel = new JLabel(new ImageIcon(this.noFotoIcon));
         fotoLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(final MouseEvent arg0) {
               if (fotoLabel.isEnabled()) {
                  openEditImagePanel();
               }
            }
         });
      }

      @Override
      public void getFieldValues() {
         data.setGrad(comboBoxGrad.getItemAt(comboBoxGrad.getSelectedIndex()));
         data.setName(fieldName.getText());
         data.setVorname(fieldVorname.getText());

         data.setEinheitId(comboBoxEinheit.getItemAt(comboBoxEinheit.getSelectedIndex()).getId());

         final SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy", Locale.GERMAN);
         data.setGeburtsdatum(XMLGregorianCalendarConverter.stringToXMLGregorianCalendar(fieldGeburtsdatum.getText(),
               sdf));

         data.setFunktion(fieldFunktion.getText());
         data.setAhvNr(fieldAhvNr.getText());
      }

      @Override
      public void setFieldValues() {
         fieldName.setText(data.getName());
         fieldVorname.setText(data.getVorname());
         fieldGeburtsdatum.setText(DateFormater.formatToDate(data.getGeburtsdatum()));
         fieldFunktion.setText(data.getFunktion());
         fieldAhvNr.setText(data.getAhvNr());
         comboBoxGrad.setSelectedItem(data.getGrad());

         reloadComboBoxEinheit();
         for (int i = 0; i < comboBoxEinheit.getItemCount(); ++i) {
            final Long comboBoxId = comboBoxEinheit.getItemAt(i).getId();
            if (comboBoxId != null && comboBoxId.equals(data.getEinheitId())) {
               comboBoxEinheit.setSelectedIndex(i);
               break;
            }
         }

         refreshImage();
      }

      public void refreshImage() {
         BufferedImage image = getCurrentImage();
         if (image == null) {
            image = ImageLoader.loadImage(data.getImageId());
         }
         if (image == null) {
            image = noFotoIcon;
         }
         fotoLabel.setIcon(new ImageIcon(scaleImage(image)));
      }

      private Image scaleImage(final BufferedImage image) {
         return image.getScaledInstance(IMAGE_WIDTH, IMAGE_HEIGHT, 0);
      }

      @Override
      public void clearFieldValues() {
         comboBoxGrad.setSelectedIndex(0);
         fieldName.setText("");
         fieldVorname.setText("");
         comboBoxEinheit.setSelectedIndex(0);
         fieldGeburtsdatum.setText("");
         fieldFunktion.setText("");
         fieldAhvNr.setText("");
         fotoLabel.setIcon(new ImageIcon(noFotoIcon));
      }

      @Override
      public void setEditable(final boolean isEditable) {
         fotoLabel.setEnabled(isEditable);
         comboBoxGrad.setEnabled(isEditable);
         fieldName.setEditable(isEditable);
         fieldVorname.setEditable(isEditable);
         comboBoxEinheit.setEnabled(isEditable);
         fieldGeburtsdatum.setEditable(isEditable);
         fieldFunktion.setEditable(isEditable);
         fieldAhvNr.setEditable(isEditable);
         ausweisSperrenButton.setEnabled(isEditable);
         neuerAusweisButton.setEnabled(isEditable);
      }

      private byte[] convertBufferedImageToByte(BufferedImage image) {

         final int DEST_WIDTH = 300;
         final int DEST_HEIGHT = 400;

         if (image == null) {
            return null;
         }

         final ByteArrayOutputStream baos = new ByteArrayOutputStream();
         byte[] resultImageAsRawBytes;

         if ((image.getWidth() > DEST_WIDTH) || (image.getHeight() > DEST_HEIGHT)) {
            final BufferedImage dest = new BufferedImage(DEST_WIDTH, DEST_HEIGHT, BufferedImage.TYPE_INT_RGB);
            final Graphics2D g = dest.createGraphics();
            final AffineTransform at = AffineTransform.getScaleInstance((double) DEST_WIDTH / image.getWidth(),
                  (double) DEST_HEIGHT / image.getHeight());
            g.drawRenderedImage(image, at);
            image = dest;
         }

         try {
            ImageIO.write(image, "jpg", baos);
            baos.flush();
            resultImageAsRawBytes = baos.toByteArray();
            baos.close();
            return resultImageAsRawBytes;
         } catch (final IOException ex) {
            ex.printStackTrace();
         }
         return null;

      }
   }
}
