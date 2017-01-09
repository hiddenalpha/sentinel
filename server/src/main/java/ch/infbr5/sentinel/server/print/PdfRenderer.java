package ch.infbr5.sentinel.server.print;

import java.util.Date;

import ch.infbr5.sentinel.common.util.DateFormater;
import ch.infbr5.sentinel.server.db.PdfStore;
import ch.infbr5.sentinel.server.model.ObjectFactory;
import ch.infbr5.sentinel.server.model.PrintJob;
import ch.infbr5.sentinel.server.utils.FileHelper;

import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfPageEventHelper;
import com.itextpdf.text.pdf.PdfWriter;

public abstract class PdfRenderer {

   public PrintJob print() {
      final byte[] data = renderPdf();
      if (data != null && data.length > 0) {
         final String filename = createFilename();
         PdfStore.savePdfAsFile(filename, data);
         return ObjectFactory.createPrintJob(getBeschreibung(), filename);
      }
      return null;
   }

   protected abstract byte[] renderPdf();

   protected abstract String getFileName();

   protected abstract String getBeschreibung();

   protected HeaderFooter createHeader() {
      return new HeaderFooter(getBeschreibung());
   }

   private String createFilename() {
      final String time = String.valueOf(new Date().getTime());
      return FileHelper.clearFilename(getFileName() + "_" + time);
   }

   private static class HeaderFooter extends PdfPageEventHelper {
      private String beschreibung;
      public HeaderFooter(String beschreibung) {this.beschreibung = beschreibung;}
      @Override
      public void onEndPage(PdfWriter writer, Document document) {
         final Phrase after = new Phrase(", " + DateFormater.formatToDate(new Date()));
         PdfContentByte cb = writer.getDirectContent();
         ColumnText.showTextAligned(cb, Element.ALIGN_CENTER, after,
             (document.right() - document.left()) / 2 + document.leftMargin(),
             document.top() + 10, 0);
      }
      
      @Override
      public void onStartPage(PdfWriter writer, Document document) {
         final Phrase before = new Phrase(beschreibung + ", Seite ");
         PdfContentByte cb = writer.getDirectContent();
         ColumnText.showTextAligned(cb, Element.ALIGN_CENTER, before,
             (document.right() - document.left()) / 2 + document.leftMargin(),
             document.top() + 10, 0);
      }
  }
   
}
