package ch.infbr5.sentinel.server.importer.personen.util;

import java.text.ParseException;

import ch.infbr5.sentinel.common.validator.AhvNrValidator;
import ch.infbr5.sentinel.common.validator.CommonValidator;
import ch.infbr5.sentinel.server.model.Grad;
import ch.infbr5.sentinel.server.utils.DateParser;

public class ValidationRules {

   public static boolean isValidGeburtstag(final String string) {
      if (string == null) {
         return false;
      }

      // fix Ahv Nr sind keine Geburtstage
      if (isValidAhvNr(string)) {
         return false;
      }

      boolean isValid = true;
      try {
         DateParser.parseDateStringToCalendar(string);
      } catch (final ParseException e) {
         isValid = false;
      }
      return isValid;
   }

   public static boolean isValidGrad(final String string) {
      if (string == null) {
         return false;
      }

      if (Grad.getGrad(string) == null || Grad.getGrad(string).equals(Grad.OHNE)) {
         return false;
      }
      return true;
   }

   public static boolean isValidString(final String string) {
      return string != null && !"".equals(string);
   }

   public static boolean isValidAhvNr(final String nr) {
      final CommonValidator validator = new AhvNrValidator();
      return validator.validate(nr);
   }

}
