package ch.infbr5.sentinel.server.tools;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import javax.persistence.EntityManager;

import ch.infbr5.sentinel.server.db.EntityManagerHelper;
import ch.infbr5.sentinel.server.db.ImageStore;
import ch.infbr5.sentinel.server.model.Ausweis;
import ch.infbr5.sentinel.server.model.Einheit;
import ch.infbr5.sentinel.server.model.Grad;
import ch.infbr5.sentinel.server.model.ObjectFactory;
import ch.infbr5.sentinel.server.model.Person;

import com.thoughtworks.xstream.XStream;

public class OldDataImporter {

	private static final String filename = "../testdaten/sync_mig.xml";
	private static EntityManager em;
	private static Map<String, Einheit> einheitCache;
	private static Long lastTimestamp;

	private static Einheit getEinheit(String einheit) {
		if (OldDataImporter.einheitCache.containsKey(einheit)) {
			return OldDataImporter.einheitCache.get(einheit);
		} else {
			Einheit e = ObjectFactory.createEinheit(einheit);
			e.setRgbColor_GsVb("00AA00");
			e.setRgbColor_TrpK("000000");
			e.setRgbColor_Einh("000000");
			e.setText_GsVb("005");
			e.setText_TrpK("005");
			e.setText_Einh("000");
			OldDataImporter.einheitCache.put(einheit, e);
			OldDataImporter.em.persist(e);
			
			return e;
		}
	}

	public static void main(String[] args) {

		List<PTransferObject> imp;
		OldDataImporter.einheitCache = new HashMap<String, Einheit>();

		EntityManagerHelper.setDebugMode(true);
		OldDataImporter.em = EntityManagerHelper.getEntityManager();

		XStream xstream = new XStream();
		xstream.alias("ausweis", ATransferObject.class);
		xstream.alias("person", PTransferObject.class);
		xstream.alias("berechtigung", BTransferObject.class);
		try {
			if (new File(OldDataImporter.filename).exists()) {

				BufferedReader in = new BufferedReader(new FileReader(OldDataImporter.filename));
				imp = (List<PTransferObject>) xstream.fromXML(in);
				in.close();

				OldDataImporter.lastTimestamp = new Date().getTime();
				for (Object element : imp) {
					PTransferObject p = (PTransferObject) element;

					OldDataImporter.em.getTransaction().begin();
					OldDataImporter.stopuhr("em.getTransaction().begin()");

					Einheit e = OldDataImporter.getEinheit(p.getEinheit());
					OldDataImporter.stopuhr("getEinheit(p.getEinheit())");

					Calendar cal = Calendar.getInstance();
					cal.setTime(p.getGeburtsdatum());
					Person person = ObjectFactory.createPerson(e, p.getAhvNr(), Grad.getGrad(p.getGradText()), p.getName(),
							p.getVorname(), cal, p.getFunktion());
					OldDataImporter.stopuhr("ObjectFactory.createAdA()");

					OldDataImporter.em.persist(person);
					OldDataImporter.stopuhr("em.persist(person);");

					ImageStore.saveJpegImage(p.getAhvNr(), p.getImage());
					OldDataImporter.stopuhr("ImageStore.saveJPEGImage();");

					for (ListIterator<ATransferObject> iterator2 = p.getAusweisListe().listIterator(); iterator2.hasNext();) {
						ATransferObject a = iterator2.next();
						if (!a.getUngueltig()) {
							Ausweis ausweis = ObjectFactory.createAusweis(person);
							ausweis.setBarcode(a.getBarCode());
							OldDataImporter.stopuhr("ObjectFactory.createAusweisPermanent(person)");

							OldDataImporter.em.persist(ausweis);
							OldDataImporter.stopuhr("em.persist(ausweis);");
							
							person.setValidAusweis(ausweis);
						}
					}
					
					OldDataImporter.em.getTransaction().commit();
					OldDataImporter.stopuhr("em.getTransaction().commit();");
				}

			}

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private static void stopuhr(String point) {
		Date now = new Date();
		System.out.print(point + " ");
		System.out.println(OldDataImporter.lastTimestamp - now.getTime());
		OldDataImporter.lastTimestamp = now.getTime();

	}

}
