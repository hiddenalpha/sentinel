package ch.infbr5.sentinel.server.model;

import static org.junit.Assert.assertTrue;

import java.util.Iterator;
import java.util.List;

import javax.persistence.EntityManager;

import org.junit.Ignore;
import org.junit.Test;

import ch.infbr5.sentinel.server.db.EntityManagerHelper;
import ch.infbr5.sentinel.server.db.QueryHelper;

@Ignore
public class AusweiseTest {

	@Test
	public void getAusweise() {

		EntityManagerHelper.setDebugMode(false);
		EntityManager em = EntityManagerHelper.createEntityManager();
		em.getTransaction().begin();

		List<Ausweis> a = new QueryHelper(em).findAusweise();

		em.getTransaction().commit();

		assertTrue(a.size() > 0);

		em.close();

	}

	@Test
	public void migDB() {

		EntityManagerHelper.setDebugMode(false);
		EntityManager em = EntityManagerHelper.createEntityManager();
		em.getTransaction().begin();

		List<Person> personen = new QueryHelper(em).getPersonen(false, true, "ZIVIL");
		//List<Person> personen = QueryHelper.getPersonen(false, true, "_Archiv_");
		for (Iterator<Person> iterator = personen.iterator(); iterator.hasNext();) {
			Person person = iterator.next();


			if(person.getValidAusweis()!=null) {
				person.getValidAusweis().invalidate();
			}

			em.remove(person);

			System.out.println(person.getName() + " " + person.getVorname());

		}
		// em.persist(p);

		em.getTransaction().commit();

		em.close();

	}

}
