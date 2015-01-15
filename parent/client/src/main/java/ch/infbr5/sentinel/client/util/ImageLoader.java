package ch.infbr5.sentinel.client.util;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.apache.log4j.Logger;

import ch.infbr5.sentinel.client.config.ConfigurationLocalHelper;

public class ImageLoader {

   private static Logger log = Logger.getLogger(ImageLoader.class);

   /**
    * Lädt das Bild. Diese Methode ist optimiert. Falls der Client und Server
    * auf einem System arbeiten, wird das Bild direkt aus dem Server Ordner
    * geladen und nicht über den Network Stack geschickt.
    *
    * @param imageId
    *           ID des Bildes.
    * @return BufferedImage Das Bild der Person oder null.
    */
   public static BufferedImage loadImage(final String imageId) {
      if (isEmptyString(imageId)) {
         return null;
      }

      final ConfigurationLocalHelper config = ConfigurationLocalHelper.getConfig();

      try {
         if (config.isLocalMode() && !isEmptyString(config.getLocalImagePath())) {
            final String path = config.getLocalImagePath() + "\\" + imageId + ".jpg";
            log.trace("Lade Bild [" + imageId + "] direkt lokal von " + path);
            return ImageIO.read(new File(path));
         } else {
            if (ServiceHelper.getSentinelService().hasPersonImage(imageId)) {
               final byte[] data = ServiceHelper.getSentinelService().getPersonImage(imageId);
               log.trace("Lade Bild [" + imageId + "] von Server");
               return ImageIO.read(new ByteArrayInputStream(data));
            }
         }
      } catch (final IOException e) {
         log.error(e);
      }

      return null;
   }

   private static boolean isEmptyString(final String s) {
      return s == null || s.isEmpty();
   }
}
