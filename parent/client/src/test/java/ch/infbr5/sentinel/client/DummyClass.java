package ch.infbr5.sentinel.client;

import java.awt.Color;
import java.lang.reflect.Array;
import java.lang.reflect.Field;

import javax.swing.JColorChooser;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.colorchooser.AbstractColorChooserPanel;

import org.junit.Test;

public class DummyClass {

   @Test
   public void testchooser() throws NoSuchFieldException, SecurityException, IllegalArgumentException,
   IllegalAccessException {
      final JColorChooser colorChooser = new JColorChooser();

      final AbstractColorChooserPanel[] panels = new AbstractColorChooserPanel[2];
      for (final AbstractColorChooserPanel panel : colorChooser.getChooserPanels()) {
         System.out.println(panel.getClass().getCanonicalName());
         if ("Swatches".equals(panel.getDisplayName())) {
            panels[0] = panel;
         }
         if ("RGB".equals(panel.getDisplayName())) {
            removeSlider(panel);
            panels[1] = panel;
         }

      }
      colorChooser.setChooserPanels(panels);
      JColorChooser.createDialog(null, "Dialog Title", true, colorChooser, null, null).setVisible(true);

      final Color selectedColor = colorChooser.getColor();
      System.out.println(selectedColor);

      // final Color selectedColor = colorChooser.showDialog(null,
      // "Farbauswahl", Color.red);
      // if (selectedColor != null) {
      // setBackground(selectedColor);
      // }

   }

   private void removeSlider(final AbstractColorChooserPanel cp) {
      try {
         final Field f = cp.getClass().getDeclaredField("panel");
         f.setAccessible(true);

         final Object colorPanel = f.get(cp);
         final Field f2 = colorPanel.getClass().getDeclaredField("spinners");
         f2.setAccessible(true);
         final Object spinners = f2.get(colorPanel);

         Object transpSlispinner = Array.get(spinners, 3);
         // if (i == colorPanels.length - 1) {
         transpSlispinner = Array.get(spinners, 4);
         // }
         final Field f3 = transpSlispinner.getClass().getDeclaredField("slider");
         f3.setAccessible(true);
         final JSlider slider = (JSlider) f3.get(transpSlispinner);
         slider.setEnabled(false);
         final Field f4 = transpSlispinner.getClass().getDeclaredField("spinner");
         f4.setAccessible(true);
         final JSpinner spinner = (JSpinner) f4.get(transpSlispinner);
         spinner.setEnabled(false);
      } catch (final Exception e) {
         e.printStackTrace();
      }
   }

}
