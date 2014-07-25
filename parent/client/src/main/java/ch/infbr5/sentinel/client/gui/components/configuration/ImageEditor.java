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

import ch.infbr5.sentinel.client.util.ImageCropper;

/**
 * This code was edited or generated using CloudGarden's Jigloo SWT/Swing GUI
 * Builder, which is free for non-commercial use. If Jigloo is being used
 * commercially (ie, by a corporation, company or business for any purpose
 * whatever) then you should purchase a license for each developer using Jigloo.
 * Please visit www.cloudgarden.com for details. Use of Jigloo implies
 * acceptance of these licensing terms. A COMMERCIAL LICENSE HAS NOT BEEN
 * PURCHASED FOR THIS MACHINE, SO JIGLOO OR THIS CODE CANNOT BE USED LEGALLY FOR
 * ANY CORPORATE OR COMMERCIAL PURPOSE.
 */
public class ImageEditor extends javax.swing.JDialog {
	// private static final int MAX_IMAGEWIDTH = 500;

	/**
	 *
	 */
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

	public ImageEditor(JFrame frame) {
		super(frame, true);

		initGui();
	}

	public void setImage(BufferedImage image) {
		this.image = image;
		if (image != null) {
			imageSaveButton.setEnabled(true);
			imageCutButton.setEnabled(true);
			imageRotateButton.setEnabled(true);
		}
		initCropper(image);
	}

	public BufferedImage getImage() {
//		// Bildgrösse auf maximale Breite beschränken.
//		if (image.getWidth() > MAX_IMAGEWIDTH) {
//			double scal_factor = (double) MAX_IMAGEWIDTH / image.getWidth();
//			AffineTransform tx = new AffineTransform();
//			tx.scale(scal_factor, scal_factor);
//			AffineTransformOp op = new AffineTransformOp(tx,
//					AffineTransformOp.TYPE_BILINEAR);
//			image = op.filter(image, null);
//		}

		return image;
	}

	private void initGui() {
		try {
			setLayout(new BorderLayout());
			setSize(800, 400);
			add(getOpenCropTurnButtonPanel(), BorderLayout.NORTH);
			add(getImageEditorPanel(), BorderLayout.CENTER);
			add(getSaveCancelButtonPanel(), BorderLayout.SOUTH);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void initCropper(BufferedImage image) {
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
				public void actionPerformed(ActionEvent evt) {
					int returnVal = getFileChooser().showOpenDialog(
							ImageEditor.this);
					if (returnVal == JFileChooser.APPROVE_OPTION) {
						File file = getFileChooser().getSelectedFile();

						try {
							image = ImageIO.read(file);
							setImage(image);
						} catch (IOException e) {
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
				public void actionPerformed(ActionEvent evt) {
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
				public void actionPerformed(ActionEvent evt) {
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
				public void actionPerformed(ActionEvent evt) {
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
				public void actionPerformed(ActionEvent evt) {
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
			FileFilter jpgFilter = new FileNameExtensionFilter("JPEG file",
					"jpg", "jpeg");
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
			BorderLayout imageEditorPanelLayout = new BorderLayout();
			imageEditorPanel.setLayout(imageEditorPanelLayout);
		}

		return imageEditorPanel;
	}

	private static BufferedImage rotateImage90(BufferedImage src) {
		// http://stackoverflow.com/questions/10426883/affinetransform-truncates-image
		int srcWidth = src.getWidth();
        int srcHeight = src.getHeight();

        AffineTransform affineTransform = new AffineTransform();
        affineTransform.setToRotation(Math.toRadians(90), srcWidth / 2d, srcHeight / 2d);

        // source image rectangle
        Point[] points = {
            new Point(0, 0),
            new Point(srcWidth, 0),
            new Point(srcWidth, srcHeight),
            new Point(0, srcHeight)
        };

        // transform to destination rectangle
        affineTransform.transform(points, 0, points, 0, 4);

        // get destination rectangle bounding box
        Point min = new Point(points[0]);
        Point max = new Point(points[0]);
        for (int i = 1, n = points.length; i < n; i ++) {
            Point p = points[i];
            double pX = p.getX(), pY = p.getY();

            // update min/max x
            if (pX < min.getX()) min.setLocation(pX, min.getY());
            if (pX > max.getX()) max.setLocation(pX, max.getY());

            // update min/max y
            if (pY < min.getY()) min.setLocation(min.getX(), pY);
            if (pY > max.getY()) max.setLocation(max.getX(), pY);
        }

        // determine new width, height
        srcWidth = (int) (max.getX() - min.getX());
        srcHeight = (int) (max.getY() - min.getY());

        // determine required translation
        double tx = min.getX();
        double ty = min.getY();

        // append required translation
        AffineTransform translation = new AffineTransform();
        translation.translate(-tx, -ty);
        affineTransform.preConcatenate(translation);

        AffineTransformOp op = new AffineTransformOp(affineTransform, null);
        BufferedImage rotatedImage = new BufferedImage(srcWidth, srcHeight, src.getType());
        op.filter(src, rotatedImage);

        return rotatedImage;
	}
}