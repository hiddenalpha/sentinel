package ch.infbr5.sentinel.client.gui.components.journal;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Date;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import net.miginfocom.swing.MigLayout;
import ch.infbr5.sentinel.client.gui.util.SwingHelper;
import ch.infbr5.sentinel.client.util.ConfigurationLocalHelper;
import ch.infbr5.sentinel.client.util.ServiceHelper;

public class JournalNewMessagePanel extends JPanel implements ActionListener {

	private JTextArea fieldMessage;
	private JButton saveButton;
	private JButton cancelButton;

	public JournalNewMessagePanel() {
		setLayout(new MigLayout("inset 20"));

		SwingHelper.addSeparator(this, "Journaleintrag");

		add(SwingHelper.createLabel("Text"), "gap para");
		fieldMessage = SwingHelper.createTextArea(7, 20);
		JScrollPane scrollPane = new JScrollPane(fieldMessage);

		fieldMessage.setLineWrap(true);
		fieldMessage.setWrapStyleWord(true);
		scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

		add(scrollPane, "span, growx");
		add(SwingHelper.createLabel("Meldung für"), "gap para");
		add(SwingHelper.createTextField(30), "span, growx");

		saveButton = new JButton("Speichern");
		saveButton.addActionListener(this);
		saveButton.setActionCommand("JOURNALPANEL_SAVE");
		add(saveButton, "tag ok, span, split");

		cancelButton = new JButton("Abbrechen");
		cancelButton.addActionListener(this);
		cancelButton.setActionCommand("JOURNALPANEL_CANCEL");
		add(cancelButton, "tag cancel");

	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand().equals("JOURNALPANEL_SAVE")) {
			if (!fieldMessage.getText().equals("")) {
				Date now = new Date();
				// TODO Operator
				ServiceHelper.getJournalService().addJournalEintrag(now.getTime(), "INFO", fieldMessage.getText(), "Operator",
						"trigger", ConfigurationLocalHelper.getConfig().getCheckpointId());
				clearFields();
			}
		} else if (e.getActionCommand().equals("JOURNALPANEL_CANCEL")) {
			clearFields();
		}

	}

	private void clearFields() {
		fieldMessage.setText("");

	}

}
