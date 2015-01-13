package ch.infbr5.sentinel.server.print.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfImportedPage;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfWriter;

public class PdfMerge {

   /**
    * Merge multiple pdf into one pdf
    *
    * @param list
    *           of pdf input stream
    * @param outputStream
    *           output file output stream
    * @throws DocumentException
    * @throws IOException
    */
   public static void doMerge(final List<InputStream> list, final OutputStream outputStream) throws DocumentException,
         IOException {
      final Document document = new Document();
      final PdfWriter writer = PdfWriter.getInstance(document, outputStream);
      document.open();
      final PdfContentByte cb = writer.getDirectContent();

      for (final InputStream in : list) {
         final PdfReader reader = new PdfReader(in);
         for (int i = 1; i <= reader.getNumberOfPages(); i++) {
            document.newPage();
            // import the page from source pdf
            final PdfImportedPage page = writer.getImportedPage(reader, i);
            // add the page to the destination pdf
            cb.addTemplate(page, 0, 0);
         }
      }

      outputStream.flush();
      document.close();
      outputStream.close();
   }
}
