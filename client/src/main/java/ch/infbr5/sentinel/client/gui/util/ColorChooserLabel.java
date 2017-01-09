package ch.infbr5.sentinel.client.gui.util;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.JColorChooser;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.colorchooser.AbstractColorChooserPanel;

public class ColorChooserLabel extends JLabel {

   private static final long serialVersionUID = 1L;

   private final MouseAdapter editColor;

   private boolean isSetNull = false;

   public ColorChooserLabel() {
      setMinimumSize(new Dimension(30, 20));
      setOpaque(true);
      setBorder(BorderFactory.createLineBorder(Color.black));

      editColor = new MouseAdapter() {
         @Override
         public void mouseClicked(final MouseEvent e) {

            final JColorChooser colorChooser = new JColorChooser();

            final AbstractColorChooserPanel[] panels = new AbstractColorChooserPanel[2];
            for (final AbstractColorChooserPanel panel : colorChooser.getChooserPanels()) {
               if ("Swatches".equals(panel.getDisplayName())) {
                  panels[0] = panel;
               }
               if ("RGB".equals(panel.getDisplayName())) {
                  panels[1] = panel;
               }

            }
            colorChooser.setChooserPanels(panels);
            colorChooser.setColor(getBackground());
            final JDialog dialog = JColorChooser.createDialog(null, "Farbauswahl", true, colorChooser, null, null);
            dialog.setVisible(true);

            Color selectedColor = colorChooser.getColor();
            if (selectedColor != null) {
               selectedColor = new Color(selectedColor.getRed(), selectedColor.getGreen(), selectedColor.getBlue());
               setBackgroundAsColor(selectedColor);
            }
         }
      };
      addMouseListener(editColor);
   }

   @Override
   public void setEnabled(final boolean mode) {
      super.setEnabled(mode);
      if (mode) {
         removeMouseListener(editColor);
         addMouseListener(editColor);
      } else {
         removeMouseListener(editColor);
      }
   }

   public void setBackgroundAsColor(final Color color) {
      if (color == null) {
         isSetNull = true;
         setBackground(Color.white);
         setText("leer");
         return;
      } else {
         setText("");
         isSetNull = false;
      }
      setBackground(color);
   }

   public void setBackgroundHtmlColor(String htmlColor) {
      if (htmlColor == null || htmlColor.isEmpty()) {
         setBackgroundAsColor(null);
         return;
      }
      if (!htmlColor.startsWith("#")) {
         htmlColor = "#" + htmlColor;
      }
      Color c = null;
      try {
         c = Color.decode(htmlColor);
      } catch (final NumberFormatException e) {
         c = null;
      }
      setBackgroundAsColor(c);
   }

   public String getBackgroundHtmlColor() {
      if (isSetNull) {
         return null;
      }
      final Color color = getBackground();
      if (color == null) {
         return null;
      } else {
         final String rgb = Integer.toHexString(color.getRGB());
         return rgb.substring(2, rgb.length());
      }
   }

}
