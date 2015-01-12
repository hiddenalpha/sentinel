package ch.infbr5.sentinel.server.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import com.google.common.io.Files;

public class FileHelper {

   public static final String FILE_AUSWEISVORLAGE_WASSERZEICHEN = "ausweisvorlage_wasserzeichen.png";

   public static final String FILE_AUSWEISVORLAGE_LOGO = "ausweisvorlage_logo.png";

   public static byte[] getAsByteArray(final String filename) throws IOException {
      return Files.toByteArray(new File(filename));
   }

   public static String clearFilename(String filename) {
      filename = filename.toLowerCase();
      filename = filename.replace(" ", "-");
      filename = filename.replace(".", "-");
      filename = filename.replaceAll("[^0-9a-z-_]", "");
      return filename;
   }

   /**
    * Loescht das File sofern es existiert.
    *
    * @param filename
    *           Datei zu loeschen.
    */
   public static void removeFile(final String filename) {
      final File file = new File(filename);
      if (file.exists()) {
         file.delete();
      }
   }

   public static void removeFile(final File file) {
      removeFile(file.getAbsolutePath());
   }

   public static void removeFolderContent(final File f) {
      if (f.isDirectory()) {
         for (final File c : f.listFiles()) {
            c.delete();
         }
      }
   }

   public static boolean saveAsFile(final String filename, final byte[] data) {
      try {
         if ((data != null) && (data.length > 0)) {
            createParentDirectoryIfRequired(filename);

            final FileOutputStream fos = new FileOutputStream(filename);

            fos.write(data);
            fos.close();
         }
         return true;

      } catch (final Exception e) {
         e.printStackTrace();
         return false;
      }
   }

   public static String getExtension(final String filename) {
      final int lastIndexOfPoint = filename.lastIndexOf(".");
      return filename.substring(lastIndexOfPoint + 1);
   }

   private static void createParentDirectoryIfRequired(final String filename) {
      final File fileHelper = new File(filename);
      final String parentDirectory = fileHelper.getParent();
      if (parentDirectory != null) {
         final File directoryHelper = new File(parentDirectory);
         final boolean directoryExists = directoryHelper.exists();
         if (!directoryExists) {
            directoryHelper.mkdirs();
         }
      }
   }

}
