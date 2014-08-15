package ch.infbr5.sentinel.server.print;

import java.util.Calendar;

import javax.persistence.EntityManager;

import ch.infbr5.sentinel.server.db.PdfStore;
import ch.infbr5.sentinel.server.db.QueryHelper;
import ch.infbr5.sentinel.server.model.ObjectFactory;
import ch.infbr5.sentinel.server.model.PrintJob;

public abstract class PrintingDocument {

	private EntityManager em;

	protected PrintingDocument(EntityManager em) {
		this.em = em;
	}

	protected QueryHelper getQueryHelper() {
		return new QueryHelper(em);
	}

	protected EntityManager getEntityManager() {
		return em;
	}

	public PrintJob print() {
		byte[] data = renderPdf();
		if (data != null) {
			long timestamp = Calendar.getInstance().getTimeInMillis();
			String name = getFileName() + "_" + String.valueOf(timestamp);

			PdfStore.savaPdfFile(name, data);

			PrintJob pj = ObjectFactory.createPrintJob(toString(), name);
			em.persist(pj);
			return pj;
		}
		return null;
	}

	protected abstract byte[] renderPdf();

	protected abstract String getFileName();

}
