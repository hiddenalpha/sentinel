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

	private List<Ausweis> ausweise;

	private String password;

	private AusweisvorlageKonfiguration config;

	private PdfContentByte pdfContentByte;

	public IdendityCardRenderer(List<Ausweis> ausweise, String password, AusweisvorlageKonfiguration config) {
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
			Rectangle querformat = new Rectangle(PageSize.A4.getHeight(), PageSize.A4.getWidth());
			Document document = new Document(querformat);

			// Streams
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			PdfWriter writer = PdfWriter.getInstance(document, outputStream);

			// Öffnen
			document.open();
			pdfContentByte = writer.getDirectContent();

			int offsetX = 50;
			int offsetY = 40;

			for (int counter = 0; counter < ausweise.size(); counter++) {

				Ausweis ausweis = ausweise.get(counter);

				IdentityCardSingleImageRenderer identityCard = new IdentityCardSingleImageRenderer(ausweis, password, config);
				BufferedImage bufferedImage = identityCard.createImage();

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

		} catch (Exception e) {
			e.printStackTrace();
			log.error(e);
		}
		return null;
	}

	private void neueSeite(Document document) throws DocumentException {
		document.add(Chunk.NEWLINE);
		document.newPage();
	}

	private void printAusweis(Ausweis ausweis, BufferedImage bufferedImage, int offsetX, int offsetY) throws DocumentException, IOException {
		int quality = 1;
		Image image = Image.getInstance(pdfContentByte, bufferedImage, quality);
		float width = (float) (image.getWidth() / 2.8);
		float height = (float) (image.getHeight() / 2.8);
		pdfContentByte.addImage(image, width, 0, 0, height, offsetX, offsetY);

		// Schriften werden mittels IText bedruckt. Sauberer Schriftzug!
		addBarcode(ausweis, offsetX, offsetY);
		addEinheitenText(ausweis, offsetX, offsetY);
		drawPersonenText(ausweis, offsetX, offsetY);
	}

	private void addBarcode(Ausweis ausweis, int offsetX, int offsetY) throws DocumentException {
		Barcode39 code39 = new Barcode39();
		code39.setCode(ausweis.getBarcode());
		code39.setAltText(BarCodeHelper.createBarCodeText(ausweis));
		Image image39 = code39.createImageWithBarcode(pdfContentByte, null, null);
		pdfContentByte.setColorFill(Color.black);
		pdfContentByte.addImage(image39, image39.getWidth(), 0, 0, image39.getHeight(), offsetX + 21.5f, offsetY + 16);
	}

	private BaseFont createBoldFont() throws DocumentException, IOException {
		return BaseFont.createFont(BaseFont.HELVETICA_BOLD, BaseFont.CP1252, BaseFont.NOT_EMBEDDED);
	}

	private BaseFont createDefaultFont() throws DocumentException, IOException {
		return BaseFont.createFont(BaseFont.HELVETICA, BaseFont.CP1252, BaseFont.NOT_EMBEDDED);
	}

	private void addEinheitenText(Ausweis ausweis, int offsetX, int offsetY) throws DocumentException, IOException {
		BaseFont font = createBoldFont();
		int size = 25;
		Color color = Color.white;
		Einheit einheit = ausweis.getPerson().getEinheit();
		float x = offsetX + 129.5f;
		offsetY += 173;
		int jumpY = -38;

		addText(font, size, einheit.getText_GsVb(), x, offsetY + 0 * jumpY, color);
		addText(font, size, einheit.getText_TrpK(), x, offsetY + 1 * jumpY, color);
		addText(font, size, einheit.getText_Einh(), x, offsetY + 2 * jumpY, color);
		addText(font, size, "000", x, offsetY + 3 * jumpY, color);
		addText(font, size, createBoxNrForPerson(ausweis.getPerson()), x, offsetY + 4 * jumpY, color);
	}

	private void addText(BaseFont font, int size, String text, float x, float y, Color color) {
		pdfContentByte.setColorFill(color);
		pdfContentByte.beginText();
		pdfContentByte.setFontAndSize(font, size);
		pdfContentByte.setTextMatrix(x, y);
		pdfContentByte.showText(text);
		pdfContentByte.endText();
	}

	private String createBoxNrForPerson(Person person) {
		int vorname = 100 * CodeHelper.getNoOfCharMod10(person.getVorname());
		int name = 10 * CodeHelper.getNoOfCharMod10(person.getName());
		int funktion = CodeHelper.getNoOfCharMod10(person.getFunktion());

		int summe = vorname + name + funktion;

		return String.valueOf(summe);
	}

	private void drawPersonenText(Ausweis ausweis, int offsetX, int offsetY) throws DocumentException, IOException {
		BaseFont fontBold = createBoldFont();
		BaseFont fontDefault = createDefaultFont();
		Person person = ausweis.getPerson();
		Color color = Color.black;
		float x = offsetX + 15.5f;

		if (person.getGrad() != null) {
			addText(fontDefault, 10, person.getGrad().getGradText(),  x, offsetY + 220, color);
		}

		addText(fontBold, 11, person.getVorname() + " " + person.getName(),  x, offsetY + 210, color);

		if (person.getGeburtsdatum() != null) {
			SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
			addText(fontDefault, 8, person.getAhvNr() + " / " + dateFormat.format(person.getGeburtsdatum().getTime()), x, offsetY + 202, color);
		}
	}

}
