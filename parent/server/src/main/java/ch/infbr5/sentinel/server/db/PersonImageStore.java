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

import org.apache.log4j.Logger;

import ch.infbr5.sentinel.server.model.Person;

public class PersonImageStore {

   private static Logger log = Logger.getLogger(PersonImageStore.class);

   private static final String FOLDER_NAME = "images";

   public static String getLocalImagePath() {
      return new File(FOLDER_NAME).getAbsolutePath();
   }

   public static Image getImage(final Person person) {
      return getImage(person.getAhvNr());
   }

   public static Image getImage(final String ahvNr) {
      Image image = null;
      try {
         if (hasImage(ahvNr)) {
            image = ImageIO.read(createFile(ahvNr));
         }
      } catch (final IOException e) {
         log.error(e);
      }
      return image;
   }

   public static boolean hasImage(final String ahvNr) {
      return createFile(ahvNr).exists();
   }

   public static byte[] loadJpegImage(final String ahvNr) {
      final File file = createFile(ahvNr);
      if (!file.exists()) {
         return null;
      }
      try {
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

   private static File createFile(final String ahvNr) {
      return new File(createFilename(ahvNr));
   }

}
