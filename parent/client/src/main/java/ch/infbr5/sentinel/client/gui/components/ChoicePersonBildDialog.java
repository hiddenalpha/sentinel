package ch.infbr5.sentinel.client.gui.components;

import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import net.miginfocom.swing.MigLayout;
import ch.infbr5.sentinel.common.gui.util.ImageLoader;
import ch.infbr5.sentinel.common.util.ImageUtil;

public class ChoicePersonBildDialog extends JDialog {

   private static final long serialVersionUID = 1L;

   private final BufferedImage oldImage;

   private final BufferedImage newImage;

   private boolean holdOldImage = true;

   private final String title;

   public ChoicePersonBildDialog(final JFrame parentFrame, final BufferedImage oldImage, final BufferedImage newImage,
         final String title) {
      super(parentFrame);
      this.oldImage = oldImage;
      this.newImage = newImage;
      this.title = title;
   }

   public void showDialog() {
      init();
      setVisible(true);
   }

   public boolean holdingOldImage() {
      return holdOldImage;
   }

   private void init() {
      setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
      setSize(500, 400);
      setTitle("Bild-Wahl " + this.title);
      setIconImage(ImageLoader.loadSentinelIcon());
      setLocationRelativeTo(null);
      setModal(true);
      setLayout(new MigLayout());

      final JButton btnOldImage = new JButton("Altes Bild behalten");
      btnOldImage.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(final ActionEvent e) {
            holdOldImage = true;
            setVisible(false);
            dispose();
         }
      });

      final JButton btnNewImage = new JButton("Neues Bild verwenden");
      btnNewImage.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(final ActionEvent e) {
            holdOldImage = false;
            setVisible(false);
            dispose();
         }
      });

      add(createPanelImage(btnOldImage, oldImage), "growx, w 50%, push, aligny top");
      add(createPanelImage(btnNewImage, newImage), "growx, w 50%, push, aligny top");
   }

   private JPanel createPanelImage(final JButton btn, final BufferedImage image) {
      final JPanel panel = new JPanel(new MigLayout());

      panel.add(btn, "growx, pushx, wrap");

      final Image resizedImage = ImageUtil.scaleImage(image, 200, 300);

      final JLabel lbl = new JLabel(new ImageIcon(resizedImage));
      panel.add(lbl, "alignx center, push");

      return panel;
   }
}
