package ch.infbr5.sentinel.server.print;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;

import org.junit.Ignore;
import org.junit.Test;

import ch.infbr5.sentinel.server.db.EntityManagerHelper;
import ch.infbr5.sentinel.server.model.Grad;
import ch.infbr5.sentinel.server.model.Person;
import ch.infbr5.sentinel.server.model.PrintJob;

@Ignore
public class AusweisRendererTestIT {

   @Test
   public void createAusweiseTest() {
      EntityManagerHelper.setDebugMode(true);
      final EntityManager em = EntityManagerHelper.createEntityManager();
      em.getTransaction().begin();

      final List<Person> personen = new ArrayList<>();
      final Person p = new Person();
      p.setName("Hulk");
      p.setVorname("Hogang");
      p.setGrad(Grad.SDT);
      personen.add(p);
      personen.add(p);
      personen.add(p);
      personen.add(p);
      personen.add(p);
      personen.add(p);

      final PrintJob j = new PdfAusweisListe(personen, false, true, "HQ Kp 5/1").print();
      em.getTransaction().commit();

      em.close();

      try {
         Desktop.getDesktop().open(new File("pdfs\\" + j.getPintJobFile() + ".pdf"));
      } catch (final IOException e) {
         // TODO Auto-generated catch block
         e.printStackTrace();
      }
   }

}
