package ch.infbr5.sentinel.server.print;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;

import javax.imageio.ImageIO;

import ch.infbr5.sentinel.common.util.ImageUtil;
import ch.infbr5.sentinel.server.db.ImageStore;
import ch.infbr5.sentinel.server.model.Ausweis;
import ch.infbr5.sentinel.server.model.Einheit;
import ch.infbr5.sentinel.server.model.Person;
import ch.infbr5.sentinel.server.ws.AusweisvorlageKonfiguration;

public class IdentityCardSingleImageRenderer {

   private static final int imageWidth = 1002;

   private static final int imageHeight = 748;

   private static final int border = 24;

   private final Color backgroundColor;

   private Graphics2D g;

   private final AusweisvorlageKonfiguration config;

   private final Ausweis ausweis;

   private final Person person;

   private final Einheit einheit;

   private final Image imagePerson;

   private final String password;

   public IdentityCardSingleImageRenderer(final Ausweis ausweis, final String password,
         final AusweisvorlageKonfiguration config) {
      if (ausweis == null || password == null || password.isEmpty() || config == null) {
         throw new IllegalArgumentException("Ung�ltige Parameter");
      }
      this.ausweis = ausweis;
      this.person = ausweis.getPerson();
      this.einheit = person.getEinheit();
      this.password = password;
      this.config = config;
      this.imagePerson = ImageStore.getImage(person);
      if (person == null || einheit == null || imagePerson == null) {
         throw new IllegalArgumentException("Ung�ltige Parameter");
      }

      // TODO Schoener machen!
      Color c;
      String color = config.getColorBackground();
      try {
         if (color == null || color.isEmpty()) {
            c = Color.white;
         } else if (!color.startsWith("#")) {
            color = "#" + color;
            c = Color.decode(color);
         } else {
            c = Color.decode(color);
         }
      } catch (final NumberFormatException e) {
         c = Color.white;
      }

      backgroundColor = c;
   }

   public BufferedImage createImage() {
      final BufferedImage image = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_RGB);
      g = (Graphics2D) image.getGraphics();

      // Ganzes Bild, weisser Hintergrund
      g.setColor(Color.white);
      g.fillRect(0, 0, imageWidth, imageHeight);

      // Mittlerer Teil dunkelgelb
      g.setColor(backgroundColor);
      g.fillRect(border + 1, border + 1, imageWidth - 2 * border - 1, imageHeight - 2 * border - 1);

      // Eckenlinien einzeichnen
      g.setColor(Color.black);

      // Eckenlinien - Links oben
      g.drawLine(border, border, 0, border);
      g.drawLine(border, border, border, 0);

      // Eckenlinien - Links unten
      g.drawLine(border, imageHeight - border, 0, imageHeight - border);
      g.drawLine(border, imageHeight - border, border, imageHeight);

      // Eckenlinien - Rechts oben
      g.drawLine(imageWidth - border, border, imageWidth - border, 0);
      g.drawLine(imageWidth - border, border, imageWidth, border);

      // Eckenlinine - Rechts unten
      g.drawLine(imageWidth - border, imageHeight - border, imageWidth, imageHeight - border);
      g.drawLine(imageWidth - border, imageHeight - border, imageWidth - border, imageHeight);

      // Mittellinien - F�r den Falz
      g.drawLine(imageWidth / 2, 0, imageWidth / 2, border);
      g.drawLine(imageWidth / 2, imageHeight - border, imageWidth / 2, imageHeight);

      // Loch-Linie - Zum Lochen
      final int mitteEineSeite = (((imageWidth / 2) - border) / 2) + border;
      g.drawLine(mitteEineSeite, 45, mitteEineSeite, 80);

      // Personenbild
      drawPersonenBild();

      // Barcode
      drawBarcode();

      // Einheit
      drawEinheit();

      // QR-Code
      if (config.isShowQRCode()) {
         drawQRCode();
      }

      // Rueckseite - Spezial Fl�che
      if (config.isShowAreaBackside()) {
         drawSpecialArea();
      }

      // Logo (z.B. vom Batallion) erste Seite oben rechts
      drawLogo();

      g.dispose();
      return image;
   }

   private void drawBarcode() {
      final int padding = 20;
      final int xStart = border + padding;
      final int height = 102;
      final int yStart = imageHeight - border - padding - 100;
      final int width = 300;

      // Barcode - Fl�che
      g.setColor(Color.white);
      g.fillRect(xStart, yStart, width, height);
   }

   private void drawQRCode() {
      final BufferedImage qRCodeimage = QRCodeHelper.createQrCodeImage(ausweis, password, 390, 390);
      g.drawImage(qRCodeimage, 540, 318, null);
   }

   private void drawEinheit() {
      final int yStart = 200;
      final int height = 93;
      final int gap = 14;

      // TODO Should be configurable!
      final String defaultEinheitColor = "#000";
      final Color defaultColor = Color.decode(defaultEinheitColor);

      String colorGsVb = defaultEinheitColor;
      if (einheit.getRgbColor_GsVb() != null && !einheit.getRgbColor_GsVb().isEmpty()) {
         colorGsVb = einheit.getRgbColor_GsVb();
         if (!colorGsVb.startsWith("#")) {
            colorGsVb = "#" + colorGsVb;
         }
      }

      String colorTrpK = defaultEinheitColor;
      if (einheit.getRgbColor_TrpK() != null && !einheit.getRgbColor_TrpK().isEmpty()) {
         colorTrpK = einheit.getRgbColor_TrpK();
         if (!colorTrpK.startsWith("#")) {
            colorTrpK = "#" + colorTrpK;
         }
      }

      String colorEinh = defaultEinheitColor;
      if (einheit.getRgbColor_Einh() != null && !einheit.getRgbColor_Einh().isEmpty()) {
         colorEinh = einheit.getRgbColor_Einh();
         if (!colorEinh.startsWith("#")) {
            colorEinh = "#" + colorEinh;
         }
      }

      Color cGsVb;
      try {
         cGsVb = Color.decode(colorGsVb);
      } catch (final NumberFormatException e) {
         cGsVb = defaultColor;
      }

      Color cTrpK;
      try {
         cTrpK = Color.decode(colorTrpK);
      } catch (final NumberFormatException e) {
         cTrpK = defaultColor;
      }

      Color cEinh;
      try {
         cEinh = Color.decode(colorEinh);
      } catch (final NumberFormatException e) {
         cEinh = defaultColor;
      }

      drawEinheitKasten(cGsVb, yStart + height * 0 + gap * 0);
      drawEinheitKasten(cTrpK, yStart + height * 1 + gap * 1);
      drawEinheitKasten(cEinh, yStart + height * 2 + gap * 2);
      drawEinheitKasten(defaultColor, yStart + height * 3 + gap * 3);
      drawEinheitKasten(defaultColor, yStart + height * 4 + gap * 4);
   }

   private void drawEinheitKasten(final Color color, final int y) {
      final int xStart = border + 20 + 300 + 10;
      final int width = 135;
      g.setColor(color);
      g.fillRect(xStart, y, width, 80);
   }

   private void drawPersonenBild() {
      final int xStart = border + 20;
      final int yStart = 200;

      // Bild - Wasserzeichen
      final Image watermark = addWasserzeichen();

      // Bild - Kleine P�nktchen hinzuf�gen
      addDots(imagePerson);

      // Bild - Bild
      // Aus Sicherheitsgr�nden wird hier h�he und breite dennoch mitgegeben,
      // dass es
      // bei falsch abgespeicherten Bildern zu keinen Problemen kommt!
      g.drawImage(imagePerson, xStart, yStart, 300, 400, null);

      // Wasserzeichen nun ueber Person legen
      // Transparenz
      g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.3f));

      // Wasserzeichen in die obere linke ecke zeiichnen
      g.drawImage(watermark, xStart, yStart, null);

      // Alpha Kanel wieder zur�ckstellen
      g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
   }

   private void addDots(final Image image) {
      final Graphics2D gImage = (Graphics2D) image.getGraphics();
      gImage.setColor(backgroundColor);
      for (int i = 0; i < calculateAnzahlDots(); i++) {
         final int xStart = image.getWidth(null) - 4;
         final int yStart = image.getHeight(null) - 4 - (i * 4);
         final int xEnde = xStart + 4;
         gImage.drawLine(xStart, yStart, xEnde, yStart);
         gImage.drawLine(xStart, yStart + 1, xEnde, yStart + 1);
      }
   }

   private int calculateAnzahlDots() {
      return CodeHelper.getNoOfCharMod10(person.getName());
   }

   private void drawSpecialArea() {
      final int xStart = 540;
      final int width = 390;

      Color c;

      String color = config.getColorAreaBackside();
      try {
         if (color == null || color.isEmpty()) {
            c = Color.white;
         } else if (!color.startsWith("#")) {
            color = "#" + color;
            c = Color.decode(color);
         } else {
            c = Color.decode(color);
         }
      } catch (final NumberFormatException e) {
         c = Color.white;
      }

      g.setColor(c);
      g.fillRect(xStart, 113, width, 187);
   }

   private void drawLogo() {
      try {
         if (config.isUseUserLogo() && config.getLogo() != null && config.getLogo().length > 0) {
            Image logo = ImageIO.read(new ByteArrayInputStream(config.getLogo()));

            logo = ImageUtil.scaleImage(logo, 135, 140);

            final int logoWidth = logo.getWidth(null);
            // Start bei Hoehe der Einheitskaestchen, addieren der halben
            // maximalen Groesse des Bildes subtrahieren der Halben
            // effektiven Groesse des Bildes!
            final int x = 355 + (135 / 2) - (logoWidth / 2);

            g.drawImage(logo, x, 45, null);
         }
      } catch (final IOException e) {
         throw new IllegalStateException(e);
      }
   }

   private Image addWasserzeichen() {
      // Bild - Wasserzeichen
      Image watermark;
      try {
         if (!config.isUseUserWasserzeichen() || config.getWasserzeichen() == null
               || config.getWasserzeichen().length == 0) {
            watermark = ImageIO.read(new ByteArrayInputStream(config.getDefaultWasserzeichen()));
         } else {

            watermark = ImageIO.read(new ByteArrayInputStream(config.getWasserzeichen()));
         }
      } catch (final IOException e) {
         throw new IllegalStateException(e);
      }

      // Skalieren
      // Wenn ich hier eine fixe Hoehe und breite definieren, dann gibt es ein
      // problem.
      final Image watermarkScale = ImageUtil.scaleImage(watermark, 130, 130);

      return watermarkScale;
   }

}
