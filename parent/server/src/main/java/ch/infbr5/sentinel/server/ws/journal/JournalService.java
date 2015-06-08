package ch.infbr5.sentinel.server.ws.journal;

import java.util.Calendar;
import java.util.List;

import javax.annotation.Resource;
import javax.jws.HandlerChain;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.persistence.EntityManager;
import javax.xml.ws.WebServiceContext;
import javax.xml.ws.soap.MTOM;

import org.apache.log4j.Logger;

import ch.infbr5.sentinel.server.db.EntityManagerHelper;
import ch.infbr5.sentinel.server.db.QueryHelper;
import ch.infbr5.sentinel.server.mapper.Mapper;
import ch.infbr5.sentinel.server.model.journal.BewegungsMeldung;
import ch.infbr5.sentinel.server.model.journal.GefechtsMeldung;
import ch.infbr5.sentinel.server.model.journal.JournalEintrag;
import ch.infbr5.sentinel.server.model.journal.SystemMeldung;

import com.google.common.collect.Lists;

@MTOM
@WebService(name = "JournalService", targetNamespace = "http://ws.sentinel.infbr5.ch/")
@HandlerChain(file = "/META-INF/ws-handler-chain.xml")
@SOAPBinding(style = SOAPBinding.Style.RPC)
public class JournalService {

   @Resource
   private WebServiceContext context;

   private static Logger log = Logger.getLogger(JournalService.class);

   @WebMethod
   public void addSystemMeldung(@WebParam(name = "meldung") final JournalSystemMeldung meldung) {
      final SystemMeldung record = Mapper.mapJournalSystemMeldungToSystemMeldung().apply(meldung);
      getEntityManager().persist(record);
   }

   @WebMethod
   public void addGefechtsMeldung(@WebParam(name = "meldung") final JournalGefechtsMeldung meldung) {
      log.info("Neue Gefechtsmeldung am Checkpoint " + meldung.getCheckpoint().getName() + " erfasst.");
      final GefechtsMeldung record = Mapper.mapJournalGefechtsMeldungToGefechtsMeldung(getEntityManager()).apply(
            meldung);
      getEntityManager().persist(record);
   }

   @WebMethod
   public void removeJournalEintrag(final Long id) {
      final JournalEintrag eintrag = getQueryHelper().getJournalEintrag(id);
      if (eintrag != null) {
         getEntityManager().remove(eintrag);
      }
   }

   @WebMethod
   public void removeJournalEintrage(final Long[] ids) {
      for (final Long id : ids) {
         removeJournalEintrag(id);
      }
   }

   @WebMethod
   public void updateGefechtsMeldung(@WebParam(name = "meldung") final JournalGefechtsMeldung meldung) {
      log.info("Gefechtsmeldung  wird aktualisiert");
      final GefechtsMeldung gefechtsMeldung = getQueryHelper().getGefechtsMeldungen(meldung.getId());

      if (!gefechtsMeldung.isIstErledigt()) {
         if (meldung.isIstErledigt()) {
            gefechtsMeldung.setZeitpunktErledigt(Calendar.getInstance());
         }
      }

      if (gefechtsMeldung.isIstErledigt()) {
         if (!meldung.isIstErledigt()) {
            gefechtsMeldung.setZeitpunktErledigt(null);
         }
      }

      gefechtsMeldung.setIstErledigt(meldung.isIstErledigt());
   }

   @WebMethod
   public JournalResponse getSystemJournal() {
      final List<SystemMeldung> data = getQueryHelper().getSystemMeldungen();
      return createJournalResponseSystemMeldung(data);
   }

   @WebMethod
   public JournalResponse getSystemJournalSeit(@WebParam(name = "timeInMillis") final long timeInMillis) {
      final List<SystemMeldung> data = getQueryHelper().getSystemMeldungenSeit(timeInMillis);
      return createJournalResponseSystemMeldung(data);
   }

   @WebMethod
   public JournalResponse getBewegungsJournal() {
      final List<BewegungsMeldung> data = getQueryHelper().getBewegungsMeldungen();
      return createJournalResponseBewegungsMeldung(data);
   }

   @WebMethod
   public JournalResponse getBewegungsJournalSeit(@WebParam(name = "timeInMillis") final long timeInMillis) {
      final List<BewegungsMeldung> data = getQueryHelper().getBewegungsMeldungenSeit(timeInMillis);
      return createJournalResponseBewegungsMeldung(data);
   }

   @WebMethod
   public JournalResponse getGefechtsJournal() {
      final List<GefechtsMeldung> data = getQueryHelper().getGefechtsMeldungen();
      return createJournalResponseGefechtsMeldung(data);
   }

   @WebMethod
   public JournalResponse getGefechtsJournalSeit(@WebParam(name = "timeInMillis") final long timeInMillis) {
      final List<GefechtsMeldung> data = getQueryHelper().getGefechtsMeldungenSeit(timeInMillis);
      return createJournalResponseGefechtsMeldung(data);
   }

   @WebMethod
   public JournalResponse getJournalSeit(@WebParam(name = "timeInMillis") final long timeInMillis) {
      final JournalResponse response = new JournalResponse();
      response.setSystemMeldungen(this.getSystemJournalSeit(timeInMillis).getSystemMeldungen());
      response.setGefechtsMeldungen(this.getGefechtsJournalSeit(timeInMillis).getGefechtsMeldungen());
      response.setBewegungsMeldungen(this.getBewegungsJournalSeit(timeInMillis).getBewegungsMeldungen());
      return response;
   }

   private JournalResponse createJournalResponseSystemMeldung(final List<SystemMeldung> data) {
      final List<JournalSystemMeldung> eintraege = Lists.transform(data,
            Mapper.mapSystemMeldungToJournalSystemMeldung());
      final JournalResponse response = new JournalResponse();
      response.setSystemMeldungen(eintraege);
      return response;
   }

   private JournalResponse createJournalResponseBewegungsMeldung(final List<BewegungsMeldung> data) {
      final List<JournalBewegungsMeldung> eintraege = Lists.transform(data,
            Mapper.mapBewegungsMeldungToJournalBewegungsMeldung());
      final JournalResponse response = new JournalResponse();
      response.setBewegungsMeldungen(eintraege);
      return response;
   }

   private JournalResponse createJournalResponseGefechtsMeldung(final List<GefechtsMeldung> data) {
      final List<JournalGefechtsMeldung> eintraege = Lists.transform(data,
            Mapper.mapGefechtsMeldungToJournalGefechtsMeldung());
      final JournalResponse response = new JournalResponse();
      response.setGefechtsMeldungen(eintraege);
      return response;
   }

   private EntityManager getEntityManager() {
      return EntityManagerHelper.getEntityManager(context);
   }

   private QueryHelper getQueryHelper() {
      return new QueryHelper(getEntityManager());
   }

}
