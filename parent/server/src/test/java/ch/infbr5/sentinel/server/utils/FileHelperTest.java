package ch.infbr5.sentinel.server.utils;

import org.junit.Assert;
import org.junit.Test;

public class FileHelperTest {

   @Test
   public void testClearFilename() {
      String s = "079";
      Assert.assertEquals("079", FileHelper.clearFilename(s));

      s = "079abc";
      Assert.assertEquals("079abc", FileHelper.clearFilename(s));

      s = "079abc!!";
      Assert.assertEquals("079abc", FileHelper.clearFilename(s));

      s = "0 79a DFA!!";
      Assert.assertEquals("0-79a-dfa", FileHelper.clearFilename(s));

      s = ".0-7_9aDFA!!";
      Assert.assertEquals(".0-7_9adfa", FileHelper.clearFilename(s));

      s = ".0-7_=ç*+9aDF{}A!!";
      Assert.assertEquals(".0-7_9adfa", FileHelper.clearFilename(s));
   }

}
