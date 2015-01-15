package ch.infbr5.sentinel.client.util;

import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class AhvNrParserTest {

   private AhvNrParser testee;

   @Before
   public void setUp() {
      testee = new AhvNrParser();
   }

   @Test
   public void getPossibleAHVNrsFromFilenameNonValid() {
      List<String> ahvNrs = testee.getPossibleAHVNrsFromFilename("alex.jpeg");
      Assert.assertEquals(0, ahvNrs.size());

      ahvNrs = testee.getPossibleAHVNrsFromFilename("7946adf697.alex");
      Assert.assertEquals(0, ahvNrs.size());

      ahvNrs = testee.getPossibleAHVNrsFromFilename("tes7946adf697");
      Assert.assertEquals(0, ahvNrs.size());

      ahvNrs = testee.getPossibleAHVNrsFromFilename("7946adf697.^qr^1'2394r^/**ç%ç*%&");
      Assert.assertEquals(0, ahvNrs.size());

      ahvNrs = testee.getPossibleAHVNrsFromFilename("AAAAAAAAAAAA");
      Assert.assertEquals(0, ahvNrs.size());
   }

   @Test
   public void getPossibleAHVNrsFromFilenameExactlyOnce() {
      List<String> ahvNrs = testee.getPossibleAHVNrsFromFilename("wohlwend-756.3713.0508.45-wohlwend.jpeg");
      Assert.assertEquals(1, ahvNrs.size());
      Assert.assertEquals("756.3713.0508.45", ahvNrs.get(0));

      ahvNrs = testee.getPossibleAHVNrsFromFilename("wohlwend-756.3713ajsödf.0asdf508.asdf45-wohlwend.jpeg");
      Assert.assertEquals(1, ahvNrs.size());
      Assert.assertEquals("756.3713.0508.45", ahvNrs.get(0));

      ahvNrs = testee.getPossibleAHVNrsFromFilename("75a6.18f18.1e919.2v2");
      Assert.assertEquals(0, ahvNrs.size());
   }

   @Test
   public void getPossibleAHVNrsFromFilenameMultipleNumbers() {
      List<String> ahvNrs = testee.getPossibleAHVNrsFromFilename("dd?756.3713.0508.45-2.jpg");
      Assert.assertEquals(1, ahvNrs.size());
      Assert.assertEquals("756.3713.0508.45", ahvNrs.get(0));

      ahvNrs = testee.getPossibleAHVNrsFromFilename("bild18-von-meihoffer-756-3713-0508-45-amdatum141516.jpg");
      Assert.assertEquals(1, ahvNrs.size());
      Assert.assertEquals("756.3713.0508.45", ahvNrs.get(0));
   }

}
