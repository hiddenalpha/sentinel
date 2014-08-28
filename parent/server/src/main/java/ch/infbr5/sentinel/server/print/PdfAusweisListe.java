package ch.infbr5.sentinel.server.print;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

import ch.infbr5.sentinel.server.db.ImageStore;
import ch.infbr5.sentinel.server.model.Ausweis;
import ch.infbr5.sentinel.server.model.Person;
import ch.infbr5.sentinel.server.utils.DateHelper;

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

public class PdfAusweisListe extends PDFRenderer {

	private boolean nurMitAusweis = true;
	private boolean nachEinheit = false;
	private String einheitName;

	private List<Person> personen;

	public PdfAusweisListe(List<Person> personen, boolean nurMitAusweis, boolean nachEinheit, String einheitName) {
		this.personen = personen;
		this.nurMitAusweis = nurMitAusweis;
		this.nachEinheit = nachEinheit;
		this.einheitName = einheitName;
	}

	@Override
	protected String getFileName() {
		return "ausweisListe";
	}

	@Override
	public String getBeschreibung() {
		String parameter = "";

		if (nurMitAusweis)
			parameter = "nur Ausweise ";

		if (nachEinheit) {
			parameter = parameter + "nach Einheit";
		} else {
			parameter = parameter + "nach Name";
		}

		return "Ausweisliste ("+parameter+")";
	}

	@Override
	protected byte[] renderPdf() {

		if (personen.isEmpty()) {
			return null;
		}

		Document document = new Document();
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		try {
			PdfWriter.getInstance(document, out);
			document.open();

			// Tabellen Layout
			PdfPTable table = new PdfPTable(4);
			float[] columnWidth = { 30, 130, 130, 200 };
			table.setTotalWidth(columnWidth);
			table.setLockedWidth(true);

			for (Person person : personen) {
				// Spalte 1 --- Foto ---
				Image imgJpeg;
				byte[] bild = ImageStore.loadJpegImage(person.getAhvNr());
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
		} catch (DocumentException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		document.close();
		return out.toByteArray();
	}

	private Phrase createAusweisPhrase(Ausweis ausweis) {
		Phrase p = new Phrase();

		p.add(new Chunk(ausweis.getBarcode() + "\n", FontFactory.getFont(FontFactory.COURIER, 5, Font.NORMAL)));

		// ---- Gueltig ab ----
		if (ausweis.getGueltigVon() != null)
			p.add(new Chunk(DateHelper.getFormatedString(ausweis.getGueltigVon()) + "\n", FontFactory.getFont(
					FontFactory.COURIER, 5, Font.NORMAL)));

		// ---- Box ----
		if (ausweis.getBox() != null)
			p.add(new Chunk(ausweis.getBox().getName() + "\n", FontFactory.getFont(FontFactory.COURIER, 5, Font.NORMAL)));

		return p;
	}

	private Phrase createPersPhrase(Person person) {
		Phrase p = new Phrase();

		// ---- AHV Nr ----
		if (person.getAhvNr() != null)
			p.add(new Chunk(person.getAhvNr() + "\n", FontFactory.getFont(FontFactory.COURIER, 5, Font.NORMAL)));

		// ---- Name Vorname ----
		String name = "";
		if (person.getName() != null) {
			name = name.concat(person.getName());
			name = name.concat(" ");
		}
		if (person.getVorname() != null)
			name = name.concat(person.getVorname());
		p.add(new Chunk(name + "\n", FontFactory.getFont(FontFactory.COURIER, 8, Font.BOLD)));

		// ---- Funktion ----
		if (person.getFunktion() != null)
			p.add(new Chunk(person.getFunktion() + ", ", FontFactory.getFont(FontFactory.COURIER, 5, Font.NORMAL)));

		// ---- Grad ----
		if (person.getGrad() != null) {
			p.add(new Chunk(person.getGrad().toString() + "\n", FontFactory.getFont(FontFactory.COURIER, 5, Font.NORMAL)));
		} else {
			p.add(new Chunk("\n", FontFactory.getFont(FontFactory.COURIER, 5, Font.NORMAL)));
		}

		// ---- Einheit ----
		if (person.getEinheit() != null)
			p.add(new Chunk(person.getEinheit().getName() + "\n", FontFactory.getFont(FontFactory.COURIER, 5, Font.NORMAL)));

		// ---- Geburtsdatum ----
		if (person.getGeburtsdatum() != null) {
			p.add(new Chunk(DateHelper.getFormatedString(person.getGeburtsdatum()), FontFactory.getFont(FontFactory.COURIER,
					5, Font.NORMAL)));
		}

		return p;

	}

}
