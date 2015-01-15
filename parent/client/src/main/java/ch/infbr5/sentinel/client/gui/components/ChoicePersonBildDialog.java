package ch.infbr5.sentinel.client.gui.components;

import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import net.miginfocom.swing.MigLayout;
import ch.infbr5.sentinel.client.util.ServiceHelper;
import ch.infbr5.sentinel.client.wsgen.OperationResponse;
import ch.infbr5.sentinel.client.wsgen.PersonDetails;
import ch.infbr5.sentinel.common.gui.util.ImageLoader;
import ch.infbr5.sentinel.common.util.ImageUtil;

public class ChoicePersonBildDialog extends JDialog {

   private static final long serialVersionUID = 1L;

   private final BufferedImage oldImage;

   private final BufferedImage newImage;

   private boolean holdOldImage = true;

   private boolean discardImage = false;

   private final PersonDetails person;

   private DefaultComboBoxModel<ComboboxPersonItem> modelDropdownPersonen;

   private final JFrame parentFrame;

   private JLabel lblOldImage;

   public ChoicePersonBildDialog(final JFrame parentFrame, final BufferedImage oldImage, final BufferedImage newImage,
         final PersonDetails person) {
      super(parentFrame);
      this.parentFrame = parentFrame;
      this.oldImage = oldImage;
      this.newImage = newImage;
      this.person = person;
   }

   public void showDialog() {
      init();
      setVisible(true);
   }

   public boolean holdingOldImage() {
      return holdOldImage;
   }

   public boolean discardImage() {
      return discardImage;
   }

   public PersonDetails getPerson() {
      final ComboboxPersonItem item = (ComboboxPersonItem) modelDropdownPersonen.getSelectedItem();
      if (item == null) {
         return null;
      } else {
         return item.person;
      }
   }

   private void init() {
      setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
      setSize(500, 500);
      setTitle("Bild-Wahl " + getTitlePerson());
      setIconImage(ImageLoader.loadSentinelIcon());
      setLocationRelativeTo(null);
      setModal(true);
      setLayout(new MigLayout());

      final JButton btnOldImage = new JButton("Altes Bild behalten");
      btnOldImage.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(final ActionEvent e) {
            if (isPersonSelected()) {
               holdOldImage = true;
               setVisible(false);
               dispose();
            }
         }
      });

      final JButton btnNewImage = new JButton("Neues Bild verwenden");
      btnNewImage.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(final ActionEvent e) {
            if (isPersonSelected()) {
               holdOldImage = false;
               setVisible(false);
               dispose();
            }
         }
      });

      final JButton btnGoToNextImage = new JButton("Bild verwerfen und zum nächsten Bild gehen");
      btnGoToNextImage.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(final ActionEvent e) {
            discardImage = true;
            setVisible(false);
            dispose();
         }
      });

      add(createPersonenDropdown(), "spanx, growx, push");
      add(createPanelImage(btnOldImage, oldImage, true), "growx, w 50%, push, aligny top");
      add(createPanelImage(btnNewImage, newImage, false), "growx, w 50%, push, aligny top, wrap");
      add(btnGoToNextImage, "growx, spanx, pushx");
   }

   private boolean isPersonSelected() {
      final boolean isPersonSelected = getPerson() != null;
      if (!isPersonSelected) {
         JOptionPane.showMessageDialog(parentFrame,
               "Es muss eine Person ausgwählt werden. Ansonsten klicken Sie auf Bild verwerfen.", "Selektion ungültig",
               JOptionPane.WARNING_MESSAGE);
      }
      return isPersonSelected;
   }

   private JComboBox<ComboboxPersonItem> createPersonenDropdown() {
      final JComboBox<ComboboxPersonItem> dropdown = new JComboBox<ComboboxPersonItem>();
      modelDropdownPersonen = new DefaultComboBoxModel<>();
      modelDropdownPersonen.addElement(null);
      final OperationResponse response = ServiceHelper.getSentinelService().getAllePersonen();
      for (final PersonDetails p : response.getPersonDetails()) {
         final ComboboxPersonItem item = new ComboboxPersonItem();
         item.person = p;
         modelDropdownPersonen.addElement(item);
      }
      dropdown.setModel(modelDropdownPersonen);

      if (person != null) {
         for (int i = 0; i < modelDropdownPersonen.getSize(); i++) {
            if (modelDropdownPersonen.getElementAt(i) != null) {
               if (person.getId().equals(modelDropdownPersonen.getElementAt(i).person.getId())) {
                  modelDropdownPersonen.setSelectedItem(modelDropdownPersonen.getElementAt(i));
               }
            }
         }
      } else {
         modelDropdownPersonen.setSelectedItem(null);
      }

      dropdown.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(final ActionEvent e) {
            final Object item = modelDropdownPersonen.getSelectedItem();
            BufferedImage loadImage = null;
            if (item != null) {
               final ComboboxPersonItem i = (ComboboxPersonItem) item;
               loadImage = ch.infbr5.sentinel.client.util.ImageLoader.loadImage(i.person.getAhvNr());
            }
            if (loadImage == null) {
               loadImage = ImageLoader.loadNobodyImage();
            }
            lblOldImage.setIcon(new ImageIcon(ImageUtil.scaleImage(loadImage, 200, 300)));
         }
      });

      return dropdown;
   }

   private JPanel createPanelImage(final JButton btn, final BufferedImage image, final boolean oldImage) {
      final JPanel panel = new JPanel(new MigLayout());

      panel.add(btn, "growx, pushx, wrap");

      final Image resizedImage = ImageUtil.scaleImage(image, 200, 300);

      final JLabel lbl = new JLabel(new ImageIcon(resizedImage));
      if (oldImage) {
         lblOldImage = lbl;
      }
      panel.add(lbl, "alignx center, push");

      return panel;
   }

   private String getTitlePerson() {
      return "Personen Bild Auswahl";
   }

   class ComboboxPersonItem {

      private PersonDetails person;

      @Override
      public String toString() {
         if (person != null) {
            return person.getName() + " " + person.getVorname() + " (" + person.getGrad() + ")";
         }
         return "";
      }

   }

}
