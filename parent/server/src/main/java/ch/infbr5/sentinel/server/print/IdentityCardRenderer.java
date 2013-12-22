package ch.infbr5.sentinel.server.print;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Iterator;
import java.util.List;

import javax.imageio.ImageIO;

import ch.infbr5.sentinel.server.db.EntityManagerHelper;
import ch.infbr5.sentinel.server.db.ImageStore;
import ch.infbr5.sentinel.server.db.QueryHelper;
import ch.infbr5.sentinel.server.model.Ausweis;
import ch.infbr5.sentinel.server.model.ConfigurationValue;
import ch.infbr5.sentinel.server.model.Einheit;
import ch.infbr5.sentinel.server.model.Person;
import ch.infbr5.sentinel.server.utils.RgbStringHelper;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.lowagie.text.BadElementException;
import com.lowagie.text.Chunk;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Image;
import com.lowagie.text.Jpeg;
import com.lowagie.text.PageSize;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.Barcode39;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfWriter;
import com.thoughtworks.xstream.core.util.Base64Encoder;

public class IdentityCardRenderer extends PrintingDocument {

	public String toString() {
		return "Erstellte Ausweise";
	}

	public String getFileName() {
		return "ausweise";
	}

	protected byte[] renderPdf() {

		List<Ausweis> ausweise = QueryHelper.findAusweiseZumDrucken();

		try {

			int offsetX = 50;
			int offsetY = 40;

			int counter = 0;

			Rectangle A4_quer = new Rectangle(PageSize.A4.getHeight(), PageSize.A4.getWidth());
			Document document = new Document(A4_quer);

			ByteArrayOutputStream out = new ByteArrayOutputStream();
			PdfWriter writer = PdfWriter.getInstance(document, out);
			document.open();
			PdfContentByte cb = writer.getDirectContent();

			String password = "";
			List<ConfigurationValue> passwordList = QueryHelper.findConfigurationValue("IdentityCardPassword");
			if (passwordList.size() > 0) {
				password = passwordList.get(0).getStringValue();
			}

			for (Iterator<Ausweis> iterator = ausweise.iterator(); iterator.hasNext();) {
				Ausweis ausweis = iterator.next();
				counter++;

				if (counter % 4 == 1)
					printAusweis(offsetX, offsetY + 270, cb, ausweis, password);
				if (counter % 4 == 2)
					printAusweis(offsetX + 400, offsetY + 270, cb, ausweis, password);
				if (counter % 4 == 3)
					printAusweis(offsetX, offsetY, cb, ausweis, password);
				if (counter % 4 == 0)
					printAusweis(offsetX + 400, offsetY, cb, ausweis, password);

				if (counter % 4 == 0)
					neueSeite(document);

				ausweis.setErstellt(true);
				EntityManagerHelper.getEntityManager().persist(ausweis);

			}

			document.close();
			return out.toByteArray();

		} catch (BadElementException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

		} finally {

		}
		return null;

	}

	private void neueSeite(Document document) throws DocumentException {
		document.add(Chunk.NEWLINE);
		document.newPage();
	}

	private void printAusweis(int offsetX, int offsetY, PdfContentByte cb, Ausweis ausweis, String password)
			throws BadElementException, IOException, DocumentException {
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
		Person person = ausweis.getPerson();
		Einheit einheit = person.getEinheit();

		addBackgroundImage(cb, offsetX, offsetY, person);

		String barcodeText = "";
		if (ausweis.getGueltigVon() != null) {
			barcodeText = " ** ".concat(dateFormat.format(ausweis.getGueltigVon())).concat(" ** ");
		}
		addFrontBarCode(cb, offsetX, offsetY, ausweis.getBarcode(), barcodeText);

		BaseFont bfBold = BaseFont.createFont(BaseFont.HELVETICA_BOLD, BaseFont.CP1252, BaseFont.NOT_EMBEDDED);
		BaseFont bf = BaseFont.createFont(BaseFont.HELVETICA, BaseFont.CP1252, BaseFont.NOT_EMBEDDED);
		BaseFont bfSign = BaseFont.createFont(BaseFont.COURIER, BaseFont.CP1252, BaseFont.NOT_EMBEDDED);

		dateFormat = new SimpleDateFormat("dd.MM.yyyy");
		printText(offsetX + 6, offsetY + 200, cb, bfBold, 11, person.getVorname().concat(" ".concat(person.getName())));
		if (person.getGrad() != null)
			printText(offsetX + 6, offsetY + 212, cb, bf, 10, person.getGrad().toString());

		if (person.getGeburtsdatum() != null) {
			printText(offsetX + 6, offsetY + 190, cb, bf, 8,
					person.getAhvNr().concat(" / ").concat(dateFormat.format(person.getGeburtsdatum().getTime())));
		}

		byte[] bild = ImageStore.loadJpegImage(person.getAhvNr());
		if (bild != null)
			addPersonImage(cb, offsetX, offsetY, ImageStore.byteArrayToBufferedImage(bild), getNoOfCharMod10(person.getName()));

		printRectanleCodes(offsetX + 113, offsetY + 150 - 0 * 36, cb, bfBold, einheit.getRgbColor_GsVb(), einheit.getText_GsVb());
		printRectanleCodes(offsetX + 113, offsetY + 150 - 1 * 36, cb, bfBold, einheit.getRgbColor_TrpK(), einheit.getText_TrpK());
		printRectanleCodes(offsetX + 113, offsetY + 150 - 2 * 36, cb, bfBold, einheit.getRgbColor_Einh(), einheit.getText_Einh());
		printRectanleCodes(offsetX + 113, offsetY + 150 - 3 * 36, cb, bfBold, "", "000");
		String boxNr = String.valueOf(100 * getNoOfCharMod10(person.getVorname()) + 10 * getNoOfCharMod10(person.getName())
				+ getNoOfCharMod10(person.getFunktion()));
		printRectanleCodes(offsetX + 113, offsetY + 150 - 4 * 36, cb, bfBold, "", boxNr);

		addBackBarCode(cb, offsetX + 168, offsetY, getQrContent(person, ausweis, password));

	}

	private int getNoOfCharMod10(String text) {
		if (text != null) {
			return text.length() % 10;
		} else {
			return 0;
		}
	}

	/**
	 * Generiert Content für QC Barcode auf Rückseite
	 * 
	 * @param p
	 *            Person
	 * @param a
	 *            Ausweis
	 * @return
	 */
	private String getQrContent(Person p, Ausweis a, String password) {

		String c = "";

		// Grad
		if (a.getBarcode() != null) {
			c = c + "B" + a.getBarcode() + "\n";
		}

		// Grad
		if (p.getGrad() != null) {
			c = c + "G" + p.getGrad().toString() + "\n";
		}

		// Name
		if (p.getName() != null) {
			c = c + "N" + p.getName() + "\n";
		}

		// Vorname
		if (p.getVorname() != null) {
			c = c + "V" + p.getVorname() + "\n";
		}

		// Funktion
		c = c + "F" + p.getFunktion() + "\n";

		// Einheitsname
		if (p.getEinheit() != null)
			c = c + "E" + p.getEinheit().getName() + "\n";

		// Geburtsdatum
		if (p.getGeburtsdatum() != null) {
			SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
			c = c + "D" + dateFormat.format(p.getGeburtsdatum().getTime()) + "\n";
		}

		// AVH Nr
		c = c + "A" + p.getAhvNr() + "\n";

		// Signatur
		try {
			MessageDigest sha1 = MessageDigest.getInstance("SHA1");
			byte[] digest = sha1.digest(password.concat(c).getBytes());
			c = c + "S" + new Base64Encoder().encode(digest);
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return c;
	}

	private void printRectanleCodes(int offsetX, int offsetY, PdfContentByte cb, BaseFont bf, String color, String text)
			throws BadElementException, IOException, DocumentException {

		RgbStringHelper rgb = new RgbStringHelper(color);

		cb.setRGBColorFill(rgb.getR(), rgb.getG(), rgb.getB());
		// fill a rectangle in state 1
		cb.rectangle(offsetX, offsetY, 55, 30);
		cb.fill();

		cb.setColorFill(new Color(0xFF, 0xFF, 0xFF));
		printText(offsetX + 6, offsetY + 5, cb, bf, 25, text);
		cb.setColorFill(new Color(0x00, 0x00, 0x00));
	}

	private void printText(int x, int y, PdfContentByte cb, BaseFont bf, int size, String text) {
		cb.beginText();
		cb.setFontAndSize(bf, size);
		cb.setTextMatrix(x, y);
		cb.showText(text);
		cb.endText();
	}

	private void addPersonImage(PdfContentByte cb, int offsetWidth, int offsetHeight, BufferedImage img, int nofYellowBoxes)
			throws IOException, DocumentException {

		Image imgJpeg = new Jpeg(modifyImage(img, nofYellowBoxes).toByteArray());
		cb.addImage(imgJpeg, 100, 0, 0, 133, offsetWidth + 6, offsetHeight + 48);
	}

	private void addFrontBarCode(PdfContentByte cb, int offsetWidth, int offsetHeight, String barcode, String alttext)
			throws DocumentException {

		// A - CODE
		Barcode39 code39 = new Barcode39();
		code39.setCode(barcode);
		code39.setAltText(alttext);
		Image image39 = code39.createImageWithBarcode(cb, null, null);

		image39.setBackgroundColor(Color.WHITE);
		cb.addImage(image39, image39.getWidth(), 0, 0, image39.getHeight(), offsetWidth + 10, offsetHeight + 6);

	}

	private void addBackBarCode(PdfContentByte cb, int offsetWidth, int offsetHeight, String content) throws DocumentException {

		try {
			Image imgJpeg = new Jpeg(createQrCode(content, 150, 150).toByteArray());
			cb.addImage(imgJpeg, imgJpeg.getWidth(), 0, 0, imgJpeg.getHeight(), offsetWidth + 13, offsetHeight + 6);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private void addBackgroundImage(PdfContentByte cb, int offsetWidth, int offsetHeight, Person person)
			throws BadElementException, IOException, DocumentException {

		Jpeg image = new Jpeg(IdentityCardRenderer.class.getResource("/images/AusweisVorlage.jpg"));
		float width = (float) (image.getWidth() / 2.8);
		float height = (float) (image.getHeight() / 2.8);
		cb.addImage(image, width, 0, 0, height, offsetWidth - 7, offsetHeight - 8);
	}

	private ByteArrayOutputStream modifyImage(BufferedImage pic, int nofBoxGelb) throws IOException {

		BufferedImage boxGelb = ImageIO.read(IdentityCardRenderer.class.getResource("/images/box_gelb.jpg"));

		BufferedImage watermark = ImageIO.read(IdentityCardRenderer.class.getResource("/images/emblem.png"));
		java.awt.Image watermarkScale = watermark.getScaledInstance(watermark.getWidth() * pic.getWidth() / 300,
				watermark.getHeight() * pic.getHeight() / 400, java.awt.Image.SCALE_DEFAULT);

		Graphics2D g2d = (Graphics2D) pic.getGraphics();
		g2d.drawImage(pic, 0, 0, null);

		// Mit float-wert kann die Transparenz prozentual eingestellt werden
		// 1.0f -> voll sichtbar // 0.0f -> ganz durchsichtig
		AlphaComposite alpha = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.3f);
		g2d.setComposite(alpha); // Wasserzeichen in die obere linke ecke
									// zeichnen

		g2d.drawImage(watermarkScale, 0, 0, null);

		alpha = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f);
		g2d.setComposite(alpha);
		for (int i = 0; i < nofBoxGelb; i++) {
			g2d.drawImage(boxGelb, pic.getWidth() - 4, pic.getHeight() - 4 - (i * 4), null);
		}

		ByteArrayOutputStream imgOut = new ByteArrayOutputStream();

		ImageIO.write(pic, "jpg", imgOut);

		return imgOut;

	}

	public ByteArrayOutputStream createQrCode(String content, int width, int height) {

		QRCodeWriter writer = new QRCodeWriter();
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		BitMatrix bitMatrix = null;

		try {
			bitMatrix = writer.encode(content, BarcodeFormat.QR_CODE, width, height);
			MatrixToImageWriter.writeToStream(bitMatrix, "jpg", stream);
			return stream;

		} catch (WriterException | IOException e) {
			e.printStackTrace();

		}
		return null;
	}

}
