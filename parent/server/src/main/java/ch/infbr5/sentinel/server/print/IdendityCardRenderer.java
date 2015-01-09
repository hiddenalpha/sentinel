package ch.infbr5.sentinel.server.print;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;

import org.apache.log4j.Logger;

import ch.infbr5.sentinel.server.model.Ausweis;
import ch.infbr5.sentinel.server.model.Einheit;
import ch.infbr5.sentinel.server.model.Person;
import ch.infbr5.sentinel.server.ws.AusweisvorlageKonfiguration;

import com.lowagie.text.Chunk;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Image;
import com.lowagie.text.PageSize;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.Barcode39;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfWriter;

public class IdendityCardRenderer extends PDFRenderer {

   private static final Logger log = Logger.getLogger(IdendityCardRenderer.class);

   private final List<Ausweis> ausweise;

   private final String password;

   private final AusweisvorlageKonfiguration config;

   private PdfContentByte pdfContentByte;

   public IdendityCardRenderer(final List<Ausweis> ausweise, final String password,
         final AusweisvorlageKonfiguration config) {
      if (ausweise == null || password == null || password.isEmpty() || config == null) {
         throw new IllegalArgumentException("Parameter ungültigt");
      }
      this.ausweise = ausweise;
      this.password = password;
      this.config = config;
   }

   @Override
   public String getFileName() {
      return "ausweise";
   }

   @Override
   protected String getBeschreibung() {
      return "Erstellte Ausweise";
   }

   @Override
   protected byte[] renderPdf() {

      if (ausweise.isEmpty()) {
         return null;
      }

      try {
         // Querformat
         final Rectangle querformat = new Rectangle(PageSize.A4.getHeight(), PageSize.A4.getWidth());
         final Document document = new Document(querformat);

         // Streams
         final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
         final PdfWriter writer = PdfWriter.getInstance(document, outputStream);

         // Oeffnen
         document.open();
         pdfContentByte = writer.getDirectContent();

         final int offsetX = 50;
         final int offsetY = 40;

         for (int counter = 0; counter < ausweise.size(); counter++) {

            final Ausweis ausweis = ausweise.get(counter);

            final IdentityCardSingleImageRenderer identityCard = new IdentityCardSingleImageRenderer(ausweis, password,
                  config);
            final BufferedImage bufferedImage = identityCard.createImage();

            if (counter % 4 == 0) {
               printAusweis(ausweis, bufferedImage, offsetX, offsetY + 270);
            } else if (counter % 4 == 1) {
               printAusweis(ausweis, bufferedImage, offsetX + 400, offsetY + 270);
            } else if (counter % 4 == 2) {
               printAusweis(ausweis, bufferedImage, offsetX, offsetY);
            } else if (counter % 4 == 3) {
               printAusweis(ausweis, bufferedImage, offsetX + 400, offsetY);
               neueSeite(document);
            }
         }

         // Schliessen
         document.close();
         return outputStream.toByteArray();

      } catch (final Exception e) {
         e.printStackTrace();
         log.error(e);
      }
      return null;
   }

   private void neueSeite(final Document document) throws DocumentException {
      document.add(Chunk.NEWLINE);
      document.newPage();
   }

   private void printAusweis(final Ausweis ausweis, final BufferedImage bufferedImage, final int offsetX,
         final int offsetY) throws DocumentException, IOException {
      final int quality = 1;
      final Image image = Image.getInstance(pdfContentByte, bufferedImage, quality);
      final float width = (float) (image.getWidth() / 2.8);
      final float height = (float) (image.getHeight() / 2.8);
      pdfContentByte.addImage(image, width, 0, 0, height, offsetX, offsetY);

      // Schriften werden mittels IText bedruckt. Sauberer Schriftzug!
      addBarcode(ausweis, offsetX, offsetY);
      addEinheitenText(ausweis, offsetX, offsetY);
      drawPersonenText(ausweis, offsetX, offsetY);
   }

   private void addBarcode(final Ausweis ausweis, final int offsetX, final int offsetY) throws DocumentException {
      final Barcode39 code39 = new Barcode39();
      code39.setCode(ausweis.getBarcode());
      code39.setAltText(BarCodeHelper.createBarCodeText(ausweis));
      final Image image39 = code39.createImageWithBarcode(pdfContentByte, null, null);
      pdfContentByte.setColorFill(Color.black);
      pdfContentByte.addImage(image39, image39.getWidth(), 0, 0, image39.getHeight(), offsetX + 21.5f, offsetY + 16);
   }

   private BaseFont createBoldFont() throws DocumentException, IOException {
      return BaseFont.createFont(BaseFont.HELVETICA_BOLD, BaseFont.CP1252, BaseFont.NOT_EMBEDDED);
   }

   private BaseFont createDefaultFont() throws DocumentException, IOException {
      return BaseFont.createFont(BaseFont.HELVETICA, BaseFont.CP1252, BaseFont.NOT_EMBEDDED);
   }

   private void addEinheitenText(final Ausweis ausweis, final int offsetX, int offsetY) throws DocumentException,
   IOException {
      final BaseFont font = createBoldFont();
      final int size = 25;
      final Color color = Color.white;
      final Einheit einheit = ausweis.getPerson().getEinheit();
      final float x = offsetX + 129.5f;
      offsetY += 173;
      final int jumpY = -38;

      addText(font, size, einheit.getText_GsVb(), x, offsetY + 0 * jumpY, color);
      addText(font, size, einheit.getText_TrpK(), x, offsetY + 1 * jumpY, color);
      addText(font, size, einheit.getText_Einh(), x, offsetY + 2 * jumpY, color);
      addText(font, size, "000", x, offsetY + 3 * jumpY, color);
      addText(font, size, createBoxNrForPerson(ausweis.getPerson()), x, offsetY + 4 * jumpY, color);
   }

   private void addText(final BaseFont font, final int size, final String text, final float x, final float y,
         final Color color) {
      pdfContentByte.setColorFill(color);
      pdfContentByte.beginText();
      pdfContentByte.setFontAndSize(font, size);
      pdfContentByte.setTextMatrix(x, y);
      pdfContentByte.showText(text);
      pdfContentByte.endText();
   }

   private String createBoxNrForPerson(final Person person) {
      final int vorname = 100 * CodeHelper.getNoOfCharMod10(person.getVorname());
      final int name = 10 * CodeHelper.getNoOfCharMod10(person.getName());
      final int funktion = CodeHelper.getNoOfCharMod10(person.getFunktion());

      final int summe = vorname + name + funktion;

      return String.valueOf(summe);
   }

   private void drawPersonenText(final Ausweis ausweis, final int offsetX, final int offsetY) throws DocumentException,
   IOException {
      final BaseFont fontBold = createBoldFont();
      final BaseFont fontDefault = createDefaultFont();
      final Person person = ausweis.getPerson();
      final Color color = Color.black;
      final float x = offsetX + 15.5f;

      if (person.getGrad() != null) {
         addText(fontDefault, 10, person.getGrad().getGradText(), x, offsetY + 220, color);
      }

      addText(fontBold, 11, person.getVorname() + " " + person.getName(), x, offsetY + 210, color);

      if (person.getGeburtsdatum() != null) {
         final SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
         addText(fontDefault, 8, person.getAhvNr() + " / " + dateFormat.format(person.getGeburtsdatum().getTime()), x,
               offsetY + 202, color);
      }
   }

}
