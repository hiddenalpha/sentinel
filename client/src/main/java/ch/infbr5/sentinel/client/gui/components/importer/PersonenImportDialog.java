package ch.infbr5.sentinel.client.gui.components.importer;

import javax.swing.JFrame;

public class PersonenImportDialog {

   private final JFrame parent;

   public PersonenImportDialog(final JFrame parent) {
      this.parent = parent;
   }

   public void show() {
      final WorkflowDialog dialog = new WorkflowDialog(parent);

      final WorkflowData data = new WorkflowData();

      final WorkflowInterceptor interceptor = dialog.getWorkflowInterceptor();

      dialog.addWorkflowStep(new WorkflowStepFile(parent, data, interceptor));
      dialog.addWorkflowStep(new WorkflowStepMapping(parent, data, interceptor));
      dialog.addWorkflowStep(new WorkflowStepModification(parent, data, interceptor));
      dialog.addWorkflowStep(new WorkflowStepImport(parent, data, interceptor));

      dialog.show();
   }

}
