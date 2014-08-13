package ch.infbr5.sentinel.client.config.checkpoint;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import net.miginfocom.swing.MigLayout;

import org.apache.log4j.Logger;

import ch.infbr5.sentinel.client.config.ConfigurationHelper;
import ch.infbr5.sentinel.client.util.ServiceHelper;
import ch.infbr5.sentinel.client.wsgen.ConfigurationDetails;
import ch.infbr5.sentinel.common.config.ConfigConstants;
import ch.infbr5.sentinel.common.gui.util.SwingHelper;

public class CheckpointConfigurationCamerasPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	private static final Logger log = Logger.getLogger(CheckpointConfigurationCamerasPanel.class);

	private JLabel lblCam;

	private JButton btnAdd;

	private JButton btnRemove;

	private JList<ConfigurationSelectionValue> cams;

	public CheckpointConfigurationCamerasPanel() {
		lblCam = SwingHelper.createLabel("Kamera-URLs");
		btnAdd = new JButton("+");
		btnAdd.addActionListener(createAddListener());
		btnRemove = new JButton("-");
		btnRemove.addActionListener(createRemoveListener());
		setupList();

		setLayout(new MigLayout());

		add(lblCam, "");
		add(btnRemove, "align right");
		add(btnAdd, "wrap, align right");

		add(new JScrollPane(cams), "push, growx, span");
	}

	private ActionListener createAddListener() {
		return new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				String option = JOptionPane.showInputDialog(null, "Trage die URL der Kamera ein.", "Neue Kamera", JOptionPane.OK_CANCEL_OPTION);
				if (option != null && !option.isEmpty()) {
					String camKey = evaluateNewCamKey();
					log.debug("Neue Kamera " + option + " am key " + camKey);
					ConfigurationDetails detail = new ConfigurationDetails();
					detail.setKey(camKey);
					detail.setLongValue(0);
					detail.setStringValue(option);
					ServiceHelper.getConfigurationsService().updateConfigurationValue(detail);
					detail.setId(ServiceHelper.getConfigurationsService().getGlobalConfigurationValue(camKey).getConfigurationDetails().get(0).getId());
					((DefaultListModel<ConfigurationSelectionValue>) cams.getModel()).add(0, new ConfigurationSelectionValue(detail));
				}
			}
		};
	}

	// TODO Eventuell sollten die Kameras nicht über das Konfigurations Model verwaltet werden
	// Darum bedarf es hier einer "key evaluation"
	private String evaluateNewCamKey() {
		List<ConfigurationDetails> details = ConfigurationHelper.loadConfigurationIPCams();
		int newId = 1;
		for (int id = 1; id < (details.size() + 2); id++) {
			boolean foundForCurrentId = false;
			for (ConfigurationDetails detail : details) {
				if (Integer.valueOf(detail.getKey().substring(10)).intValue() == id) {
					foundForCurrentId = true;
				}
			}
			if (!foundForCurrentId) {
				newId = id;
				break;
			}
		}

		return ConfigConstants.URL_IPCAM_ + String.valueOf(newId);
	}

	private ActionListener createRemoveListener() {
		return new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				ConfigurationSelectionValue selectedValue = cams.getSelectedValue();
				if (selectedValue != null) {
					if (JOptionPane.YES_OPTION == JOptionPane.showConfirmDialog(null, "Kamera wirklich löschen?", "Kamera löschen", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE)) {
						ServiceHelper.getConfigurationsService().removeConfiguration(selectedValue.detail.getId());
						log.debug("Kamera " + selectedValue.detail.getStringValue() + " entfernt");
						((DefaultListModel<ConfigurationSelectionValue>) cams.getModel()).remove(cams.getSelectedIndex());
					}
				}
			}
		};
	}

	private void setupList() {
		cams = new JList<>();
		List<ConfigurationDetails> details = ConfigurationHelper.loadConfigurationIPCams();
		DefaultListModel<ConfigurationSelectionValue> model = new DefaultListModel<>();
		for (ConfigurationDetails detail : details) {
			model.addElement(new ConfigurationSelectionValue(detail));
		}
		cams.setModel(model);
	}

	class ConfigurationSelectionValue {

		private ConfigurationDetails detail;

		ConfigurationSelectionValue(ConfigurationDetails detail) {
			this.detail = detail;
		}

		@Override
		public String toString() {
			return detail.getStringValue();
		}

	}

}
