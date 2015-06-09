package ch.infbr5.sentinel.client.image;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;

import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * @author Christophe Le Besnerais
 */
public class ImageCropper extends JLayeredPane {

   private static final long serialVersionUID = 1L;

   private static final float TARGET_IMAGE_WIDTH = 150.0f;

   private static final float TARGET_IMAGE_HEIGHT = 200.0f;

   private final BufferedImage image;
   private BufferedImage thumbnail;
   private BufferedImage croppedImage;
   private JPanel background;
   private JPanel croppingPanel;
   private JPanel resizePanel;
   private Rectangle crop;
   private final float propFaktor = TARGET_IMAGE_HEIGHT / TARGET_IMAGE_WIDTH;
   private BufferedImageOp filter;
   private transient ChangeEvent changeEvent;

   public ImageCropper(final BufferedImage image) {
      final InnerListener listener = new InnerListener();
      this.addComponentListener(listener);
      this.image = image;

      if (image.getHeight() > (image.getWidth() * propFaktor)) {
         this.crop = new Rectangle(0, 0, image.getWidth(), (int) (image.getWidth() * propFaktor));
      } else {
         this.crop = new Rectangle(0, 0, (int) (image.getHeight() / propFaktor), image.getHeight());
      }

      this.croppedImage = image.getSubimage(crop.x, crop.y, crop.width, crop.height);

      createBackgroundPanel();

      createCroppingPanel(listener);

      createResizePanel(listener);
   }

   public BufferedImageOp getFilter() {
      return filter;
   }

   public void setFilter(final BufferedImageOp filter) {
      this.filter = filter;
   }

   public BufferedImage getCroppedImage() {
      return croppedImage;
   }

   private BufferedImage getThumbbail() {
      if (thumbnail == null) {
         int width = ImageCropper.this.image.getWidth();
         int height = ImageCropper.this.image.getHeight();
         final float ratio = (float) width / (float) height;
         if ((float) this.getWidth() / (float) this.getHeight() > ratio) {
            height = this.getHeight();
            width = (int) (height * ratio);
         } else {
            width = this.getWidth();
            height = (int) (width / ratio);
         }

         thumbnail = getGraphicsConfiguration().createCompatibleImage(width, height,
               ImageCropper.this.image.getTransparency());
         final Graphics2D g2d = thumbnail.createGraphics();
         g2d.drawImage(ImageCropper.this.image, 0, 0, width, height, null);
         g2d.dispose();

         if (filter != null) {
            thumbnail = filter.filter(thumbnail, null);
         }
      }

      return thumbnail;
   }

   private void changePosition() {
      if (crop.x < 0)
         crop.x = 0;
      if (crop.x > image.getWidth())
         crop.x = image.getWidth();
      if (crop.y < 0)
         crop.y = 0;
      if (crop.y > image.getHeight())
         crop.y = image.getHeight();
      if (crop.x + crop.width > image.getWidth())
         crop.x = image.getWidth() - crop.width;
      if (crop.y + crop.height > image.getHeight())
         crop.y = image.getHeight() - crop.height;

      changeCrop(true);
   }

   private void changeSize() {
      if (crop.width <= 0) {
         crop.width = 1;
      }

      if (crop.height <= 0) {
         crop.height = 1;
      }

      final float ratio = TARGET_IMAGE_WIDTH / TARGET_IMAGE_HEIGHT;
      if (crop.x + crop.width > image.getWidth()) {
         crop.width = image.getWidth() - crop.x;
         crop.height = (int) (crop.width / ratio);
      } else if (crop.y + crop.height > image.getHeight()) {
         crop.height = image.getHeight() - crop.y;
         crop.width = (int) (crop.height * ratio);
      } else if (crop.width > image.getWidth()) {
         crop.width = image.getWidth();
         crop.height = (int) (crop.width / ratio);
      } else if (crop.height > image.getHeight()) {
         crop.height = image.getHeight();
         crop.width = (int) (crop.height * ratio);
      }

      changeCrop(true);
   }

   private void changeCrop(final boolean updatePanel) {
      this.croppedImage = image.getSubimage(crop.x, crop.y, crop.width, crop.height);
      this.fireStateChanged();

      if (updatePanel) {
         final float ratio = (float) image.getWidth() / (float) getThumbbail().getWidth();
         this.croppingPanel.setSize((int) (crop.width / ratio), (int) (crop.height / ratio));
         this.croppingPanel.setLocation((int) (crop.x / ratio) + (getWidth() - getThumbbail().getWidth()) / 2,
               (int) (crop.y / ratio) + (getHeight() - getThumbbail().getHeight()) / 2);
         this.resizePanel.setLocation(croppingPanel.getX() + croppingPanel.getWidth() - resizePanel.getWidth() / 2,
               croppingPanel.getY() + croppingPanel.getHeight() - resizePanel.getHeight() / 2);
      } else {
         this.croppingPanel.repaint();
      }
   }

   private void createBackgroundPanel() {
      this.background = new JPanel(true) {
         /**
			 *
			 */
         private static final long serialVersionUID = 1L;

         @Override
         protected void paintComponent(final Graphics g) {
            g.drawImage(getThumbbail(), (this.getWidth() - getThumbbail().getWidth()) / 2,
                  (this.getHeight() - getThumbbail().getHeight()) / 2, null);
         }
      };
      this.add(background, JLayeredPane.DEFAULT_LAYER);
   }

   private void createResizePanel(final InnerListener listener) {
      this.resizePanel = new JPanel(true) {
         /**
			 *
			 */
         private static final long serialVersionUID = 1L;

         @Override
         protected void paintComponent(final Graphics g) {
            g.setColor(Color.GRAY);
            g.fillRect(0, 0, this.getWidth(), this.getHeight());
            g.setColor(Color.BLACK);
            g.drawRect(0, 0, this.getWidth() - 1, this.getHeight() - 1);
            g.drawLine(2, 4, 4, 2);
            g.drawLine(2, 7, 7, 2);
            g.drawLine(5, 7, 7, 5);
         }
      };
      this.resizePanel.addMouseListener(listener);
      this.resizePanel.addMouseMotionListener(listener);
      this.resizePanel.setCursor(Cursor.getPredefinedCursor(Cursor.NW_RESIZE_CURSOR));
      this.resizePanel.setSize(10, 10);
      this.add(resizePanel, JLayeredPane.PALETTE_LAYER, 0);
   }

   private void createCroppingPanel(final InnerListener listener) {
      this.croppingPanel = new JPanel(true) {
         /**
			 *
			 */
         private static final long serialVersionUID = 1L;

         @Override
         protected void paintComponent(final Graphics g) {
            g.drawImage(croppedImage, 0, 0, this.getWidth(), this.getHeight(), null);
            g.setColor(Color.BLACK);
            g.drawRect(0, 0, this.getWidth() - 1, this.getHeight() - 1);
         }
      };
      this.croppingPanel.addMouseListener(listener);
      this.croppingPanel.addMouseMotionListener(listener);
      this.croppingPanel.setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
      this.add(croppingPanel, JLayeredPane.PALETTE_LAYER, 2);
   }

   public void addChangeListener(final ChangeListener l) {
      listenerList.add(ChangeListener.class, l);
   }

   public void removeChangeListener(final ChangeListener l) {
      listenerList.remove(ChangeListener.class, l);
   }

   public ChangeListener[] getChangeListeners() {
      return (listenerList.getListeners(ChangeListener.class));
   }

   private void fireStateChanged() {
      final Object[] listeners = listenerList.getListenerList();
      for (int i = listeners.length - 2; i >= 0; i -= 2) {
         if (listeners[i] == ChangeListener.class) {
            if (changeEvent == null)
               changeEvent = new ChangeEvent(this);
            ((ChangeListener) listeners[i + 1]).stateChanged(changeEvent);
         }
      }
   }

   private class InnerListener extends MouseAdapter implements ComponentListener, MouseMotionListener {

      private Point cursorPosition;

      @Override
      public void componentResized(final ComponentEvent e) {
         thumbnail = null;
         background.setSize(ImageCropper.this.getSize());
         final float ratio = (float) getThumbbail().getWidth() / (float) image.getWidth();
         croppingPanel.setSize((int) (crop.width * ratio), (int) (crop.height * ratio));
         croppingPanel.setLocation((int) (crop.x * ratio + (getWidth() - getThumbbail().getWidth()) / 2), (int) (crop.y
               * ratio + (getHeight() - getThumbbail().getHeight()) / 2));
         resizePanel.setLocation(croppingPanel.getX() + croppingPanel.getWidth() - resizePanel.getWidth() / 2,
               croppingPanel.getY() + croppingPanel.getHeight() - resizePanel.getHeight() / 2);
         thumbnail = null;
      }

      @Override
      public void mousePressed(final MouseEvent e) {
         cursorPosition = e.getPoint();
         setLayer(e.getComponent(), JLayeredPane.DRAG_LAYER);
      }

      @Override
      public void mouseReleased(final MouseEvent e) {
         cursorPosition = null;
         setLayer(e.getComponent(), JLayeredPane.PALETTE_LAYER);
         setPosition(croppingPanel, 1);
         setPosition(resizePanel, 0);
      }

      @Override
      public void mouseDragged(final MouseEvent e) {
         if (cursorPosition != null) {
            if (e.getComponent().equals(croppingPanel)) {
               final float ratio = (float) image.getWidth() / (float) getThumbbail().getWidth();
               final Point pos = SwingUtilities.convertPoint(croppingPanel, e.getPoint(), background);
               pos.translate(-cursorPosition.x, -cursorPosition.y);
               pos.translate(croppingPanel.getWidth() / 2, croppingPanel.getHeight() / 2);
               pos.translate(-(getWidth() - getThumbbail().getWidth()) / 2,
                     -(getHeight() - getThumbbail().getHeight()) / 2);
               pos.setLocation(pos.x * ratio, pos.y * ratio);
               pos.translate(-crop.width / 2, -crop.height / 2);
               crop.setLocation(pos.x, pos.y);
               changePosition();

            } else if (e.getComponent().equals(resizePanel)) {
               final float ratio = (float) image.getWidth() / (float) getThumbbail().getWidth();
               final Point start = SwingUtilities.convertPoint(resizePanel, cursorPosition, background);
               final Point end = SwingUtilities.convertPoint(resizePanel, e.getPoint(), background);

               final int cSize = (int) ((end.x - start.x) * ratio);
               crop.setSize(crop.width + cSize, (int) (crop.width * propFaktor));

               changeSize();
            }
         }
      }

      @Override
      public void componentHidden(final ComponentEvent arg0) {
      }

      @Override
      public void componentMoved(final ComponentEvent arg0) {
      }

      @Override
      public void componentShown(final ComponentEvent arg0) {
      }
   }
}