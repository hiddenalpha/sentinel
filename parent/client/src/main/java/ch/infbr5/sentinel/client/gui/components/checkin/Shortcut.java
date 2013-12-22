package ch.infbr5.sentinel.client.gui.components.checkin;

import java.awt.Color;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.BorderFactory;
import javax.swing.JLabel;

public class Shortcut extends JLabel implements MouseListener, CheckInChangeListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String key;

	private CheckInModel model;
	private CheckInOperation operation;

	public Shortcut(CheckInModel model, CheckInOperation operation, String key) {
		this.model = model;
		this.key = key;
		this.operation = operation;
		this.initComponents();
	}

	private void initComponents() {
		this.setText(this.key + ": " + this.operation.toString());
		this.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEtchedBorder(),
				BorderFactory.createEmptyBorder(2, 4, 2, 4)));

		this.setForgroundColor();

		this.addMouseListener(this);
		this.model.addCheckInChangedListener(this);
	}

	public void mouseClicked(MouseEvent e) {
		this.model.setOperation(this.operation);
	}

	public void mouseEntered(MouseEvent e) {
		this.setBackground(Color.RED);
	}

	public void mouseExited(MouseEvent e) {
		this.setBackground(null);
	}

	public void mousePressed(MouseEvent e) {
	}

	public void mouseReleased(MouseEvent e) {
	}

	private void setForgroundColor(){
		if (this.model.getSelectedOperation().equals(this.operation)) {
			this.setForeground(Color.RED);
		} else {
			this.setForeground(Color.BLACK);
		}
	}

	public void valueChanged(CheckInChangedEvent shortcutbarChangeEvent) {
		this.setForgroundColor();
	}

}
