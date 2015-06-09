package ch.infbr5.sentinel.client.gui.components.configuration;

import java.awt.BorderLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

import ch.infbr5.sentinel.client.image.ImageCropper;
import ch.infbr5.sentinel.common.gui.util.ImageLoader;

public class ImageEditorDialog extends javax.swing.JDialog {

   private static final long serialVersionUID = 1L;

   private AbstractAction abstractActionCutter;
   private AbstractAction abstractActionNewImage;
   private AbstractAction abstractActionRotateImage;
   private AbstractAction abstractActionCancel;
   private AbstractAction abstractActionSave;

   private JFileChooser fileChooser;
   private JButton imageLadenButton;
   private JButton imageSaveButton;
   private JButton imageCutButton;
   private JButton imageRotateButton;
   private JButton imageCancelButton;

   private ImageCropper cropper;
   private JPanel openCropTurnPanel;
   private JPanel saveCancelPanel;
   private JPanel imageEditorPanel;

   private BufferedImage image;

   public ImageEditorDialog(final JFrame frame, final BufferedImage image) {
      super(frame, true);
      initGui();
      setImage(image);
   }

   public BufferedImage getImage() {
      return image;
   }

   private void initGui() {
      setLayout(new BorderLayout());
      setResizable(false);
      setSize(800, 400);
      setTitle("Personen-Bild bearbeiten");
      setIconImage(ImageLoader.loadSentinelIcon());
      setLocationRelativeTo(null);
      add(getOpenCropTurnButtonPanel(), BorderLayout.NORTH);
      add(getImageEditorPanel(), BorderLayout.CENTER);
      add(getSaveCancelButtonPanel(), BorderLayout.SOUTH);
   }

   private void setImage(final BufferedImage image) {
      this.image = image;
      if (image != null) {
         imageSaveButton.setEnabled(true);
         imageCutButton.setEnabled(true);
         imageRotateButton.setEnabled(true);
      }
      initCropper(image);
   }

   private void initCropper(final BufferedImage image) {
      if (cropper != null) {
         imageEditorPanel.remove(cropper);
      }
      if (image != null) {
         cropper = new ImageCropper(image);
         imageEditorPanel.add(cropper, BorderLayout.CENTER);
         this.getContentPane().validate();
      }
   }

   private AbstractAction getAbstractActionNewImage() {
      if (abstractActionNewImage == null) {
         abstractActionNewImage = new AbstractAction("Bild laden", null) {
            private static final long serialVersionUID = 1L;

            @Override
            public void actionPerformed(final ActionEvent evt) {
               final int returnVal = getFileChooser().showOpenDialog(ImageEditorDialog.this);
               if (returnVal == JFileChooser.APPROVE_OPTION) {
                  final File file = getFileChooser().getSelectedFile();

                  try {
                     image = ImageIO.read(file);
                     setImage(image);
                  } catch (final IOException e) {
                     e.printStackTrace();
                  }
               }

            }
         };
      }

      return abstractActionNewImage;
   }

   private AbstractAction getAbstractActionCancel() {
      if (abstractActionCancel == null) {
         abstractActionCancel = new AbstractAction("Abbrechen", null) {
            private static final long serialVersionUID = 1L;

            @Override
            public void actionPerformed(final ActionEvent evt) {
               image = null;
               dispose();
            }
         };
      }

      return abstractActionCancel;
   }

   private AbstractAction getAbstractActionSave() {
      if (abstractActionSave == null) {
         abstractActionSave = new AbstractAction("OK", null) {
            private static final long serialVersionUID = 1L;

            @Override
            public void actionPerformed(final ActionEvent evt) {
               saveCroppedImage();
            }
         };
      }

      return abstractActionSave;
   }

   private void saveCroppedImage() {
      // don't set image to null
      dispose();
   }

   private AbstractAction getAbstractActionRotateImage() {
      if (abstractActionRotateImage == null) {
         abstractActionRotateImage = new AbstractAction("Drehen", null) {
            private static final long serialVersionUID = 1L;

            @Override
            public void actionPerformed(final ActionEvent evt) {
               image = rotateImage90(image);

               initCropper(image);
            }
         };
      }

      return abstractActionRotateImage;
   }

   private AbstractAction getAbstractActionCutter() {
      if (abstractActionCutter == null) {
         abstractActionCutter = new AbstractAction("Ausschneiden", null) {
            private static final long serialVersionUID = 1L;

            @Override
            public void actionPerformed(final ActionEvent evt) {
               image = cropper.getCroppedImage();
               initCropper(image);
            }
         };
      }

      return abstractActionCutter;
   }

   private JFileChooser getFileChooser() {
      if (fileChooser == null) {
         fileChooser = new JFileChooser();
         final FileFilter jpgFilter = new FileNameExtensionFilter("JPEG file", "jpg", "jpeg");
         fileChooser.setFileFilter(jpgFilter);
      }

      return fileChooser;
   }

   private JPanel getOpenCropTurnButtonPanel() {
      if (openCropTurnPanel == null) {
         openCropTurnPanel = new JPanel();
         openCropTurnPanel.add(getImageLadenButton());
         openCropTurnPanel.add(getImageRotateButton());
         openCropTurnPanel.add(getImageCutButton());

         imageCutButton.setEnabled(false);
         imageRotateButton.setEnabled(false);
      }

      return openCropTurnPanel;
   }

   private JPanel getSaveCancelButtonPanel() {
      if (saveCancelPanel == null) {
         saveCancelPanel = new JPanel();
         saveCancelPanel.add(getImageCancelButton());
         saveCancelPanel.add(getImageSaveButton());

         imageSaveButton.setEnabled(false);
      }

      return saveCancelPanel;
   }

   private JButton getImageRotateButton() {
      if (imageRotateButton == null) {
         imageRotateButton = new JButton();
         imageRotateButton.setText("Drehen");
         imageRotateButton.setAction(getAbstractActionRotateImage());
      }

      return imageRotateButton;
   }

   private JButton getImageLadenButton() {
      if (imageLadenButton == null) {
         imageLadenButton = new JButton();
         imageLadenButton.setText("Neu");
         imageLadenButton.setAction(getAbstractActionNewImage());
      }

      return imageLadenButton;
   }

   private JButton getImageCutButton() {
      if (imageCutButton == null) {
         imageCutButton = new JButton();
         imageCutButton.setText("Zuschneiden");
         imageCutButton.setAction(getAbstractActionCutter());
      }

      return imageCutButton;
   }

   private JButton getImageSaveButton() {
      if (imageSaveButton == null) {
         imageSaveButton = new JButton("OK");

         imageSaveButton.setAction(getAbstractActionSave());
      }

      return imageSaveButton;
   }

   private JButton getImageCancelButton() {
      if (imageCancelButton == null) {
         imageCancelButton = new JButton("Abbrechen");
         imageCancelButton.setAction(getAbstractActionCancel());
      }

      return imageCancelButton;
   }

   private JPanel getImageEditorPanel() {
      if (imageEditorPanel == null) {
         imageEditorPanel = new JPanel();
         final BorderLayout imageEditorPanelLayout = new BorderLayout();
         imageEditorPanel.setLayout(imageEditorPanelLayout);
      }

      return imageEditorPanel;
   }

   private static BufferedImage rotateImage90(final BufferedImage src) {
      // http://stackoverflow.com/questions/10426883/affinetransform-truncates-image
      int srcWidth = src.getWidth();
      int srcHeight = src.getHeight();

      final AffineTransform affineTransform = new AffineTransform();
      affineTransform.setToRotation(Math.toRadians(90), srcWidth / 2d, srcHeight / 2d);

      // source image rectangle
      final Point[] points = { new Point(0, 0), new Point(srcWidth, 0), new Point(srcWidth, srcHeight),
            new Point(0, srcHeight) };

      // transform to destination rectangle
      affineTransform.transform(points, 0, points, 0, 4);

      // get destination rectangle bounding box
      final Point min = new Point(points[0]);
      final Point max = new Point(points[0]);
      for (int i = 1, n = points.length; i < n; i++) {
         final Point p = points[i];
         final double pX = p.getX(), pY = p.getY();

         // update min/max x
         if (pX < min.getX()) {
            min.setLocation(pX, min.getY());
         }
         if (pX > max.getX()) {
            max.setLocation(pX, max.getY());
         }

         // update min/max y
         if (pY < min.getY()) {
            min.setLocation(min.getX(), pY);
         }
         if (pY > max.getY()) {
            max.setLocation(max.getX(), pY);
         }
      }

      // determine new width, height
      srcWidth = (int) (max.getX() - min.getX());
      srcHeight = (int) (max.getY() - min.getY());

      // determine required translation
      final double tx = min.getX();
      final double ty = min.getY();

      // append required translation
      final AffineTransform translation = new AffineTransform();
      translation.translate(-tx, -ty);
      affineTransform.preConcatenate(translation);

      final AffineTransformOp op = new AffineTransformOp(affineTransform, null);
      final BufferedImage rotatedImage = new BufferedImage(srcWidth, srcHeight, src.getType());
      op.filter(src, rotatedImage);

      return rotatedImage;
   }
}