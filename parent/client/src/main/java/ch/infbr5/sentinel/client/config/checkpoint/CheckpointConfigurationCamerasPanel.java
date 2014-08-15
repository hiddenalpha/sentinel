package ch.infbr5.sentinel.client.config.checkpoint;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Enumeration;
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

import ch.infbr5.sentinel.client.util.ServiceHelper;
import ch.infbr5.sentinel.client.wsgen.IpCams;
import ch.infbr5.sentinel.common.gui.util.SwingHelper;

public class CheckpointConfigurationCamerasPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	private static final Logger log = Logger.getLogger(CheckpointConfigurationCamerasPanel.class);

	private JLabel lblCam;

	private JButton btnAdd;

	private JButton btnRemove;

	private JList<String> cams;

	private boolean selfManaged;

	public CheckpointConfigurationCamerasPanel(boolean selfManaged, List<String> camUrls) {
		this.selfManaged = selfManaged;

		lblCam = SwingHelper.createLabel("Kamera-URLs");
		btnAdd = new JButton("+");
		btnAdd.addActionListener(createAddListener());
		btnRemove = new JButton("-");
		btnRemove.addActionListener(createRemoveListener());
		setupList(camUrls);

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
				String option = JOptionPane.showInputDialog(null, "Trage die URL der Kamera ein.", "Neue Kamera",
						JOptionPane.OK_CANCEL_OPTION);
				if (option != null && !option.isEmpty()) {
					log.debug("Neue Kamera " + option);
					((DefaultListModel<String>) cams.getModel()).add(0, option);
					updateData();
				}
			}
		};
	}

	private ActionListener createRemoveListener() {
		return new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				String selectedValue = cams.getSelectedValue();
				if (selectedValue != null) {
					if (JOptionPane.YES_OPTION == JOptionPane.showConfirmDialog(null, "Kamera wirklich löschen?",
							"Kamera löschen", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE)) {
						log.debug("Kamera " + selectedValue + " entfernt");
						((DefaultListModel<String>) cams.getModel()).remove(cams.getSelectedIndex());
						updateData();
					}
				}
			}
		};
	}

	private void setupList(List<String> camUrls) {
		cams = new JList<>();

		if (selfManaged) {
			IpCams ipCams = ServiceHelper.getConfigurationsService().getIPCams();
			DefaultListModel<String> model = new DefaultListModel<>();
			for (String url : ipCams.getCams()) {
				model.addElement(url);
			}
			cams.setModel(model);
		} else {
			DefaultListModel<String> model = new DefaultListModel<>();
			for (String url : camUrls) {
				model.addElement(url);
			}
			cams.setModel(model);
		}
	}

	private void updateData() {
		if (selfManaged) {
			Enumeration<String> elements = ((DefaultListModel<String>) cams.getModel()).elements();
			IpCams ipCams = new IpCams();
			while (elements.hasMoreElements()) {
				ipCams.getCams().add(elements.nextElement());
			}
			ServiceHelper.getConfigurationsService().setIPCams(ipCams);
		}
	}

	public IpCams getUrls() {
		IpCams urls = new IpCams();
		Enumeration<String> elements = ((DefaultListModel<String>) cams.getModel()).elements();
		while (elements.hasMoreElements()) {
			urls.getCams().add(elements.nextElement());
		}
		return urls;
	}

	public void setUrls(List<String> urls) {
		((DefaultListModel<String>) cams.getModel()).removeAllElements();
		for (String url : urls) {
			((DefaultListModel<String>) cams.getModel()).addElement(url);
		}
	}

}
