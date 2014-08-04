package ch.infbr5.sentinel.server.ws;

import java.util.Set;

import javax.persistence.EntityManager;
import javax.xml.namespace.QName;
import javax.xml.soap.SOAPMessage;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;

import org.apache.log4j.Logger;

import ch.infbr5.sentinel.server.db.EntityManagerHelper;

public class RequestHandler implements SOAPHandler<SOAPMessageContext> {

	private static Logger log = Logger.getLogger(RequestHandler.class);

	@Override
	public void close(MessageContext context) {
		// TODO Auto-generated method stub

	}

	@Override
	public Set<QName> getHeaders() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean handleFault(SOAPMessageContext context) {
		EntityManager em = EntityManagerHelper.getEntityManager();
		em.getTransaction().rollback();
		return true;
	}

	@Override
	public boolean handleMessage(SOAPMessageContext context) {
		Boolean outboundProperty = (Boolean) context.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);

		EntityManager em = EntityManagerHelper.getEntityManager();
		if (outboundProperty.booleanValue()) {
			em.getTransaction().commit();
		} else {
			em.getTransaction().begin();
		}

		// TODO: Debug abfrage einbauen
		this.logToSystemOut(context, outboundProperty);
		return true;
	}

	private void logToSystemOut(SOAPMessageContext smc, boolean outboundProperty) {

		if (outboundProperty) {
			//TODO Debug Log
		} else {
			//TODO Debug Log
		}

		SOAPMessage message = smc.getMessage();
		try {
			//TODO Debug Log
			//message.writeTo(System.out);
		} catch (Exception e) {
			log.error("Exception in handler: " + e);
		}
	}

}
