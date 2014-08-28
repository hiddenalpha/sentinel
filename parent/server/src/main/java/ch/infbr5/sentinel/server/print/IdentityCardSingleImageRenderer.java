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

	private Color backgroundColor;

	private Graphics2D g;

	private AusweisvorlageKonfiguration config;

	private Ausweis ausweis;

	private Person person;

	private Einheit einheit;

	private Image imagePerson;

	private String password;

	public IdentityCardSingleImageRenderer(Ausweis ausweis, String password, AusweisvorlageKonfiguration config) {
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

		backgroundColor = Color.decode(config.getColorBackground());
	}

	public BufferedImage createImage() {
		BufferedImage image = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_RGB);
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
		int mitteEineSeite = (((imageWidth / 2) - border) / 2) + border;
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

		// R�ckseite - Spezial Fl�che
		if (config.isShowAreaBackside()) {
			drawSpecialArea();
		}

		// Logo (z.B. vom Batallion) erste Seite oben rechts
		drawLogo();

		g.dispose();
		return image;
	}

	private void drawBarcode() {
		int padding = 20;
		int xStart = border + padding;
		int height = 102;
		int yStart = imageHeight - border - padding - 100;
		int width = 300;

		// Barcode - Fl�che
		g.setColor(Color.white);
		g.fillRect(xStart, yStart, width, height);
	}

	private void drawQRCode() {
		BufferedImage qRCodeimage = QRCodeHelper.createQrCodeImage(ausweis, password, 390, 390);
		g.drawImage(qRCodeimage, 540, 318, null);
	}

	private void drawEinheit() {
		int yStart = 200;
		int height = 93;
		int gap = 14;

		// TODO Should be configurable!
		String defaultEinheitColor = "#000";

		String colorGsVb = defaultEinheitColor;
		if (einheit.getRgbColor_GsVb() != null && !einheit.getRgbColor_GsVb().isEmpty()) {
			colorGsVb = einheit.getRgbColor_GsVb();
		}

		String colorTrpK = defaultEinheitColor;
		if (einheit.getRgbColor_TrpK() != null && !einheit.getRgbColor_TrpK().isEmpty()) {
			colorTrpK = einheit.getRgbColor_TrpK();
		}

		String colorEinh = defaultEinheitColor;
		if (einheit.getRgbColor_Einh() != null && !einheit.getRgbColor_Einh().isEmpty()) {
			colorEinh = einheit.getRgbColor_Einh();
		}

		drawEinheitKasten(Color.decode(colorGsVb), yStart + height * 0 + gap * 0);
		drawEinheitKasten(Color.decode(colorTrpK), yStart + height * 1 + gap * 1);
		drawEinheitKasten(Color.decode(colorEinh), yStart + height * 2 + gap * 2);
		drawEinheitKasten(Color.decode(defaultEinheitColor), yStart + height * 3 + gap * 3);
		drawEinheitKasten(Color.decode(defaultEinheitColor), yStart + height * 4 + gap * 4);
	}

	private void drawEinheitKasten(Color color, int y) {
		int xStart = border + 20 + 300 + 10;
		int width = 135;
		g.setColor(color);
		g.fillRect(xStart, y, width, 80);
	}

	private void drawPersonenBild() {
		int xStart = border + 20;
		int yStart = 200;

		// Bild - Wasserzeichen
		addWasserzeichen(imagePerson);

		// Bild - Kleine P�nktchen hinzuf�gen
		addDots(imagePerson);

		// Bild - Bild
		// Aus Sicherheitsgr�nden wird hier h�he und breite dennoch mitgegeben, dass es
		// bei falsch abgespeicherten Bildern zu keinen Problemen kommt!
		g.drawImage(imagePerson, xStart, yStart, 300, 400, null);
	}

	private void addDots(Image image) {
		Graphics2D gImage = (Graphics2D) image.getGraphics();
		gImage.setColor(backgroundColor);
		for (int i = 0; i < calculateAnzahlDots(); i++) {
			int xStart = image.getWidth(null) - 4;
			int yStart = image.getHeight(null) - 4 - (i * 4);
			int xEnde = xStart + 4;
			gImage.drawLine(xStart, yStart, xEnde, yStart);
			gImage.drawLine(xStart, yStart + 1, xEnde, yStart + 1);
		}
	}

	private int calculateAnzahlDots() {
		return CodeHelper.getNoOfCharMod10(person.getName());
	}

	private void drawSpecialArea() {
		int xStart = 540;
		int width = 390;
		g.setColor(Color.decode(config.getColorAreaBackside()));
		g.fillRect(xStart, 113, width, 187);
	}

	private void drawLogo() {
		try {
			if (config.isUseUserLogo() && config.getLogo() != null && config.getLogo().length > 0) {
				Image logo = ImageIO.read(new ByteArrayInputStream(config.getLogo()));

				logo = ImageUtil.scaleImage(logo, 135, 140);

				int logoWidth = logo.getWidth(null);
				// Start bei H�he der Einheitsk�stchen, addieren der halben maximalen Gr�sse des Bildes subtrahieren der Halben effektiven Gr�sse des Bildes!
				int x = 355 + (135 / 2) - (logoWidth / 2);

				g.drawImage(logo, x, 45, null);
			}
		} catch (IOException e) {
			throw new IllegalStateException(e);
		}
	}

	private void addWasserzeichen(Image image) {
		// Bild - Wasserzeichen
		Image watermark;
		try {
			if (!config.isUseUserWasserzeichen() || config.getWasserzeichen() == null || config.getWasserzeichen().length == 0) {
				watermark = ImageIO.read(new ByteArrayInputStream(config.getDefaultWasserzeichen()));
			} else {

				watermark = ImageIO.read(new ByteArrayInputStream(config.getWasserzeichen()));
			}
		} catch (IOException e) {
			throw new IllegalStateException(e);
		}

		// Skalieren
		Image watermarkScale = ImageUtil.scaleImage(watermark, 130, 130);

		// Transparenz
		Graphics2D gImage = (Graphics2D) image.getGraphics();
		gImage.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.3f));

		// Wasserzeichen in die obere linke ecke zeiichnen
		gImage.drawImage(watermarkScale, 0, 0, null);

		// Alpha Kanel wieder zur�ckstellen
		gImage.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
	}



}
