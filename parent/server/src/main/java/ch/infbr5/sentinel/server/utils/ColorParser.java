package ch.infbr5.sentinel.server.utils;

import java.awt.Color;

public class ColorParser {

   public Color parse(String htmlCode, final Color defaultColor) {
      Color c;
      try {
         if (htmlCode == null || htmlCode.isEmpty()) {
            c = defaultColor;
         } else if (!htmlCode.startsWith("#")) {
            htmlCode = "#" + htmlCode;
            c = Color.decode(htmlCode);
         } else {
            c = Color.decode(htmlCode);
         }
      } catch (final NumberFormatException e) {
         c = defaultColor;
      }
      return c;
   }

}
