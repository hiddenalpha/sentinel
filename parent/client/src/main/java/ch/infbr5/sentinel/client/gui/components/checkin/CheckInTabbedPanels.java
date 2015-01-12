package ch.infbr5.sentinel.client.gui.components.checkin;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.KeyEvent;
import java.text.SimpleDateFormat;

import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import javax.xml.datatype.XMLGregorianCalendar;

import ch.infbr5.sentinel.client.wsgen.PraesenzStatus;

public class CheckInTabbedPanels extends JTabbedPane implements ChangeListener, ImageChangeListener {

   private static final long serialVersionUID = 1L;

   private final CheckInModel model;

   private final String[] tabNames = { "Ausweis", "IN", "OUT", "Urlaub", "Angemeldet" };

   private final CheckInTableModel[] tableModels = new CheckInTableModel[5];

   public CheckInTabbedPanels(final CheckInModel model) {
      super();
      this.model = model;
      this.initComponents();
   }

   private void createCheckInTablePane(final int i, final PraesenzStatus status) {
      final JScrollPane scrollPane = new JScrollPane();
      this.tableModels[i] = new CheckInTableModel(status, model);
      final JTable table = new JTable();
      scrollPane.setViewportView(table);
      table.setModel(this.tableModels[i]);

      // Datumsfomat
      final TableCellRenderer tableCellRenderer = new DefaultTableCellRenderer() {

         private static final long serialVersionUID = 1L;
         SimpleDateFormat f = new SimpleDateFormat("dd.MM.yyyy  HH:mm");

         @Override
         public Component getTableCellRendererComponent(final JTable table, Object value, final boolean isSelected,
               final boolean hasFocus, final int row, final int column) {
            if (value instanceof XMLGregorianCalendar) {
               value = f.format(((XMLGregorianCalendar) value).toGregorianCalendar().getTime());
            }
            return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
         }
      };

      table.getColumnModel().getColumn(3).setCellRenderer(tableCellRenderer);

      // Context Menu
      table.addMouseListener(new CheckInTableContextMenu(table, tableModels[i], model));

      this.addTab(this.tabNames[i], scrollPane);
   }

   public void displayPersonSelectionDialog() {
      final CheckInSelectionValue[] values = this.model.getPersonenMitAusweis();

      CheckInSelectionValue selected = null;

      if (values.length > 0) {
         selected = (CheckInSelectionValue) JOptionPane.showInputDialog(this, "Manuele Auswahl",
               "Auswahl nur im Notfall (wird protokolliert).", JOptionPane.WARNING_MESSAGE, null, values, values[0]);
      } else {
         JOptionPane.showMessageDialog(this, "Keine Ausweise vorhanden", "Warnung", JOptionPane.WARNING_MESSAGE);
      }

      if (selected != null) {
         this.model.handleCheckinEvent(selected.getBarcode());
      }
   }

   @Override
   public void imageChanged(final ImageChangedEvent e) {
      this.setSelectedIndex(0);

      this.updateTabNames();
   }

   private void initComponents() {
      final JPanel panel1 = new JPanel();
      panel1.setLayout(new BorderLayout());

      panel1.add(new AusweisInfoPanel(this.model), BorderLayout.CENTER);
      panel1.add(new ShortcutbarPanel(this.model), BorderLayout.SOUTH);

      this.addTab("Ausweis", panel1);

      this.setMnemonicAt(0, KeyEvent.VK_1);

      this.createCheckInTablePane(1, PraesenzStatus.INNERHALB);
      this.createCheckInTablePane(2, PraesenzStatus.AUSSERHALB);
      this.createCheckInTablePane(3, PraesenzStatus.URLAUB);
      this.createCheckInTablePane(4, PraesenzStatus.ANGEMELDET);

      this.addChangeListener(this);
      this.model.addImageChangedListener(this);

      this.updateTabNames();
   }

   @Override
   public void stateChanged(final ChangeEvent e) {
      final int selectedIndex = this.getSelectedIndex();

      if (this.tableModels[selectedIndex] != null) {
         this.tableModels[selectedIndex].update();
      }

      this.updateTabNames();
   }

   private void updateTabNames() {
      this.setTitleAt(1, this.tabNames[1] + " (" + this.model.getCounterIn() + ")");
      this.setTitleAt(2, this.tabNames[2] + " (" + this.model.getCounterOut() + ")");
      this.setTitleAt(3, this.tabNames[3] + " (" + this.model.getCounterUrlaub() + ")");
      this.setTitleAt(4, this.tabNames[4] + " (" + this.model.getCounterAngemeldet() + ")");
   }
}
