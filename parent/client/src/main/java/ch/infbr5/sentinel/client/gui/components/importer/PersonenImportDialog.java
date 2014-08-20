package ch.infbr5.sentinel.client.gui.components.importer;

import java.awt.Frame;

public class PersonenImportDialog {

	private Frame parent;

	public PersonenImportDialog(Frame parent) {
		this.parent = parent;
	}

	public void show() {
		WorkflowDialog dialog = new WorkflowDialog();

		WorkflowData data = new WorkflowData();

		WorkflowInterceptor interceptor = dialog.getWorkflowInterceptor();

		dialog.addWorkflowStep(new WorkflowStepFile(parent, data, interceptor));
		dialog.addWorkflowStep(new WorkflowStepMapping(parent, data, interceptor));
		dialog.addWorkflowStep(new WorkflowStepModification(parent, data, interceptor));
		dialog.addWorkflowStep(new WorkflowStepImport(parent, data, interceptor));

		dialog.show();
	}

}
