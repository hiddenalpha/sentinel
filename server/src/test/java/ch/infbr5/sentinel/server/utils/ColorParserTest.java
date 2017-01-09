package ch.infbr5.sentinel.server.utils;

import java.awt.Color;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class ColorParserTest {

   private ColorParser testee;

   @Before
   public void setUp() {
      testee = new ColorParser();
   }

   @Test
   public void testParse000() {
      assertParsing("#000", null, Color.black);
   }

   @Test
   public void testParseNull() {
      assertParsing(null, null, null);
   }

   @Test
   public void testParseNullWithDefault() {
      assertParsing(null, Color.pink, Color.pink);
   }

   @Test
   public void testParseWithoutHag() {
      assertParsing("000", null, Color.black);
   }

   @Test
   public void testParse6Signs() {
      assertParsing("#FFFFFF", null, Color.white);
   }

   private void assertParsing(final String htmlColor, final Color defaultColor, final Color expectedColor) {
      Assert.assertEquals(expectedColor, testee.parse(htmlColor, defaultColor));
   }

}
