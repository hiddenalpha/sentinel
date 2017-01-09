package ch.infbr5.sentinel.client;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

public class Version {

   private static Version v = null;

   private String version = "DevVersion";

   private String buildTimestamp = "DevTimestamp";

   private Version() {
      final String pathManifest = "/META-INF/MANIFEST.MF";

      final URL manifestURL = Version.class.getResource(pathManifest);
      final Class<Version> clazz = Version.class;
      final String className = clazz.getSimpleName() + ".class";
      final String classPath = clazz.getResource(className).toString();
      if (!classPath.startsWith("jar")) {
         return;
      }
      final String manifestPath = classPath.substring(0, classPath.lastIndexOf("!") + 1) + pathManifest;

      try {
         if (manifestURL != null) {
            final InputStream is = new URL(manifestPath).openStream();
            final Manifest mf = new Manifest(is);
            final Attributes a = mf.getMainAttributes();
            version = a.getValue("Implementation-Version");
            buildTimestamp = a.getValue("BuildTimestamp");
            is.close();
         }

      } catch (final IOException e) {
         e.printStackTrace();
      }

   }

   public static Version get() {
      if (v == null) {
         v = new Version();
      }
      return v;
   }

   public String getVersion() {
      return version;
   }

   public String getBuildTimestamp() {
      return buildTimestamp;
   }

}
