package ch.infbr5.sentinel.client.gui.util;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.JColorChooser;
import javax.swing.JLabel;

public class ColorChooserLabel extends JLabel {

   private static final long serialVersionUID = 1L;

   private final MouseAdapter editColor;

   public ColorChooserLabel() {
      setMinimumSize(new Dimension(20, 20));
      setOpaque(true);
      setBorder(BorderFactory.createLineBorder(Color.black));

      editColor = new MouseAdapter() {
         @Override
         public void mouseClicked(final MouseEvent e) {
            final Color selectedColor = JColorChooser.showDialog(null, "Farbauswahl", getBackground());
            if (selectedColor != null) {
               setBackground(selectedColor);
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

   public void setBackgroundHtmlColor(String htmlColor) {
      if (htmlColor == null) {
         setBackground(null);
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
      setBackground(c);
   }

   public String getBackgroundHtmlColor() {
      final String rgb = Integer.toHexString(getBackground().getRGB());
      return rgb.substring(2, rgb.length());
   }

}
