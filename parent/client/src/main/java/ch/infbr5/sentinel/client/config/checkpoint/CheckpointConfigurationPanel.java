package ch.infbr5.sentinel.client.config.checkpoint;

import java.util.List;
import java.util.Vector;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import net.miginfocom.swing.MigLayout;
import ch.infbr5.sentinel.client.wsgen.CheckpointDetails;
import ch.infbr5.sentinel.common.gui.util.SwingHelper;

public class CheckpointConfigurationPanel extends JPanel {

   private static final long serialVersionUID = 1L;

   // UI Components
   private final JPanel groupPanel;

   private final CheckpointConfigurationCamerasPanel cameraPanel;

   private final JLabel lblCheckpoint;

   private JComboBox<CheckpointSelectionValue> cmbCheckpoints;

   private final JLabel lblInfo;

   public CheckpointConfigurationPanel(final String info, final List<CheckpointDetails> checkpoints) {
      lblInfo = SwingHelper.createLabel("<html>Nachfolgend können Sie den Checkpoint konfigurieren.<br/><br/>" + info
            + "</html>");
      lblCheckpoint = SwingHelper.createLabel("Checkpoint");
      setupDropdownCheckpoints(checkpoints);
      groupPanel = new JPanel(new MigLayout());
      cameraPanel = new CheckpointConfigurationCamerasPanel(true, null);

      SwingHelper.attachLabledBorder("Checkpoint-Konfiguration", groupPanel);
      SwingHelper.attachLabledBorder("Infos", lblInfo);

      setLayout(new MigLayout());

      final JPanel ckPanel = new JPanel(new MigLayout());
      ckPanel.add(lblCheckpoint, "");
      ckPanel.add(cmbCheckpoints, "push, wrap, align right");

      groupPanel.add(ckPanel, "push, growx, wrap");
      groupPanel.add(cameraPanel, "push, growx");

      add(lblInfo, "growx, push, wrap");
      add(groupPanel, "push, growx");
   }

   private void setupDropdownCheckpoints(final List<CheckpointDetails> checkpoints) {
      final Vector<CheckpointSelectionValue> values = new Vector<>();
      for (final CheckpointDetails checkpoint : checkpoints) {
         final CheckpointSelectionValue value = new CheckpointSelectionValue();
         value.setId(checkpoint.getId());
         value.setName(checkpoint.getName());
         values.add(value);
      }

      cmbCheckpoints = new JComboBox<CheckpointSelectionValue>();
      cmbCheckpoints.setModel(new DefaultComboBoxModel<>(values));
   }

   public Long getCheckpointId() {
      return ((CheckpointSelectionValue) cmbCheckpoints.getModel().getSelectedItem()).getId();
   }

}
