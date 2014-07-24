package ch.infbr5.sentinel.client.gui.components.journal.list;

import java.awt.Color;
import java.awt.Component;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.ListCellRenderer;
import javax.swing.border.Border;

import net.miginfocom.swing.MigLayout;
import ch.infbr5.sentinel.client.gui.util.SwingHelper;
import ch.infbr5.sentinel.client.util.Formater;
import ch.infbr5.sentinel.client.wsgen.JournalGefechtsMeldung;

public class ListCellJournalGefechtsMeldungRenderer<T extends JournalGefechtsMeldung> extends JPanel implements ListCellRenderer<T> {

	private static final long serialVersionUID = 1L;

	private Border raisedbevel;

	private Border loweredbevel;

	private JCheckBox ckbDone;

	private JLabel lblDate;

	private JLabel lblMeldungFuer;

	private JLabel lblCheckpoint;

	private JLabel lblWerWasWieWo;

	private JLabel txtPaneWerWasWieWo;

	private JLabel lblMassnahmen;

	private JTextArea txtPaneMassnahmen;

	private JPanel detailInfoPanel;

	private JPanel textPanel;

	public ListCellJournalGefechtsMeldungRenderer() {
		setLayout(new MigLayout("aligny top", "[20%][80%]"));

		// Borders
		raisedbevel = BorderFactory.createRaisedBevelBorder();
		loweredbevel = BorderFactory.createLoweredBevelBorder();

		// Detailpanel
		detailInfoPanel = new JPanel();
		detailInfoPanel.setLayout(new MigLayout());

		lblDate = new JLabel();
		detailInfoPanel.add(lblDate, "wrap");

		lblCheckpoint = new JLabel();
		detailInfoPanel.add(lblCheckpoint, "wrap");

		lblMeldungFuer = new JLabel();
		detailInfoPanel.add(lblMeldungFuer, "wrap");

		ckbDone = new JCheckBox();
		detailInfoPanel.add(ckbDone);

		add(detailInfoPanel, "aligny top");

		// Textpanel
		textPanel = new JPanel();
		textPanel.setLayout(new MigLayout());

		lblWerWasWieWo = new JLabel("<html><b>Wer/Was/Wie/Wo</b></html>");
		textPanel.add(lblWerWasWieWo, "wrap");

		txtPaneWerWasWieWo = new JLabel();
		textPanel.add(txtPaneWerWasWieWo, "growx, wrap");

		lblMassnahmen = new JLabel("<html><b>Massnahmen</b></html>");
		textPanel.add(lblMassnahmen, "wrap");

		txtPaneMassnahmen = new JTextArea();
		textPanel.add(txtPaneMassnahmen, "growx, wrap");

		add(textPanel, "aligny top");
	}

	@Override
	public Component getListCellRendererComponent(JList<? extends T> list, T value, int index, boolean isSelected,
			boolean cellHasFocus) {

		String werWasWieWo = "";
		String massnahmen = "";
		String status = "";
		String meldungFor = "";
		boolean isDone = false;
		String date = "";
		Color color = null;
		String checkpoint = "";

		if (value.getZeitpunktMeldungsEingang() != null) {
			date = Formater.formatDateTime(value.getZeitpunktMeldungsEingang());
		}
		werWasWieWo = value.getWerWasWoWie().trim();
		massnahmen = value.getMassnahme().trim();
		checkpoint = String.valueOf(value.getCheckpoint().getName());

		meldungFor = "";
		if (value.getWeiterleitenAnPerson() != null) {
			meldungFor = value.getWeiterleitenAnPerson().getGrad() + " " + value.getWeiterleitenAnPerson().getName()
					+ " " + value.getWeiterleitenAnPerson().getVorname();
		} else {
			meldungFor = "niemanden";
		}

		if (value.isIstErledigt()) {
			isDone = true;
			status = "";
			if (value.getZeitpunktErledigt() != null) {
				status += Formater.formatDateTime(value.getZeitpunktErledigt());
			}
			color = SwingHelper.COLOR_GREEN;
		} else {
			status = "Nicht erledigt";
			color = SwingHelper.COLOR_RED;
		}

		//txtPaneWerWasWieWo.setWrapStyleWord(true);
		//txtPaneMassnahmen.setWrapStyleWord(true);

		ckbDone.setText(status);
		lblDate.setText(date);
		lblMeldungFuer.setText(meldungFor);
		lblCheckpoint.setText(checkpoint);
		txtPaneWerWasWieWo.setText("<html>" + werWasWieWo + "</html>");
		txtPaneMassnahmen.setText(massnahmen);
		ckbDone.setSelected(isDone);

		setColor(color);

		if (cellHasFocus) {
			setBorder(loweredbevel);
		} else {
			setBorder(raisedbevel);
		}

		return this;
	}

	private void setColor(Color color) {
		setBackground(color);
		ckbDone.setBackground(color);
		txtPaneWerWasWieWo.setBackground(color);
		txtPaneMassnahmen.setBackground(color);
		textPanel.setBackground(color);
		detailInfoPanel.setBackground(color);
	}

}
