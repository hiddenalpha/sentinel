package ch.infbr5.sentinel.client.gui.components.checkin;

import java.awt.Image;

import ch.infbr5.sentinel.client.gui.components.journal.panel.GefechtsJournalModel;
import ch.infbr5.sentinel.client.wsgen.OperationResponse;
import ch.infbr5.sentinel.client.wsgen.OperationResponseStatus;


public interface CheckInModel {

	public void addCheckInChangedListener(CheckInChangeListener l);

	public void addImageChangedListener(ImageChangeListener l);

	public long getCounterAngemeldet();

	public long getCounterIn();

	public long getCounterOut();

	public long getCounterUrlaub();

	public Image getImage();

	public String getMessageText();

	public CheckInSelectionValue[] getPersonenMitAusweis();

	public CheckInOperation getSelectedOperation();

	public OperationResponseStatus getStatus();

	public void handleCheckinEvent(String barcode);

	public void setOperation(CheckInOperation op);

	public void resetImageAndMessage();

	public void updateCounter(OperationResponse response);

	public void setJournalGefechtsModel(GefechtsJournalModel model);

}