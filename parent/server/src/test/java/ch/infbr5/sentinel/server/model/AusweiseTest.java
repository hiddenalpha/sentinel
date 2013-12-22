package ch.infbr5.sentinel.server.model;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import javax.persistence.EntityManager;

import org.junit.Ignore;
import org.junit.Test;
import static org.junit.Assert.*;

import ch.infbr5.sentinel.server.db.EntityManagerHelper;
import ch.infbr5.sentinel.server.db.QueryHelper;
import ch.infbr5.sentinel.server.exporter.AusweisDatenWriter;
import ch.infbr5.sentinel.server.importer.AusweisDatenReader;
import ch.infbr5.sentinel.server.importer.PisaCsvReader;

@Ignore
public class AusweiseTest {

	@Test
	public void getAusweise() {

		EntityManagerHelper.setDebugMode(false);
		EntityManager em = EntityManagerHelper.getEntityManager();
		em.getTransaction().begin();

		List<Ausweis> a = QueryHelper.findAusweise();

		em.getTransaction().commit();

		assertTrue(a.size() > 0);

		em.close();

	}

	@Test
	public void migDB() {

		EntityManagerHelper.setDebugMode(false);
		EntityManager em = EntityManagerHelper.getEntityManager();
		em.getTransaction().begin();

		List<Person> personen = QueryHelper.getPersonen(true, true, "BESUCHER");
		for (Iterator<Person> iterator = personen.iterator(); iterator.hasNext();) {
			Person person = iterator.next();
//			person.setGrad(null);
//			
//			if(person.getValidAusweis()!=null) {
//				person.getValidAusweis().invalidate();
//			}
//			
//			Ausweis a = ObjectFactory.createAusweis(person);
//			EntityManagerHelper.getEntityManager().persist(a);
//			person.setValidAusweis(a);
//			
//			System.out.println(person.getName() + " " + person.getVorname());

		}
		// em.persist(p);

		em.getTransaction().commit();

		em.close();

	}

}
