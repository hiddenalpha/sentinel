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

	private List<Person> personen;

	private String einheitName;

	public PdfAusweisBoxInventar(List<Person> personen, String einheitName) {
		this.personen = personen;
		this.einheitName = einheitName;
	}

	@Override
	protected String getFileName() {
		return "ausweisboxInventar";
	}

	@Override
	protected String getBeschreibung() {
		return "Ausweisbox Inventar";
	}

	@Override
	protected byte[] renderPdf() {

		if (personen.isEmpty()) {
			return null;
		}

		Document document = new Document();
		document.setPageSize(PageSize.A4.rotate());
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		try {
			PdfWriter.getInstance(document, out);
			document.open();

			int seiten = ((personen.size() - 1) / NOF_SLOTS_IN_BOX) + 1;

			for (int s = 1; s <= seiten; s++) {
				// Tabellen Layout
				PdfPTable table = new PdfPTable(5);
				float[] columnWidth = { 150, 150, 150, 150, 150 };
				table.setTotalWidth(columnWidth);
				table.setLockedWidth(true);

				for (int i = 1; i <= 27; i++) {
					for (int j = 1; j <= 5; j++) {

						Phrase p = new Phrase();

						p.add(new Chunk(getAusweisAt(i, j, s, personen),
								FontFactory.getFont(FontFactory.COURIER, 8,
										Font.NORMAL)));
						PdfPCell c = new PdfPCell(p);
						c.setFixedHeight(18);
						table.addCell(c);
					}

				}
				document.add(table);
				document.newPage();
			}

		} catch (DocumentException e) {
			e.printStackTrace();
			// } catch (IOException e) {
			// e.printStackTrace();
		}
		document.close();
		return out.toByteArray();
	}

	private String getAusweisAt(int row, int col, int page,
			List<Person> personen) {
		if ((col == 3) && (row > 24)) {
			return "";
		}

		int nr = (col - 1) * 27 + row;
		if (col > 3) {
			nr = nr - 3;
		}

		int AusweisNr = nr - 1 + (NOF_SLOTS_IN_BOX * (page - 1));

		String txt = "";
		if (AusweisNr < personen.size()) {
			Person p = personen.get(AusweisNr);
			if (p != null) {
				txt = String.valueOf(nr) + ". ";
				if (p.getName() != null)
					txt = txt + p.getName() + " ";
				if (p.getVorname() != null)
					txt = txt + p.getVorname() + " ";
				if (p.getGrad() != null)
					txt = txt + "(" + p.getGrad().toString() + ")";
			}
		}

		return txt.trim();
	}


}
