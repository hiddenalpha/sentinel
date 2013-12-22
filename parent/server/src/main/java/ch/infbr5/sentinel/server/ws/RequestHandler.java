package ch.infbr5.sentinel.server.ws;

import java.util.Set;

import javax.persistence.EntityManager;
import javax.xml.namespace.QName;
import javax.xml.soap.SOAPMessage;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;

import ch.infbr5.sentinel.server.db.EntityManagerHelper;

public class RequestHandler implements SOAPHandler<SOAPMessageContext> {

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
			//System.out.println("\nOutbound message:");
		} else {
			//TODO Debug Log
			//System.out.println("\nInbound message:");
		}

		SOAPMessage message = smc.getMessage();
		try {
			//TODO Debug Log
			//message.writeTo(System.out);
			//System.out.println("");
		} catch (Exception e) {
			System.out.println("Exception in handler: " + e);
		}
	}

}
