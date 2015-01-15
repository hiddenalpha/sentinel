package ch.infbr5.sentinel.client.util;

import java.util.ArrayList;
import java.util.List;

import ch.infbr5.sentinel.common.validator.AhvNrValidator;

public class AhvNrParser {

   private final AhvNrValidator ahvNrValidator = new AhvNrValidator();

   /**
    * Gibt alle möglichen AHV Nr zurück, welche in dem String versteckt sein
    * können. Es werden alle nicht nummerischen Zeichen eliminiert und die
    * daraus resultierende Zeichenfolge, alle möglichen AHV Nr generiert. Die
    * resultierenden AHV Nr werden validiert. Es werden nur gültige zurückgeben.
    *
    * @param filename
    *           X-Belieber string
    * @return Alle möglichen AHV Nr.
    */
   public List<String> getPossibleAHVNrsFromFilename(final String string) {
      final String filenameJustNumbers = string.replaceAll("[^\\d]", "");
      final List<String> ahvNrs = new ArrayList<>();
      if (filenameJustNumbers != null && filenameJustNumbers.length() >= 13) {
         for (int i = 0; i <= (filenameJustNumbers.length() - 13); i++) {
            final String subString = filenameJustNumbers.substring(i, i + 13);
            final String ahvNr = transformToAhvNr(subString);
            if (ahvNrValidator.validate(ahvNr)) {
               ahvNrs.add(ahvNr);
            }
         }
      }
      return ahvNrs;
   }

   /**
    * Als input eine 13 Zeichen langer String bestehend aus numbers. Daraus wird
    * dann eine AHV-Nr geformt.
    *
    * @param numbers
    *           13 Zeichen langer String.
    * @return AHV Nr.
    */
   private String transformToAhvNr(String numbers) {
      if (numbers != null && numbers.length() == 13) {
         numbers = numbers.substring(0, 3) + "." + numbers.substring(3, 7) + "." + numbers.substring(7, 11) + "."
               + numbers.substring(11, 13);
         return numbers;
      }
      return null;
   }

}
