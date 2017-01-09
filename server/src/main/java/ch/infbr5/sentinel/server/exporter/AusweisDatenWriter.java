package ch.infbr5.sentinel.server.exporter;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.List;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.ZipParameters;

import org.apache.log4j.Logger;

import ch.infbr5.sentinel.server.model.Person;
import ch.infbr5.sentinel.server.utils.FileHelper;

import com.google.common.collect.Lists;
import com.thoughtworks.xstream.XStream;

public class AusweisDatenWriter {

   private static final String EXPORT_FILENAME = "pExpData.zip";

   private static final String ZIPED_XML_FILENAME = "personData.xml";

   private static final Logger log = Logger.getLogger(AusweisDatenWriter.class);

   public static byte[] export(final String password, final List<Person> result) {

      try {
         // Eventuell Datei loeschen
         FileHelper.removeFile(EXPORT_FILENAME);

         // XStream vorbereiten
         final List<Person> personen = Lists.newArrayList(result);

         final XStream xstream = new XStream();
         xstream.alias("person", Person.class);
         xstream.alias("list", personen.getClass());

         final ByteArrayOutputStream bos = new ByteArrayOutputStream();
         xstream.toXML(personen, new OutputStreamWriter(bos, "UTF-8"));
         bos.close();

         // Zip Datei erstellen
         final ZipFile zipFile = new ZipFile(new File(EXPORT_FILENAME));
         final ZipParameters parameters = ZipUtil.createZipParameters(password, ZIPED_XML_FILENAME);
         zipFile.addStream(new ByteArrayInputStream(bos.toByteArray()), parameters);

         // Weitere Dateien hinzfuf�gen
         parameters.setSourceExternalStream(false);
         if (new File("images").exists()) {
            zipFile.addFolder("images", parameters);
         }

         return FileHelper.getAsByteArray(EXPORT_FILENAME);
      } catch (ZipException | IOException e) {
         log.error(e);
      }

      return null;
   }

}
