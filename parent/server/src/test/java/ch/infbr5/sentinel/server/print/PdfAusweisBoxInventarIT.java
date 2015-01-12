package ch.infbr5.sentinel.server.print;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Ignore;
import org.junit.Test;

import ch.infbr5.sentinel.server.model.Grad;
import ch.infbr5.sentinel.server.model.Person;
import ch.infbr5.sentinel.server.model.PrintJob;

@Ignore
public class PdfAusweisBoxInventarIT {

   @Test
   public void print() {
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

      final PdfAusweisBoxInventar printer = new PdfAusweisBoxInventar(personen, "EinheitsNamedddddddddddddddddddddddd");
      final PrintJob printJob = printer.print();
      try {
         Desktop.getDesktop().open(new File("pdfs\\" + printJob.getPintJobFile() + ".pdf"));
      } catch (final IOException e) {
         // TODO Auto-generated catch block
         e.printStackTrace();
      }
   }

}
