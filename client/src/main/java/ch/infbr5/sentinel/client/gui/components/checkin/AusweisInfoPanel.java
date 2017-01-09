package ch.infbr5.sentinel.client.gui.components.checkin;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.Timer;

import ch.infbr5.sentinel.client.wsgen.OperationResponseStatus;
import ch.infbr5.sentinel.common.gui.util.ImageLoader;

public class AusweisInfoPanel extends JPanel implements CheckInChangeListener, ImageChangeListener, ActionListener {

   public static final String LABEL_STATUS_TEXT = "LABEL_STATUS_TEXT";
   /**
    *
    */
   private static final long serialVersionUID = 1L;
   private JLabel statusTextLabel;
   private ImageIcon noFotoIcon;
   private JLabel fotoLabel;
   private final CheckInModel model;
   private final Timer timer;
   private Color defaultBackgroundColor;

   public AusweisInfoPanel(final CheckInModel model) {
      super();
      this.model = model;
      this.initComponents();

      timer = new Timer(5000, this);

   }

   @Override
   public void imageChanged(final ImageChangedEvent e) {
      if (this.model.getImage() != null) {
         this.fotoLabel.setIcon(new ImageIcon(this.model.getImage()));
         if (timer.isRunning()) {
            timer.restart();
         } else {
            timer.start();
         }

      } else {
         this.fotoLabel.setIcon(this.noFotoIcon);
         timer.stop();
      }
   }

   @Override
   public void actionPerformed(final ActionEvent arg0) {
      if (timer.isRunning()) {
         timer.stop();
         if (model.getImage() != null) {
            model.resetImageAndMessage();
         }
      }

   }

   private void initComponents() {
      defaultBackgroundColor = getBackground();

      this.setLayout(new BorderLayout());

      this.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createTitledBorder("Ausweis Foto"),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)));

      this.noFotoIcon = new ImageIcon(ImageLoader.loadNobodyImage());

      this.fotoLabel = new JLabel(this.noFotoIcon);
      this.add(this.fotoLabel);

      this.statusTextLabel = new JLabel(this.model.getMessageText(), SwingConstants.CENTER);
      this.statusTextLabel.setName(LABEL_STATUS_TEXT);
      this.add(this.statusTextLabel, BorderLayout.SOUTH);

      this.model.addCheckInChangedListener(this);
      this.model.addImageChangedListener(this);
   }

   @Override
   public void valueChanged(final CheckInChangedEvent e) {
      this.statusTextLabel.setText(this.model.getMessageText());

      if (this.model.getStatus() == OperationResponseStatus.SUCESS) {
         this.setBackground(Color.GREEN);
      } else if (this.model.getStatus() == OperationResponseStatus.FAIL) {
         this.setBackground(Color.RED);
      } else {
         setBackground(defaultBackgroundColor);
      }
   }

}
