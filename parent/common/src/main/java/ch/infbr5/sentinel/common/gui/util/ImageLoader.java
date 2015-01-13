package ch.infbr5.sentinel.common.gui.util;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;

public class ImageLoader {

   public static BufferedImage loadSentinelIcon() {
      BufferedImage defaultImage = null;
      final URL imageURL = ImageLoader.class.getResource("/images/icon.gif");
      try {
         defaultImage = ImageIO.read(imageURL);
      } catch (final IOException e) {
         e.printStackTrace();
      }
      return defaultImage;
   }

   public static BufferedImage loadNobodyImage() {
      BufferedImage defaultImage = null;
      final URL imageURL = ImageLoader.class.getResource("/images/nobody.jpg");
      try {
         defaultImage = ImageIO.read(imageURL);
      } catch (final IOException e) {
         e.printStackTrace();
      }
      return defaultImage;
   }

}
