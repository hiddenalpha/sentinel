package ch.infbr5.sentinel.client.gui.components.checkin;

import java.awt.Image;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import ch.infbr5.sentinel.client.gui.components.journal.dialog.ChangeStatusGefechtsMeldungDialog;
import ch.infbr5.sentinel.client.gui.components.journal.panel.GefechtsJournalModel;
import ch.infbr5.sentinel.client.image.ImageLoader;
import ch.infbr5.sentinel.client.util.ServiceHelper;
import ch.infbr5.sentinel.client.util.Sound;
import ch.infbr5.sentinel.client.wsgen.JournalGefechtsMeldung;
import ch.infbr5.sentinel.client.wsgen.OperationResponse;
import ch.infbr5.sentinel.client.wsgen.OperationResponseStatus;
import ch.infbr5.sentinel.client.wsgen.PersonDetails;

public class CheckInModelImpl implements CheckInModel {

   private final List<CheckInChangeListener> checkInChangeListeners = new ArrayList<CheckInChangeListener>();

   private final List<ImageChangeListener> imageChangeListeners = new ArrayList<ImageChangeListener>();

   private String messageText = "";
   private Image image;
   private CheckInOperation selectedOperation = CheckInOperation.CHECKIN;
   private final long checkpointId;
   private long counterIn;
   private long counterOut;
   private long counterUrlaub;
   private long counterAngemeldet;

   private OperationResponseStatus status;

   public CheckInModelImpl(final long checkpointId) {
      this.checkpointId = checkpointId;

      this.selectedOperation = CheckInOperation.CHECKIN;
      this.messageText = "";
      this.image = null;

      this.fireStateChanged();
      this.fireImageChanged();
   }

   @Override
   public void addCheckInChangedListener(final CheckInChangeListener l) {
      this.checkInChangeListeners.add(l);
   }

   @Override
   public void addImageChangedListener(final ImageChangeListener l) {
      this.imageChangeListeners.add(l);
   }

   private void fireImageChanged() {
      for (final ListIterator<ImageChangeListener> iterator = this.imageChangeListeners.listIterator(); iterator
            .hasNext();) {
         final ImageChangeListener l = iterator.next();
         l.imageChanged(new ImageChangedEvent(this));
      }
   }

   private void fireStateChanged() {
      for (final ListIterator<CheckInChangeListener> iterator = this.checkInChangeListeners.listIterator(); iterator
            .hasNext();) {
         final CheckInChangeListener l = iterator.next();
         l.valueChanged(new CheckInChangedEvent(this));
      }
   }

   @Override
   public long getCounterAngemeldet() {
      return this.counterAngemeldet;
   }

   @Override
   public long getCounterIn() {
      return this.counterIn;
   }

   @Override
   public long getCounterOut() {
      return this.counterOut;
   }

   @Override
   public long getCounterUrlaub() {
      return this.counterUrlaub;
   }

   @Override
   public Image getImage() {
      return this.image;
   }

   @Override
   public String getMessageText() {
      return this.messageText;
   }

   @Override
   public CheckInSelectionValue[] getPersonenMitAusweis() {
      final OperationResponse response = ServiceHelper.getSentinelService().getPersonenMitAusweis();

      final CheckInSelectionValue[] pdArray = new CheckInSelectionValue[response.getPersonDetails().size()];
      for (int j = 0; j < pdArray.length; j++) {
         final PersonDetails pd = response.getPersonDetails().get(j);
         final CheckInSelectionValue selectionValue = new CheckInSelectionValue();
         if ((pd.getGrad() != null) && (pd.getGrad() != "")) {
            selectionValue.setDisplayName(pd.getName() + " " + pd.getVorname() + ", " + pd.getGrad());
         } else {
            selectionValue.setDisplayName(pd.getName() + " " + pd.getVorname());
         }
         selectionValue.setBarcode(pd.getBarcode());

         pdArray[j] = selectionValue;
      }

      return pdArray;
   }

   @Override
   public CheckInOperation getSelectedOperation() {
      return this.selectedOperation;
   }

   @Override
   public OperationResponseStatus getStatus() {
      return this.status;
   }

   @Override
   public void handleCheckinEvent(final String barcode) {
      OperationResponse response;
      switch (this.selectedOperation) {
         case CHECKIN:
            response = ServiceHelper.getSentinelService().checkin(this.checkpointId, barcode);
         break;

         case CHECKOUT:
            response = ServiceHelper.getSentinelService().checkout(this.checkpointId, barcode);
         break;

         case URLAUB:
            response = ServiceHelper.getSentinelService().beurlauben(this.checkpointId, barcode);
         break;

         case ANMELDEN:
            response = ServiceHelper.getSentinelService().anmelden(this.checkpointId, barcode);
         break;

         case ABMELDEN:
            response = ServiceHelper.getSentinelService().abmelden(this.checkpointId, barcode);
         break;

         default:
            // TODO: Log error
            return;
      }

      this.messageText = response.getMessage();
      this.status = response.getStatus();

      image = ImageLoader.loadImage(response.getImageId());

      this.updateCounter(response);

      this.selectedOperation = CheckInOperation.CHECKIN;

      this.fireStateChanged();
      this.fireImageChanged();

      if (status.equals(OperationResponseStatus.SUCESS)) {
         Sound.ok();
      } else {
         Sound.warn();
      }

      // Trigger Einträge anzeigen.
      final List<JournalGefechtsMeldung> triggerEintraege = response.getPersonTriggerEintraege();
      for (final JournalGefechtsMeldung eintrag : triggerEintraege) {
         this.showPersonTriggerInfoPopup(eintrag);
      }
   }

   @Override
   public void resetImageAndMessage() {
      image = null;
      messageText = "";
      status = null;

      fireStateChanged();
      fireImageChanged();
   }

   @Override
   public void setOperation(final CheckInOperation op) {
      this.selectedOperation = op;

      this.fireStateChanged();
   }

   private void showPersonTriggerInfoPopup(final JournalGefechtsMeldung personTriggerEintrag) {
      final ChangeStatusGefechtsMeldungDialog d = new ChangeStatusGefechtsMeldungDialog(null, personTriggerEintrag,
            journalGefechtsModel);
      d.setVisible(true);
   }

   @Override
   public void updateCounter(final OperationResponse response) {
      this.counterIn = response.getCounterIn();
      this.counterOut = response.getCounterOut();
      this.counterUrlaub = response.getCounterUrlaub();
      this.counterAngemeldet = response.getCounterAngemeldet();
   }

   private GefechtsJournalModel journalGefechtsModel;

   @Override
   public void setJournalGefechtsModel(final GefechtsJournalModel model) {
      journalGefechtsModel = model;
   }

}
