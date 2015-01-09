package ch.infbr5.sentinel.server.print;

import java.awt.Desktop;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import ch.infbr5.sentinel.server.ServerConfiguration;
import ch.infbr5.sentinel.server.model.Ausweis;
import ch.infbr5.sentinel.server.model.Einheit;
import ch.infbr5.sentinel.server.model.Grad;
import ch.infbr5.sentinel.server.model.ObjectFactory;
import ch.infbr5.sentinel.server.model.Person;
import ch.infbr5.sentinel.server.ws.AusweisvorlageKonfiguration;

import com.google.common.collect.Lists;

public class IdentityCardRenderer2Test {

   private List<Ausweis> ausweise;

   public String createBarcode() {
      final Random zufall = new Random();
      final String barcode = ObjectFactory.BARCODE_PREFIX.concat(String.valueOf(100000 + zufall.nextInt((int) Math.pow(
            10, ObjectFactory.BARCODE_LAENGE) - 100000)));
      return barcode;
   }

   @Before
   public void setUp() {
      ausweise = Lists.newArrayList();

      final Einheit e1 = new Einheit();
      e1.setName("Stab");
      e1.setRgbColor_GsVb("#234eee");
      e1.setRgbColor_Einh("#445ddd");
      e1.setRgbColor_TrpK("#112233");
      e1.setText_Einh("001");
      e1.setText_GsVb("002");
      e1.setText_TrpK("003");

      final Einheit e2 = new Einheit();
      e2.setName("Küche");
      e2.setRgbColor_GsVb("#eeeeee");
      e2.setRgbColor_Einh("#dddddd");
      e2.setRgbColor_TrpK("#233233");
      e2.setText_Einh("004");
      e2.setText_GsVb("005");
      e2.setText_TrpK("006");

      final Einheit e3 = new Einheit();
      e3.setName("ZuKo");
      e3.setRgbColor_GsVb("#777888");
      e3.setRgbColor_Einh("#888777");
      e3.setRgbColor_TrpK("#999999");
      e3.setText_Einh("007");
      e3.setText_GsVb("008");
      e3.setText_TrpK("009");

      final Person p1 = new Person();
      p1.setAhvNr("654");
      p1.setName("Suter");
      p1.setVorname("Alexander");
      p1.setFunktion("Betriebssoldat");
      p1.setGeburtsdatum(Calendar.getInstance());
      p1.setGrad(Grad.SDT);
      p1.setEinheit(e1);

      final Person p2 = new Person();
      p2.setAhvNr("321");
      p2.setName("Illimar");
      p2.setVorname("Yenzi");
      p2.setFunktion("Putze");
      p2.setGeburtsdatum(Calendar.getInstance());
      p2.setGrad(Grad.SDT);
      p2.setEinheit(e2);

      final Person p3 = new Person();
      p3.setAhvNr("789");
      p3.setName("Balboa");
      p3.setVorname("Rocky");
      p3.setFunktion("Vernichter");
      p3.setGeburtsdatum(Calendar.getInstance());
      p3.setGrad(Grad.SDT);
      p3.setEinheit(e3);

      final Person p4 = new Person();
      p4.setAhvNr("456");
      p4.setName("Schwarzenegger");
      p4.setVorname("Arnold");
      p4.setFunktion("Alleskönner");
      p4.setGeburtsdatum(Calendar.getInstance());
      p4.setGrad(Grad.BUNDESRAT);
      p4.setEinheit(e1);

      final Person p5 = new Person();
      p5.setAhvNr("123");
      p5.setName("Jordan");
      p5.setVorname("Michael");
      p5.setFunktion("Slamdunker");
      p5.setGeburtsdatum(Calendar.getInstance());
      p5.setGrad(Grad.BUNDESRAT);
      p5.setEinheit(e2);

      final Ausweis a1 = new Ausweis();
      a1.setBarcode(createBarcode());
      a1.setGueltigVon(new Date());
      a1.setPerson(p1);
      ausweise.add(a1);

      final Ausweis a2 = new Ausweis();
      a2.setBarcode(createBarcode());
      a2.setGueltigVon(new Date());
      a2.setPerson(p2);
      ausweise.add(a2);

      final Ausweis a3 = new Ausweis();
      a3.setBarcode(createBarcode());
      a3.setGueltigVon(new Date());
      a3.setPerson(p3);
      ausweise.add(a3);

      final Ausweis a4 = new Ausweis();
      a4.setBarcode(createBarcode());
      a4.setGueltigVon(new Date());
      a4.setPerson(p4);
      ausweise.add(a4);

      final Ausweis a5 = new Ausweis();
      a5.setBarcode(createBarcode());
      a5.setGueltigVon(new Date());
      a5.setPerson(p5);
      ausweise.add(a5);
   }

   @Test
   @Ignore
   public void testCreate() throws IOException {
      final AusweisvorlageKonfiguration config = new AusweisvorlageKonfiguration();
      config.setShowQRCode(false);
      config.setShowAreaBackside(false);
      config.setColorAreaBackside("#77AA11");
      config.setColorBackground("#11FF11");
      config.setDefaultWasserzeichen(to(IdentityCardRenderer2Test.class
            .getResource(ServerConfiguration.RESOURCE_PATH_AUSWEISVORLAGE_WASSERZEICHEN)));
      // config.setDefaultWasserzeichen(Files.toByteArray(new
      // File(FileHelper.FILE_AUSWEISVORLAGE_WASSERZEICHEN)));

      final IdendityCardRenderer r = new IdendityCardRenderer(ausweise, "infbr5", config);
      try {
         // Files.write(r.renderPdf(), new File("C:\\tmp\\test.pdf"));
         Desktop.getDesktop().open(new File("C:\\tmp\\test.pdf"));
      } catch (final IOException e1) {
         e1.printStackTrace();
      }
   }

   private byte[] to(final URL url) {
      final ByteArrayOutputStream bais = new ByteArrayOutputStream();
      InputStream is = null;
      try {
         is = url.openStream();
         final byte[] byteChunk = new byte[4096]; // Or whatever size you want
                                                  // to read in at a time.
         int n;

         while ((n = is.read(byteChunk)) > 0) {
            bais.write(byteChunk, 0, n);
         }
         is.close();
      } catch (final IOException e) {
         System.err.printf("Failed while reading bytes from %s: %s", url.toExternalForm(), e.getMessage());
         e.printStackTrace();
         // Perform any other exception handling that's appropriate.
      } finally {

      }
      return bais.toByteArray();
   }

}
