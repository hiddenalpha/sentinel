package ch.infbr5.sentinel.server.ws;

import java.util.List;

import javax.jws.HandlerChain;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.xml.ws.soap.MTOM;

import ch.infbr5.sentinel.server.db.EntityManagerHelper;
import ch.infbr5.sentinel.server.db.QueryHelper;
import ch.infbr5.sentinel.server.model.LogEintrag;
import ch.infbr5.sentinel.server.model.ObjectFactory;
import ch.infbr5.sentinel.server.model.journal.OperatorEintrag;

@MTOM
@WebService(name = "JournalService", targetNamespace = "http://ws.sentinel.infbr5.ch/")
@HandlerChain(file = "/META-INF/ws-handler-chain.xml")
@SOAPBinding(style = SOAPBinding.Style.RPC)
public class JournalService {

	@WebMethod
	public void addLogEintrag(@WebParam(name = "reportedClass") String reportedClass,
			@WebParam(name = "loggerClass") String loggerClass, @WebParam(name = "millis") long millis,
			@WebParam(name = "sequence") long sequence, @WebParam(name = "level") String level,
			@WebParam(name = "method") String method, @WebParam(name = "thread") int thread,
			@WebParam(name = "message") String message, @WebParam(name = "operator") String operator,
			@WebParam(name = "checkpointId") long checkpointId) {

		LogEintrag record = ObjectFactory.createLogEintrag(checkpointId, level, message, millis, 0, loggerClass, method,
				operator, reportedClass, sequence, thread);
		EntityManagerHelper.getEntityManager().persist(record);

	}

	@WebMethod
	public void addJournalEintrag(@WebParam(name = "millis") long millis,  @WebParam(name = "level") String level, @WebParam(name = "message") String message,
			@WebParam(name = "operator") String operator, @WebParam(name = "trigger") String trigger, @WebParam(name = "checkpointId") long checkpointId) {

		LogEintrag record = ObjectFactory.createLogEintrag(checkpointId, level, message, millis, 1);
		EntityManagerHelper.getEntityManager().persist(record);

	}

	@WebMethod
	public JournalResponse getLogEintraege(long checkpointId) {
		List<LogEintrag> records = QueryHelper.getLogEintraege(checkpointId, 4);

		JournalEintragDetails[] details = new JournalEintragDetails[records.size()];
		for (int i = 0; i < records.size(); i++) {
			LogEintrag r = records.get(i);

			JournalEintragDetails detail = new JournalEintragDetails();
			detail.setId(r.getId());
			detail.setLevel(r.getLevel());
			detail.setMessage(r.getMessage());
			detail.setType(r.getType());
			detail.setMillis(r.getMillis());
			detail.setCheckpointId(r.getCheckpointId());

			details[i] = detail;
		}

		JournalResponse response = new JournalResponse();
		response.setRecords(details);
		return response;
	}

	// @WebMethod
	// public JournalResponse getSystemEintraege(long checkpointId) {
	// List<LogEintrag> systemEintraege =
	// QueryHelper.findSystemEintraege(checkpointId);
	//
	// SystemEintragDetails[] systemEintraegeDetails = new
	// SystemEintragDetails[systemEintraege.size()];
	// for (int i = 0; i < systemEintraege.size(); i++) {
	// LogEintrag systemEintrag = systemEintraege.get(i);
	//
	// SystemEintragDetails systemEintragDetail = new SystemEintragDetails();
	//
	// //systemEintragDetail.setCreator(systemEintrag.getCreator());
	// //systemEintragDetail.setCause(systemEintrag.getCause());
	// // systemEintragDetail.setDate(systemEintrag.getDate());
	// //systemEintragDetail.setIncidenceClass(systemEintrag.getIncidenceClass());
	// systemEintragDetail.setCheckpointId(systemEintrag.getCheckpointId());
	//
	// systemEintraegeDetails[i] = systemEintragDetail;
	// }
	//
	// JournalResponse response = new JournalResponse();
	// response.setSystemEintraege(systemEintraegeDetails);
	//
	// return response;
	// }

	@WebMethod
	public boolean setPersonTriggerToDone(@WebParam(name = "operatorEintrag") OperatorEintrag operatorEintrag) {
		return QueryHelper.updateOperatorEintrag(operatorEintrag);
	}
}
