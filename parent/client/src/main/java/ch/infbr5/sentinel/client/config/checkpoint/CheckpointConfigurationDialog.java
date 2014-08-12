package ch.infbr5.sentinel.client.config.checkpoint;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import net.miginfocom.swing.MigLayout;
import ch.infbr5.sentinel.client.config.ConfigurationLocalHelper;
import ch.infbr5.sentinel.client.util.ServiceHelper;

public class CheckpointConfigurationDialog extends JDialog {

	private static final long serialVersionUID = 1L;

	// UI Components
	private JButton btnSave;

	private JButton btnCancel;

	private final CheckpointConfigurationPanel panel;

	public CheckpointConfigurationDialog(final JFrame parent, final CheckpointConfigurator config, String info, final boolean isConfigurationWhileStartup) {
		super(parent);

		setModal(true);
		setTitle("Checkpoint konfigurieren");
		setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		setResizable(false);
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				dispose();
				if (isConfigurationWhileStartup) {
					System.exit(0);
				}
			}
		});

		panel = new CheckpointConfigurationPanel(info, ServiceHelper.getConfigurationsService().getCheckpoints().getCheckpointDetails());

		btnSave = new JButton("Speichern");
		btnSave.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (isConfigurationWhileStartup) {
					save();
					dispose();
				} else {
					if (JOptionPane.YES_OPTION == JOptionPane.showConfirmDialog(parent, "Möchten Sie die Einstellung wirklich speichern? Der Client wird automatisch beendet. Starten Sie diesen dannach neu.", "Konfiguration speichern", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE)) {
						save();
						System.exit(0);
					}
				}
			}

			private void save() {
				ConfigurationLocalHelper.getConfig().setCheckpointId(panel.getCheckpointId());
			}
		});

		btnCancel = new JButton("Abbrechen");
		btnCancel.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				dispose();
				if (isConfigurationWhileStartup) {
					System.exit(0);
				}
			}
		});

		setLayout(new MigLayout());
		add(panel, "push, span, growx, wrap");
		add(btnSave, "tag ok, span, split");
		add(btnCancel, "tag cancel");
		setSize(500, 420);
		setLocationRelativeTo(null);
	}

}
