package ch.infbr5.sentinel.client.gui.components.journal.list;

import java.awt.Color;
import java.awt.Component;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;
import javax.swing.border.Border;

import net.miginfocom.swing.MigLayout;
import ch.infbr5.sentinel.client.gui.util.SwingHelper;
import ch.infbr5.sentinel.client.util.Formater;
import ch.infbr5.sentinel.client.wsgen.JournalGefechtsMeldung;

public class ListCellJournalGefechtsMeldungRenderer<T extends JournalGefechtsMeldung> extends JPanel implements
		ListCellRenderer<T> {

	private static final long serialVersionUID = 1L;

	private Border raisedbevel;

	private Border loweredbevel;

	private JCheckBox ckbDone;

	private JLabel lblDate;

	private JLabel lblMeldungFuer;

	private JLabel lblCheckpoint;

	public ListCellJournalGefechtsMeldungRenderer() {
		setLayout(new MigLayout());

		// Borders
		raisedbevel = BorderFactory.createRaisedBevelBorder();
		loweredbevel = BorderFactory.createLoweredBevelBorder();

		// Detailpanel
		lblDate = new JLabel();
		add(lblDate, "width 100px");

		lblCheckpoint = new JLabel();
		add(lblCheckpoint, "width 100px, alignx center");

		lblMeldungFuer = new JLabel();
		add(lblMeldungFuer);

		ckbDone = new JCheckBox();
		add(ckbDone, "push, alignx right");
	}

	@Override
	public Component getListCellRendererComponent(JList<? extends T> list, T value, int index, boolean isSelected,
			boolean cellHasFocus) {

		String status = "";
		String meldungFor = "";
		boolean isDone = false;
		String date = "";
		Color color = null;
		String checkpoint = "";

		if (value.getZeitpunktMeldungsEingang() != null) {
			date = Formater.formatDateTime(value.getZeitpunktMeldungsEingang());
		}
		checkpoint = String.valueOf(value.getCheckpoint().getName());

		meldungFor = Formater.getFullName(value.getWeiterleitenAnPerson());

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

		ckbDone.setText(status);
		lblDate.setText(date);
		lblMeldungFuer.setText(meldungFor);
		lblCheckpoint.setText(checkpoint);
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
	}

}
