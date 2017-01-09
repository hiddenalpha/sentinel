package ch.infbr5.sentinel.client.gui.components.configuration;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import ch.infbr5.sentinel.client.gui.util.ColorChooserLabel;
import ch.infbr5.sentinel.common.gui.util.SwingHelper;

public abstract class AbstractAdminDetailPanel<T> extends JPanel {

   private static final long serialVersionUID = 1L;

   protected T data;

   public void setDataRecord(final T record) {
      data = record;
      if (data != null) {
         setFieldValues();
      } else {
         clearFieldValues();
      }
   }

   public T getDataRecord() {
      if (data != null) {
         getFieldValues();
      }

      return data;
   }

   protected JTextField createField(final String fieldCaption) {
      return createField(fieldCaption, "");
   }

   protected JTextField createField(final String fieldCaption, final String regex) {
      add(SwingHelper.createLabel(fieldCaption), "gap para");
      final JTextField field = SwingHelper.createTextField(30, regex);
      field.setName(fieldCaption);
      add(field, "span, growx");

      return field;
   }

   protected ColorChooserLabel createColorChooser(final String fieldCaption, final boolean withRemover) {
      add(SwingHelper.createLabel(fieldCaption), "gap para");
      final ColorChooserLabel chooser = new ColorChooserLabel();
      chooser.setName(fieldCaption);
      if (withRemover) {
         add(chooser, "gap para");
      } else {
         add(chooser, "span, growx");
      }
      return chooser;
   }

   protected JLabel createColorChooserRemover(final ColorChooserLabel lbl) {
      final JLabel remover = new JLabel();
      remover.setText("Farbe entfernen");
      remover.setForeground(Color.blue);
      remover.setFont(new Font("Arial", Font.ITALIC, 12));
      remover.addMouseListener(new MouseAdapter() {
         @Override
         public void mouseClicked(final MouseEvent e) {
            if (remover.isEnabled()) {
               lbl.setBackgroundHtmlColor(null);
            }
         }
      });
      add(remover, "span, growx");
      return remover;
   }

   protected JCheckBox createCheckbox(final String fieldCaption) {
      JCheckBox checkbox = new JCheckBox();

      add(SwingHelper.createLabel(fieldCaption), "gap para");
      checkbox = SwingHelper.createCheckBox();
      add(checkbox, "span, growx");

      return checkbox;
   }

   public abstract void getFieldValues();

   public abstract void setFieldValues();

   public abstract void clearFieldValues();

   public abstract void setEditable(boolean mode);

}
