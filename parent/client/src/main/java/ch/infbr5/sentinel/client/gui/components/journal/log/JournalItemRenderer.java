package ch.infbr5.sentinel.client.gui.components.journal.log;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.util.Date;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTextPane;
import javax.swing.ListCellRenderer;
import javax.swing.border.Border;

import net.miginfocom.swing.MigLayout;
import ch.infbr5.sentinel.client.gui.util.SwingHelper;
import ch.infbr5.sentinel.client.util.Formater;
import ch.infbr5.sentinel.client.wsgen.JournalEintrag;
import ch.infbr5.sentinel.client.wsgen.JournalGefechtsMeldung;

public class JournalItemRenderer<T extends JournalEintrag> extends JPanel implements ListCellRenderer<T> {

	private static final long serialVersionUID = 1L;

	private Border raisedbevel;

	private Border loweredbevel;

	private JLabel lblDate;

	private JLabel lblCreator;

	private JLabel lblStatus;

	private JLabel lblMeldungFuer;

	private JTextPane txtPaneMessage;

	public JournalItemRenderer() {
		super();
		setLayout(new MigLayout("","3[fill,grow][fill,right]3","3[]3[]3"));

		raisedbevel = BorderFactory.createRaisedBevelBorder();
		loweredbevel = BorderFactory.createLoweredBevelBorder();

		lblCreator = new JLabel();
		setSmallStyle(lblCreator);
		add(lblCreator);

		lblDate = new JLabel();
		setSmallStyle(lblDate);
		add(lblDate,"wrap");

		lblMeldungFuer = new JLabel();
		add(lblMeldungFuer,"wrap");

		txtPaneMessage = new JTextPane();
		txtPaneMessage.setPreferredSize(new Dimension(200, 50));
		add(txtPaneMessage,"spanx");

		lblStatus = new JLabel();
		setSmallStyle(lblStatus);
		add(lblStatus);

		setBackground(Color.WHITE);
	}

	private void setSmallStyle(JLabel label) {
		label.setFont(new Font("Arial", Font.PLAIN, 10));
		label.setForeground(Color.black);
	}

	@Override
	public Component getListCellRendererComponent(JList<? extends T> list, T value,
			int index, boolean isSelected, boolean cellHasFocus) {

		JournalEintrag eintrag = (JournalEintrag) value;

		String creator = "";
		String message = "";
		String status = "";
		String meldungFor = "";
		String date = Formater.formatWithTime(new Date(eintrag.getMillis()));
		Color color = null;

		if (eintrag instanceof JournalGefechtsMeldung) {
			JournalGefechtsMeldung j = (JournalGefechtsMeldung) eintrag;
			message = j.getText();
			creator = j.getCreator();

			meldungFor = "";
			if (j.getPersonDetails() != null) {
				meldungFor = "Meldung ist für " + j.getPersonDetails().getGrad() + " " + j.getPersonDetails().getName() + " " + j.getPersonDetails().getVorname();
			}

			if (j.isDone()) {
				status = "Erledigt";
				color = SwingHelper.COLOR_GREEN;
			} else {
				status = "Noch nicht erledigt";
				color = SwingHelper.COLOR_RED;
			}
		}

		setBackground(color);
		txtPaneMessage.setBackground(color);
		lblCreator.setText("Ersteller: " + creator);
		lblDate.setText(date);
		txtPaneMessage.setText(message);
		lblStatus.setText(status);
		lblMeldungFuer.setText(meldungFor);

		if (cellHasFocus){
			setBorder(loweredbevel);
		} else {
			setBorder(raisedbevel);
		}

		return this;
	}

}
