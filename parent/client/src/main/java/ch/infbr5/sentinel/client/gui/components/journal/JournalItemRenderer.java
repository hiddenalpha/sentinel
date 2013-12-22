package ch.infbr5.sentinel.client.gui.components.journal;

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
import ch.infbr5.sentinel.client.util.Formater;
import ch.infbr5.sentinel.client.wsgen.JournalEintragDetails;

public class JournalItemRenderer extends JPanel implements ListCellRenderer<JournalEintragDetails> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JLabel labelCause;
	private Border raisedbevel;
	private Border loweredbevel;
	private JLabel labelDate;
	private JLabel labelCrator;
	private JTextPane causePane;
	
	public JournalItemRenderer() {
		super();
		setLayout(new MigLayout("","3[fill,grow][fill,right]3","3[]3[]3"));
		
		raisedbevel = BorderFactory.createRaisedBevelBorder();    
		loweredbevel = BorderFactory.createLoweredBevelBorder();
		
		labelCrator = new JLabel();
		setSmallStyle(labelCrator);
		add(labelCrator);
		
		labelDate = new JLabel();
		setSmallStyle(labelDate);
		add(labelDate,"wrap");
		
		causePane = new JTextPane();
		causePane.setPreferredSize(new Dimension(200, 50));
		add(causePane,"spanx");
		
		//setOpaque(true);
		setBackground(Color.WHITE);
		
	}
	
	private void setSmallStyle(JLabel label) {
		label.setFont(new Font("Arial", Font.PLAIN, 10));
		label.setForeground(Color.GRAY);
	}

	@Override
	public Component getListCellRendererComponent(JList<? extends JournalEintragDetails> list, JournalEintragDetails value,
			int index, boolean isSelected, boolean cellHasFocus) {
		labelCrator.setText(value.getOperator());
		labelDate.setText(Formater.formatWithTime(new Date(value.getMillis())));
		causePane.setText(""+value.getMessage());
		
		
		
		if (cellHasFocus){
			setBorder(loweredbevel);
		} else {
			setBorder(raisedbevel);
		}

		return this;
	}

}
