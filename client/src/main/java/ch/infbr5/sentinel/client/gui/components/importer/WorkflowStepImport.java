package ch.infbr5.sentinel.client.gui.components.importer;

import java.awt.Frame;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import net.miginfocom.swing.MigLayout;
import ch.infbr5.sentinel.client.util.ServiceHelper;
import ch.infbr5.sentinel.client.wsgen.Modification;
import ch.infbr5.sentinel.client.wsgen.ModificationDto;

public class WorkflowStepImport extends WorkflowStep {

   private JPanel panel;

   private ModificationDto dto;

   public WorkflowStepImport(final Frame parent, final WorkflowData data, final WorkflowInterceptor interceptor) {
      super(parent, data, interceptor);
   }

   @Override
   public String getName() {
      return "Import";
   }

   @Override
   public String getUserInfo() {
      return "Zu sehen ist eine Zusammenfassung, was beim Klick auf Weiter gemacht wird. "
            + "Beispiel: Person aktualisieren 5/6 bedeutet, dass 5 von 6 Personen-Aktualisierungen durchgef�hrt werden.";
   }

   @Override
   public JPanel getPanel() {
      panel = new JPanel(new MigLayout());

      final StringBuilder builder = new StringBuilder("<html><b>Folgende Änderungen werden gemacht:</b>");

      builder.append("<br /><br />Neue Personen erfassen ");
      builder.append(getSizeToModify(dto.getModificationNewPersons()));
      builder.append("/");
      builder.append(dto.getModificationNewPersons().size());

      builder.append("<br /><br />Personen aktualisieren ");
      builder.append(getSizeToModify(dto.getModificationUpdatePersons()));
      builder.append("/");
      builder.append(dto.getModificationUpdatePersons().size());

      builder.append("<br /><br />Personen aktualisieren und neuen Ausweis ausstellen ");
      builder.append(getSizeToModify(dto.getModificationNewAusweise()));
      builder.append("/");
      builder.append(dto.getModificationNewAusweise().size());

      builder.append("<br /><br />Personen archivieren ");
      builder.append(getSizeToModify(dto.getModificationArchivePersons()));
      builder.append("/");
      builder.append(dto.getModificationArchivePersons().size());

      builder.append("<br /><br />Nicht berücksichtigte Datensätze ");
      builder.append(dto.getModificationErrors().size());

      builder.append("</html>");

      panel.add(new JLabel(builder.toString()));
      return panel;
   }

   public int getSizeToModify(final List<? extends Modification> list) {
      int size = 0;
      for (final Modification mod : list) {
         if (mod.isToModify()) {
            size++;
         }
      }
      return size;
   }

   @Override
   public void init() {
      getInterceptor().activateNext();
      dto = ServiceHelper.getPersonenImporterService().getModifications(getData().getSessionKey());
   }

   @Override
   public void abort() {
      if (getData().getSessionKey() != null) {
         try {
            ServiceHelper.getPersonenImporterService().abortImport(getData().getSessionKey());
         } catch (final RuntimeException e) {
            e.printStackTrace();
         }
      }
   }

   @Override
   public void finishReturn() {
      // Do nothing
   }

   @Override
   public void finishNext() {
      final boolean result = ServiceHelper.getPersonenImporterService().startImport(getData().getSessionKey());

      final String title = "Pisadaten importieren";
      int messageType = 0;
      String message = null;
      if (result) {
         message = "Die Datei wurde gespeichert.";
         messageType = JOptionPane.INFORMATION_MESSAGE;
      } else {
         message = "Die Datei konnte nicht erfolgreich gespeichert werden.";
         messageType = JOptionPane.WARNING_MESSAGE;
      }

      JOptionPane.showMessageDialog(null, message, title, messageType);
   }

}
