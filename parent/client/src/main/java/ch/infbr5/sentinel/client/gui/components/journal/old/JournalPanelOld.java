package ch.infbr5.sentinel.client.gui.components.journal.old;

import java.awt.Point;
import java.awt.Rectangle;

import javax.swing.DefaultListSelectionModel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JViewport;
import javax.swing.ListSelectionModel;
import javax.swing.event.TableModelEvent;

import ch.infbr5.sentinel.client.gui.components.journal.old.TableDataChangeListener;

import net.miginfocom.swing.MigLayout;

public class JournalPanelOld extends JPanel implements TableDataChangeListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JTable table;
	private ListSelectionModel selectionModel;

	public JournalPanelOld(JournalModel model) {
		super();

		this.setLayout(new MigLayout("wrap 1"));

		this.addElementsToTable(model);

		DetailInfoPanel detailInfoPanel = new DetailInfoPanel(model);
		this.add(detailInfoPanel);

		model.addTableDataChangedListener(this);

		this.selectionModel = new DefaultListSelectionModel();
		this.selectionModel
		.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		this.table.setSelectionModel(this.selectionModel);
		this.table.setRowSelectionAllowed(true);

		SelectionListener listener = new SelectionListener(this.table);
		listener.addDetailInfoPanelChangedListener(detailInfoPanel);

		this.selectionModel.addListSelectionListener(listener);
		this.table.getColumnModel().getSelectionModel()
		.addListSelectionListener(listener);
	}

	private void addElementsToTable(JournalModel model) {
		this.table = new JTable(new JournalTableModel(
				model.getJournalEintraege()));

		this.table.setFillsViewportHeight(true);

		// Create the scroll pane and add the table to it.
		JScrollPane scrollPane = new JScrollPane(this.table);

		// Add the scroll pane to this panel.
		this.add(scrollPane, "span");
	}

	// Copied from http://www.exampledepot.com/egs/javax.swing.table/Vis.html
	private void scrollToVisible(int rowIndex, int vColIndex) {
		if (!(this.table.getParent() instanceof JViewport)) {
			return;
		}

		JViewport viewport = (JViewport) this.table.getParent();

		// This rectangle is relative to the table where the
		// northwest corner of cell (0,0) is always (0,0).
		Rectangle rect = this.table.getCellRect(rowIndex, vColIndex, true);

		// The location of the viewport relative to the table
		Point pt = viewport.getViewPosition();

		// Translate the cell location so that it is relative
		// to the view, assuming the northwest corner of the
		// view is (0,0)
		rect.setLocation(rect.x - pt.x, rect.y - pt.y);

		// Scroll the area into view
		viewport.scrollRectToVisible(rect);
	}

	public void tableDataChanged(TableDataChangedEvent e) {
		this.table.tableChanged(new TableModelEvent(this.table.getModel()));

		int noOfRows = this.table.getRowCount();

		this.scrollToVisible(noOfRows - 1, 1);

		this.selectionModel.setSelectionInterval(noOfRows - 1, noOfRows - 1);
	}
}
