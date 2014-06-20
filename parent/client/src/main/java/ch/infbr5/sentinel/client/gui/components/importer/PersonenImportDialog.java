package ch.infbr5.sentinel.client.gui.components.importer;

import java.awt.Frame;

public class PersonenImportDialog {

	private Frame parent;
	
	private boolean isKompletterBestand;
	
	public PersonenImportDialog(Frame parent, boolean isKompletterBestand) {
		this.parent = parent;
		this.isKompletterBestand = isKompletterBestand;
	}	
	
	public void show() {
		WorkflowDialog dialog = new WorkflowDialog();
		
		WorkflowData data = new WorkflowData();
		data.setKompletterBestand(isKompletterBestand);
		
		WorkflowInterceptor interceptor = dialog.getWorkflowInterceptor();
		
		dialog.addWorkflowStep(new WorkflowStepFile(parent, data, interceptor));
		dialog.addWorkflowStep(new WorkflowStepMapping(parent, data, interceptor));
		dialog.addWorkflowStep(new WorkflowStepModification(parent, data, interceptor));
		dialog.addWorkflowStep(new WorkflowStepImport(parent, data, interceptor));

		dialog.show();
	}
	
}
