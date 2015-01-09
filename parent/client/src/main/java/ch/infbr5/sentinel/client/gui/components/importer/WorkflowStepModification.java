package ch.infbr5.sentinel.client.gui.components.importer;

import java.awt.Color;
import java.awt.Frame;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.xml.datatype.XMLGregorianCalendar;

import net.miginfocom.swing.MigLayout;
import ch.infbr5.sentinel.client.util.ServiceHelper;
import ch.infbr5.sentinel.client.wsgen.Modification;
import ch.infbr5.sentinel.client.wsgen.ModificationArchivePerson;
import ch.infbr5.sentinel.client.wsgen.ModificationDto;
import ch.infbr5.sentinel.client.wsgen.ModificationError;
import ch.infbr5.sentinel.client.wsgen.ModificationNewPerson;
import ch.infbr5.sentinel.client.wsgen.ModificationUpdatePerson;
import ch.infbr5.sentinel.client.wsgen.ModificationUpdatePersonAndNewAusweis;
import ch.infbr5.sentinel.client.wsgen.PersonDetails;
import ch.infbr5.sentinel.client.wsgen.UpdatePersonAttributeDiff;
import ch.infbr5.sentinel.common.gui.util.SwingHelper;

public class WorkflowStepModification extends WorkflowStep {

   private JPanel panel;

   private final Map<JCheckBox, Modification> checkboxes = new HashMap<>();

   private ModificationDto dto;

   public WorkflowStepModification(final Frame parent, final WorkflowData data, final WorkflowInterceptor interceptor) {
      super(parent, data, interceptor);
   }

   @Override
   public String getName() {
      return "�nderungen";
   }

   @Override
   public String getUserInfo() {
      return "Hier sind alle Änderungen aufgelistet. Jede Änderung kann deaktiviert werden, so wird diese im"
            + " Import nicht berücksichtigt.";
   }

   @Override
   public JPanel getPanel() {
      panel = new JPanel(new MigLayout());

      boolean hasMods = false;

      final String layoutoptions = "width 100%, wrap";
      for (int i = 0; i < dto.getModificationErrors().size(); i++) {
         panel.add(createPanelModification(dto.getModificationErrors().get(i)), layoutoptions);
         hasMods = true;
      }
      for (int i = 0; i < dto.getModificationNewPersons().size(); i++) {
         panel.add(createPanelModification(dto.getModificationNewPersons().get(i)), layoutoptions);
         hasMods = true;
      }

      for (int i = 0; i < dto.getModificationUpdatePersons().size(); i++) {
         panel.add(createPanelModification(dto.getModificationUpdatePersons().get(i)), layoutoptions);
         hasMods = true;
      }

      for (int i = 0; i < dto.getModificationNewAusweise().size(); i++) {
         panel.add(createPanelModification(dto.getModificationNewAusweise().get(i)), layoutoptions);
         hasMods = true;
      }

      for (int i = 0; i < dto.getModificationArchivePersons().size(); i++) {
         panel.add(createPanelModification(dto.getModificationArchivePersons().get(i)), layoutoptions);
         hasMods = true;
      }

      if (!hasMods) {
         panel.add(new JLabel("Dieser Import bringt keine �nderungen mit sich."));
      }

      return panel;
   }

   @Override
   public void init() {
      dto = ServiceHelper.getPersonenImporterService().getModifications(getData().getSessionKey());
      getInterceptor().activateNext();
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
   public void finishNext() {
      for (final JCheckBox ckbox : checkboxes.keySet()) {
         if (ckbox.isSelected()) {
            checkboxes.get(ckbox).setToModify(true);
         } else {
            checkboxes.get(ckbox).setToModify(false);
         }
      }
      ServiceHelper.getPersonenImporterService().setModifications(getData().getSessionKey(), dto);
   }

   @Override
   public void finishReturn() {
      finishNext();
   }

   private JPanel createPanel(final JLabel label, final Modification modification, final boolean isSelected,
         final boolean enabled) {
      final JPanel panel = new JPanel(new MigLayout());
      panel.setBorder(BorderFactory.createLineBorder(Color.black));

      final JCheckBox checkbox = new JCheckBox();
      checkbox.setSelected(isSelected);
      checkbox.setEnabled(enabled);

      if (enabled == false) {
         panel.setBackground(SwingHelper.COLOR_RED);
         checkbox.setBackground(SwingHelper.COLOR_RED);
      }

      panel.add(checkbox, "aligny top");
      panel.add(label);

      checkboxes.put(checkbox, modification);

      return panel;
   }

   private JLabel createLabel(final String typ, final PersonDetails person, final String additional) {
      return new JLabel("<html><b>" + typ + " </b><br />" + personString(person) + " <br /> " + additional + "</html>");
   }

   private JPanel createPanelModification(final ModificationNewPerson mod) {
      return createPanel(createLabel("Neue Person", mod.getPersonDetails(), ""), mod, mod.isToModify(), true);
   }

   private JPanel createPanelModification(final ModificationError mod) {
      return createPanel(
            createLabel("Fehlerhafter Datensatz (Datensatz wird nicht berücksichtigt)", mod.getPersonDetails(),
                  "<br/><b>Fehler</b>:<br />" + mod.getErrorMessage()), mod, false, false);
   }

   private JPanel createPanelModification(final ModificationArchivePerson mod) {
      return createPanel(createLabel("Person archivieren", mod.getPersonDetails(), ""), mod, mod.isToModify(), true);
   }

   private JPanel createPanelModification(final ModificationUpdatePersonAndNewAusweis mod) {
      return createPanel(
            createLabel("Person aktualisieren und neuer Ausweis", mod.getPersonDetailsOld(),
                  "<br /><b>�nderungen</b>:<br />" + personDiff(mod)), mod, mod.isToModify(), true);
   }

   private JPanel createPanelModification(final ModificationUpdatePerson mod) {
      return createPanel(
            createLabel("Person aktualisieren", mod.getPersonDetailsNew(), "<br /><b>Änderungen</b>:<br />"
                  + personDiff(mod)), mod, mod.isToModify(), true);
   }

   private String personString(final PersonDetails person) {
      return person.getGrad() + " " + person.getName() + " " + person.getVorname() + "<br/>" + person.getAhvNr() + ", "
            + toString(person.getGeburtsdatum()) + "<br/>" + person.getEinheitText() + ", " + person.getFunktion();
   }

   private String toString(final XMLGregorianCalendar calendar) {
      return new SimpleDateFormat("dd.MM.yyyy").format(calendar.toGregorianCalendar().getTime());
   }

   private String personDiff(final ModificationUpdatePerson mod) {
      String diff = "";
      for (final UpdatePersonAttributeDiff d : mod.getUpdatePersonenDiffs()) {
         diff += d.getPersonenAttribute().value() + ": " + d.getOldValue() + " >>> " + d.getNewValue() + "<br />";
      }
      return diff;
   }

}
