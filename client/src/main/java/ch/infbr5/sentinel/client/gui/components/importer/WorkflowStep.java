package ch.infbr5.sentinel.client.gui.components.importer;

import java.awt.Frame;

import javax.swing.JPanel;

public abstract class WorkflowStep {

	private Frame parent;
	
	private WorkflowData data;
	
	private WorkflowInterceptor interceptor;
	
	public WorkflowStep(Frame parent, WorkflowData data, WorkflowInterceptor interceptor) {
		this.parent = parent;
		this.data = data;
		this.interceptor = interceptor;
	}
	
	public Frame getParent() {
		return parent;
	}
	
	public WorkflowData getData() {
		return data;
	}
	
	public WorkflowInterceptor getInterceptor() {
		return interceptor;
	}
	
	abstract String getName();
	
	abstract JPanel getPanel();
	
	abstract void init();
	
	abstract void finishReturn();
	
	abstract void finishNext();
	
	abstract void abort();
	
	abstract String getUserInfo();
	
}
