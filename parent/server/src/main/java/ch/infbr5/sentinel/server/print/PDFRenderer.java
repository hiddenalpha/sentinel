package ch.infbr5.sentinel.server.print;

import java.util.Calendar;
import java.util.Date;

import ch.infbr5.sentinel.common.util.DateFormater;
import ch.infbr5.sentinel.server.db.PdfStore;
import ch.infbr5.sentinel.server.model.ObjectFactory;
import ch.infbr5.sentinel.server.model.PrintJob;

import com.lowagie.text.HeaderFooter;
import com.lowagie.text.Phrase;

public abstract class PDFRenderer {

   public PrintJob print() {
      final byte[] data = renderPdf();
      if (data != null && data.length > 0) {
         final String filename = createFilename();
         PdfStore.savaPdfFile(filename, data);
         return ObjectFactory.createPrintJob(getBeschreibung(), filename);
      }
      return null;
   }

   protected abstract byte[] renderPdf();

   protected abstract String getFileName();

   protected abstract String getBeschreibung();

   protected HeaderFooter createHeader() {
      final Phrase before = new Phrase(getBeschreibung() + ", Seite ");
      final Phrase after = new Phrase(", " + DateFormater.formatToDate(new Date()));
      return new HeaderFooter(before, after);
   }

   private String createFilename() {
      final long timestamp = Calendar.getInstance().getTimeInMillis();
      return getFileName() + "_" + String.valueOf(timestamp);
   }

}
