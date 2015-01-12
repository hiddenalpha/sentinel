package ch.infbr5.sentinel.server.print;

import java.io.ByteArrayOutputStream;
import java.util.List;

import ch.infbr5.sentinel.server.model.Person;

import com.lowagie.text.Chunk;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.PageSize;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

public class PdfAusweisBoxInventar extends PDFRenderer {

   private static final int NOF_SLOTS_IN_BOX = 132;

   private final List<Person> personen;

   private final String einheitName;

   public PdfAusweisBoxInventar(final List<Person> personen, final String einheitName) {
      this.personen = personen;
      this.einheitName = einheitName;
   }

   @Override
   protected String getFileName() {
      return "ausweisbox_"; // TODO Einheitsname
   }

   @Override
   protected String getBeschreibung() {
      return "Ausweisbox " + einheitName;
   }

   @Override
   protected byte[] renderPdf() {

      if (personen.isEmpty()) {
         return null;
      }

      final Document document = new Document();
      document.setPageSize(PageSize.A4.rotate());
      final ByteArrayOutputStream out = new ByteArrayOutputStream();
      final Font font = FontFactory.getFont(FontFactory.COURIER, 8, Font.NORMAL);

      try {
         PdfWriter.getInstance(document, out);
         document.setHeader(createHeader());
         document.open();

         final int seiten = ((personen.size() - 1) / NOF_SLOTS_IN_BOX) + 1;

         for (int s = 1; s <= seiten; s++) {
            // Tabellen Layout
            final PdfPTable table = new PdfPTable(5);
            final float[] columnWidth = { 150, 150, 150, 150, 150 };
            table.setTotalWidth(columnWidth);
            table.setLockedWidth(true);

            for (int i = 1; i <= 27; i++) {
               for (int j = 1; j <= 5; j++) {

                  final Phrase p = new Phrase();

                  p.add(new Chunk(getAusweisAt(i, j, s), font));

                  final PdfPCell c = new PdfPCell(p);
                  c.setFixedHeight(18);
                  table.addCell(c);
               }

            }
            document.add(table);
            document.newPage();
         }

      } catch (final DocumentException e) {
         e.printStackTrace();
      }
      document.close();
      return out.toByteArray();
   }

   private String getAusweisAt(final int row, final int col, final int page) {
      if ((col == 3) && (row > 24)) {
         return "";
      }

      int nr = (col - 1) * 27 + row;
      if (col > 3) {
         nr = nr - 3;
      }

      final int AusweisNr = nr - 1 + (NOF_SLOTS_IN_BOX * (page - 1));

      String txt = "";
      if (AusweisNr < personen.size()) {
         txt = getPersonDescription(nr, personen.get(AusweisNr));
      }
      return txt;
   }

   private String getPersonDescription(final int nr, final Person person) {
      String description = "";
      if (person != null) {
         description = String.valueOf(nr) + ". ";
         if (person.getName() != null) {
            description += person.getName() + " ";
         }
         if (person.getVorname() != null) {
            description += person.getVorname() + " ";
         }
         if (person.getGrad() != null) {
            description += "(" + person.getGrad().getGradText() + ")";
         }
      }
      return description.trim();
   }

}
