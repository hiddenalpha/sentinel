package ch.infbr5.sentinel.server.print;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;

import javax.persistence.EntityManager;

import org.junit.Ignore;
import org.junit.Test;

import ch.infbr5.sentinel.server.db.EntityManagerHelper;
import ch.infbr5.sentinel.server.model.PrintJob;
@Ignore
public class AusweisRendererTest {

	@Test
	public void createAusweiseTest() {
		EntityManagerHelper.setDebugMode(true);
		EntityManager em = EntityManagerHelper.createEntityManager();
		em.getTransaction().begin();

		PrintJob j = new PdfAusweisListe(em, false, true, "HQ Kp 5/1").print();

		em.getTransaction().commit();

		em.close();

		try {
			Desktop.getDesktop().open(new File("pdfs\\" + j.getPintJobFile() + ".pdf"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
