package ch.infbr5.sentinel.client.config.server;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import net.miginfocom.swing.MigLayout;
import ch.infbr5.sentinel.client.util.ServiceHelper;
import ch.infbr5.sentinel.client.wsgen.ServerSetupInformation;

public class ServerConfigurationDialog extends JDialog {

	private static final long serialVersionUID = 1L;

	private JButton btnSave;

	private JButton btnCancel;

	private final ServerConfigurationPanel panel;

	public ServerConfigurationDialog(final JFrame parent, ServerSetupInformation info) {
		super(parent);

		setModal(true);
		setTitle("Server Konfiguration");
		setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		setResizable(false);
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				dispose();
				System.exit(0);
			}
		});

		panel = new ServerConfigurationPanel(info);

		btnSave = new JButton("Speichern");
		btnSave.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (panel.validateInfo()) {
					if (JOptionPane.YES_OPTION == JOptionPane.showConfirmDialog(parent, "Möchten Sie die Einstellung wirklich speichern?", "Konfiguration speichern", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE)) {
						ServiceHelper.getConfigurationsService().applyServerSetupInformation(panel.getInfo());
						dispose();
					}
				}
			}

		});

		btnCancel = new JButton("Abbrechen");
		btnCancel.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				dispose();
				System.exit(0);
			}
		});

		setLayout(new MigLayout());
		add(panel, "push, span, growx, wrap");
		add(btnSave, "tag ok, span, split");
		add(btnCancel, "tag cancel");
		setSize(430, 520);
		setLocationRelativeTo(null);
	}

}
