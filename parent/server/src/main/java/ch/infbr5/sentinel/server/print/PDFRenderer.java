package ch.infbr5.sentinel.server.print;

import java.util.Calendar;

import ch.infbr5.sentinel.server.db.PdfStore;
import ch.infbr5.sentinel.server.model.ObjectFactory;
import ch.infbr5.sentinel.server.model.PrintJob;

public abstract class PDFRenderer {

	public PrintJob print() {
		byte[] data = renderPdf();
		if (data != null && data.length > 0) {
			String filename = createFilename();
			PdfStore.savaPdfFile(filename, data);
			return ObjectFactory.createPrintJob(getBeschreibung(), filename);
		}
		return null;
	}

	protected abstract byte[] renderPdf();

	protected abstract String getFileName();

	protected abstract String getBeschreibung();

	private String createFilename() {
		long timestamp = Calendar.getInstance().getTimeInMillis();
		return getFileName() + "_" + String.valueOf(timestamp);
	}

}
