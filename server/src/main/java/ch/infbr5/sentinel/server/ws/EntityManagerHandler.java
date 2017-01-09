package ch.infbr5.sentinel.server.ws;

import javax.persistence.EntityManager;
import javax.xml.ws.handler.LogicalHandler;
import javax.xml.ws.handler.LogicalMessageContext;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.MessageContext.Scope;

import ch.infbr5.sentinel.server.db.EntityManagerHelper;

public class EntityManagerHandler implements LogicalHandler<LogicalMessageContext> {

	public static final String ENTITY_MANAGER_PROPERTY = "em";

	@Override
	public boolean handleMessage(LogicalMessageContext context) {
		Boolean outboundProperty = (Boolean) context.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);
		if (outboundProperty.booleanValue()) {
			finishRequest(context);
		} else {
			startRequest(context);
		}
		return true;
	}

	@Override
	public boolean handleFault(LogicalMessageContext context) {
		EntityManager em = (EntityManager) context.get(ENTITY_MANAGER_PROPERTY);
		em.getTransaction().rollback();
		return true;
	}

	@Override
	public void close(MessageContext context) {
		EntityManager em = (EntityManager) context.get(ENTITY_MANAGER_PROPERTY);
		safeClose(em);
	}

	private void startRequest(LogicalMessageContext context) {
		EntityManager em = EntityManagerHelper.createEntityManager();
		em.getTransaction().begin();
		context.put(ENTITY_MANAGER_PROPERTY, em);
		context.setScope(ENTITY_MANAGER_PROPERTY, Scope.APPLICATION);
	}

	private void finishRequest(LogicalMessageContext context) {
		EntityManager em = (EntityManager) context.get(ENTITY_MANAGER_PROPERTY);
		em.getTransaction().commit();
	}

	private void safeClose(EntityManager em) {
		if (em != null && em.isOpen()) {
			em.close();
		}
	}

}
