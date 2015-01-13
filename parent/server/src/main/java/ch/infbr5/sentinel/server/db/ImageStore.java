package ch.infbr5.sentinel.server.db;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

import org.apache.log4j.Logger;

import ch.infbr5.sentinel.server.model.Person;

public class ImageStore {

   private static Logger log = Logger.getLogger(ImageStore.class);

   private static final String FOLDER_NAME = "images";

   public static Image getImage(final Person person) {
      return getImage(person.getAhvNr());
   }

   public static Image getImage(final String imageId) {
      Image image = null;
      try {
         image = ImageIO.read(new File(ImageStore.createFilename(imageId)));
      } catch (final IOException e) {
         log.error(e);
      }
      return image;
   }

   public static ImageIcon getImageIcon(final String ahvNr, final int width, final int height) {
      ImageIcon imageIcon = null;
      if (ImageStore.hasImage(ahvNr)) {
         imageIcon = new ImageIcon(ImageStore.createFilename(ahvNr));
      }
      return imageIcon;
   }

   public static boolean hasImage(final String ahvNr) {
      final File jpegFile = new File(ImageStore.createFilename(ahvNr));
      return jpegFile.exists();
   }

   public static byte[] loadJpegImage(final String ahvNr) {
      // create file object
      final File file = new File(ImageStore.createFilename(ahvNr));

      if (!file.exists()) {
         return null;
      }

      try {
         // create FileInputStream object
         final FileInputStream fin = new FileInputStream(file);

         final byte fileContent[] = new byte[(int) file.length()];

         fin.read(fileContent);
         fin.close();

         return fileContent;

      } catch (final FileNotFoundException e) {
         log.error("File not found" + e);
      } catch (final IOException ioe) {
         log.error("Exception while reading the file " + ioe);
      }

      return null;
   }

   public static BufferedImage byteArrayToBufferedImage(final byte[] binaryData) {
      if (binaryData == null || binaryData.length == 0) {
         return null;
      }

      BufferedImage image = null;
      try {
         final ByteArrayInputStream bais = new ByteArrayInputStream(binaryData);
         image = ImageIO.read(bais);
      } catch (final IOException e) {
         log.error(e);
      }
      return image;
   }

   public static boolean saveJpegImage(final String ahvNr, final byte[] data) {
      try {
         if ((data != null) && (data.length > 0)) {
            final String filename = createFilename(ahvNr);

            createParentDirectoryIfRequired(filename);

            final FileOutputStream fos = new FileOutputStream(filename);
            fos.write(data);
            fos.close();
         }

         return true;

      } catch (final Exception e) {
         log.error(e);

         return false;
      }
   }

   private static void createParentDirectoryIfRequired(final String filename) {
      final File fileHelper = new File(filename);
      final String parentDirectory = fileHelper.getParent();
      final File directoryHelper = new File(parentDirectory);
      final boolean directoryExists = directoryHelper.exists();
      if (!directoryExists) {
         directoryHelper.mkdirs();
      }
   }

   private static String createFilename(final String ahvNr) {
      final String folder = "." + File.separator + FOLDER_NAME + File.separator;
      return folder + ahvNr + ".jpg";
   }

   public static String getLocalImagePath() {
      return new File(FOLDER_NAME).getAbsolutePath();
   }

}
