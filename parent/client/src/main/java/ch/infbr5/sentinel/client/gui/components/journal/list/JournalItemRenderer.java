package ch.infbr5.sentinel.client.gui.components.journal.list;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.util.Date;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
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

	private JCheckBox ckbDone;

	private JLabel lblDate;

	private JLabel lblMeldungFuer;

	private JLabel lblWerWasWieWo;

	private JTextPane txtPaneWerWasWieWo;

	private JLabel lblMassnahmen;

	private JTextPane txtPaneMassnahmen;

	public JournalItemRenderer() {
		super();
		setLayout(new MigLayout("","3[fill,grow][fill,right]3","3[]3[]3"));

		raisedbevel = BorderFactory.createRaisedBevelBorder();
		loweredbevel = BorderFactory.createLoweredBevelBorder();

		lblDate = new JLabel();
		setSmallStyle(lblDate);
		add(lblDate,"");

		ckbDone = new JCheckBox();
		setSmallStyle(ckbDone);
		add(ckbDone,"wrap");

		lblMeldungFuer = new JLabel();
		add(lblMeldungFuer,"spanx, gapbottom 5");

		lblWerWasWieWo = new JLabel("Wer/Was/Wie/Wo");
		lblWerWasWieWo.setFont(new Font("Arial", Font.BOLD, 12));
		add(lblWerWasWieWo);

		lblMassnahmen = new JLabel("Massnahmen");
		lblMassnahmen.setFont(new Font("Arial", Font.BOLD, 12));
		add(lblMassnahmen, "wrap");

		txtPaneWerWasWieWo = new JTextPane();
		add(txtPaneWerWasWieWo,"aligny top, width 50%");

		txtPaneMassnahmen = new JTextPane();
		add(txtPaneMassnahmen,"aligny top, width 50%");

	}

	private void setSmallStyle(JComponent label) {
		label.setFont(new Font("Arial", Font.PLAIN, 10));
		label.setForeground(Color.black);
	}

	@Override
	public Component getListCellRendererComponent(JList<? extends T> list, T value,
			int index, boolean isSelected, boolean cellHasFocus) {

		JournalEintrag eintrag = (JournalEintrag) value;

		String werWasWieWo = "";
		String massnahmen = "";
		String status = "";
		String meldungFor = "";
		boolean isDone = false;
		String date = Formater.formatDateTime(new Date(eintrag.getMillis()));
		Color color = null;

		if (eintrag instanceof JournalGefechtsMeldung) {
			JournalGefechtsMeldung j = (JournalGefechtsMeldung) eintrag;

			if (j.getZeitpunktMeldungsEingang() != null) {
				date = Formater.formatDateTime(j.getZeitpunktMeldungsEingang());
			}
			werWasWieWo = j.getWerWasWoWie();
			massnahmen = j.getMassnahme();

			meldungFor = "";
			if (j.getWeiterleitenAnPerson() != null) {
				meldungFor = j.getWeiterleitenAnPerson().getGrad() + " " + j.getWeiterleitenAnPerson().getName() + " " + j.getWeiterleitenAnPerson().getVorname();
			}

			if (j.isIstErledigt()) {
				isDone = true;
				status = "Erledigt";
				if (j.getZeitpunktErledigt() != null) {
					status += " am " + Formater.formatDateTime(j.getZeitpunktErledigt());
				}
				color = SwingHelper.COLOR_GREEN;
			} else {
				status = "Noch nicht erledigt";
				color = SwingHelper.COLOR_RED;
			}
		}

		setBackground(color);
		ckbDone.setBackground(color);
		txtPaneWerWasWieWo.setBackground(color);
		txtPaneMassnahmen.setBackground(color);

		ckbDone.setText(status);
		lblDate.setText(date);
		lblMeldungFuer.setText("<html><b>Meldung an</b> " + meldungFor + "</html>");
		txtPaneWerWasWieWo.setText(werWasWieWo);
		txtPaneMassnahmen.setText(massnahmen);
		ckbDone.setSelected(isDone);

		if (cellHasFocus){
			setBorder(loweredbevel);
		} else {
			setBorder(raisedbevel);
		}

		return this;
	}

}
