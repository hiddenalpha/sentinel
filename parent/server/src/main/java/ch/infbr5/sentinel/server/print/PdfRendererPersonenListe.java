package ch.infbr5.sentinel.server.print;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

import ch.infbr5.sentinel.common.util.DateFormater;
import ch.infbr5.sentinel.server.db.ImageStore;
import ch.infbr5.sentinel.server.model.Ausweis;
import ch.infbr5.sentinel.server.model.Person;

import com.lowagie.text.Chunk;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Image;
import com.lowagie.text.Jpeg;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

public class PdfRendererPersonenListe extends PdfRenderer {

   private final List<Person> personen;

   private final String beschreibung;

   public PdfRendererPersonenListe(final List<Person> personen, final String beschreibung) {
      this.personen = personen;
      this.beschreibung = beschreibung;
   }

   @Override
   protected String getFileName() {
      return getBeschreibung();
   }

   @Override
   public String getBeschreibung() {
      return this.beschreibung;
   }

   @Override
   protected byte[] renderPdf() {

      if (personen.isEmpty()) {
         return null;
      }

      final Document document = new Document();
      final ByteArrayOutputStream out = new ByteArrayOutputStream();
      try {
         PdfWriter.getInstance(document, out);
         document.setHeader(createHeader());
         document.open();

         // Tabellen Layout
         final PdfPTable table = new PdfPTable(4);
         final float[] columnWidth = { 30, 130, 130, 200 };
         table.setTotalWidth(columnWidth);
         table.setLockedWidth(true);

         for (final Person person : personen) {
            // Spalte 1 --- Foto ---
            Image imgJpeg;
            final byte[] bild = ImageStore.loadJpegImage(person.getAhvNr());
            if (bild != null) {
               imgJpeg = new Jpeg(bild);
               table.addCell(imgJpeg);
            } else {
               table.addCell("");
            }

            // Spalte 2 --- Personendaten ---
            table.addCell(createPersPhrase(person));

            // Spalte 3 --- Ausweisinformationen ---
            if (person.getValidAusweis() != null) {
               table.addCell(createAusweisPhrase(person.getValidAusweis()));
            } else {
               table.addCell("");
            }

            // Spalte 4 --- Leer ---
            table.addCell("");

         }

         document.add(table);
      } catch (final DocumentException e) {
         e.printStackTrace();
      } catch (final IOException e) {
         e.printStackTrace();
      }
      document.close();
      return out.toByteArray();
   }

   private Phrase createAusweisPhrase(final Ausweis ausweis) {
      final Phrase p = new Phrase();

      p.add(new Chunk(ausweis.getBarcode() + "\n", createCourierNormal()));

      // ---- Gueltig ab ----
      if (ausweis.getGueltigVon() != null) {
         p.add(new Chunk(DateFormater.formatToDate(ausweis.getGueltigVon()) + "\n", createCourierNormal()));
      }

      // ---- Box ----
      if (ausweis.getBox() != null) {
         p.add(new Chunk(ausweis.getBox().getName() + "\n", createCourierNormal()));
      }

      return p;
   }

   private Phrase createPersPhrase(final Person person) {
      final Phrase p = new Phrase();

      // ---- AHV Nr ----
      if (person.getAhvNr() != null) {
         p.add(new Chunk(person.getAhvNr() + "\n", createCourierNormal()));
      }

      // ---- Name Vorname ----
      String name = "";
      if (person.getName() != null) {
         name = name.concat(person.getName());
         name = name.concat(" ");
      }
      if (person.getVorname() != null) {
         name = name.concat(person.getVorname());
      }
      p.add(new Chunk(name + "\n", FontFactory.getFont(FontFactory.COURIER, 8, Font.BOLD)));

      // ---- Funktion ----
      if (person.getFunktion() != null) {
         p.add(new Chunk(person.getFunktion() + ", ", createCourierNormal()));
      }

      // ---- Grad ----
      if (person.getGrad() != null) {
         p.add(new Chunk(person.getGrad().toString() + "\n", createCourierNormal()));
      } else {
         p.add(new Chunk("\n", createCourierNormal()));
      }

      // ---- Einheit ----
      if (person.getEinheit() != null) {
         p.add(new Chunk(person.getEinheit().getName() + "\n", createCourierNormal()));
      }

      // ---- Geburtsdatum ----
      if (person.getGeburtsdatum() != null) {
         p.add(new Chunk(DateFormater.formatToDate(person.getGeburtsdatum()), createCourierNormal()));
      }

      return p;

   }

   private Font createCourierNormal() {
      return FontFactory.getFont(FontFactory.COURIER, 5, Font.NORMAL);
   }

}
