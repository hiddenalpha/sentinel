package ch.infbr5.sentinel.client.gui.components.checkin;

import java.awt.Image;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import ch.infbr5.sentinel.client.gui.components.journal.operator.dialog.OperatorInfoDialogPanel;
import ch.infbr5.sentinel.client.util.ImageCreator;
import ch.infbr5.sentinel.client.util.ServiceHelper;
import ch.infbr5.sentinel.client.util.Sound;
import ch.infbr5.sentinel.client.wsgen.OperationResponse;
import ch.infbr5.sentinel.client.wsgen.OperationResponseStatus;
import ch.infbr5.sentinel.client.wsgen.OperatorEintrag;
import ch.infbr5.sentinel.client.wsgen.PersonDetails;
import ch.infbr5.sentinel.utils.JournalEintragLogger;

public class CheckInModelImpl implements CheckInModel {

	private List<CheckInChangeListener> checkInChangeListeners = new ArrayList<CheckInChangeListener>();

	private List<ImageChangeListener> imageChangeListeners = new ArrayList<ImageChangeListener>();

	private String messageText = "";
	private Image image;
	private CheckInOperation selectedOperation = CheckInOperation.CHECKIN;
	private long checkpointId;
	private long counterIn;
	private long counterOut;
	private long counterUrlaub;
	private long counterAngemeldet;

	private JournalEintragLogger systemEintragLogger;

	private final JFrame parent;

	private OperationResponseStatus status;

	public CheckInModelImpl(long checkpointId, JournalEintragLogger systemEintragLogger, JFrame parent) {
		this.checkpointId = checkpointId;
		this.systemEintragLogger = systemEintragLogger;
		this.parent = parent;

		this.selectedOperation = CheckInOperation.CHECKIN;
		this.messageText = "";
		this.image = null;

		this.fireStateChanged();
		this.fireImageChanged();
	}

	public void addCheckInChangedListener(CheckInChangeListener l) {
		this.checkInChangeListeners.add(l);
	}

	public void addImageChangedListener(ImageChangeListener l) {
		this.imageChangeListeners.add(l);
	}

	private void fireImageChanged() {
		for (ListIterator<ImageChangeListener> iterator = this.imageChangeListeners.listIterator(); iterator.hasNext();) {
			ImageChangeListener l = iterator.next();
			l.imageChanged(new ImageChangedEvent(this));
		}
	}

	private void fireStateChanged() {
		for (ListIterator<CheckInChangeListener> iterator = this.checkInChangeListeners.listIterator(); iterator.hasNext();) {
			CheckInChangeListener l = iterator.next();
			l.valueChanged(new CheckInChangedEvent(this));
		}
	}

	public long getCounterAngemeldet() {
		return this.counterAngemeldet;
	}

	public long getCounterIn() {
		return this.counterIn;
	}

	public long getCounterOut() {
		return this.counterOut;
	}

	public long getCounterUrlaub() {
		return this.counterUrlaub;
	}

	public Image getImage() {
		return this.image;
	}

	public String getMessageText() {
		return this.messageText;
	}

	public CheckInSelectionValue[] getPersonenMitAusweis() {
		OperationResponse response = ServiceHelper.getSentinelService().getPersonenMitAusweis();

		CheckInSelectionValue[] pdArray = new CheckInSelectionValue[response.getPersonDetails().size()];
		for (int j = 0; j < pdArray.length; j++) {
			PersonDetails pd = response.getPersonDetails().get(j);
			CheckInSelectionValue selectionValue = new CheckInSelectionValue();
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

	public CheckInOperation getSelectedOperation() {
		return this.selectedOperation;
	}

	public OperationResponseStatus getStatus() {
		return this.status;
	}

	public void handleCheckinEvent(String barcode) {
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

		this.systemEintragLogger.addSystemEintrag(this.messageText, this.getClass());

		image = ImageCreator.createImage(response.getImageId());

		this.updateCounter(response);

		this.selectedOperation = CheckInOperation.CHECKIN;

		this.fireStateChanged();
		this.fireImageChanged();

		if (status.equals(OperationResponseStatus.SUCESS)) {
			Sound.ok();
		} else {
			Sound.warn();
		}

		OperatorEintrag personTriggerEintrag = response.getPersonTriggerEintrag();
		if (personTriggerEintrag != null) {
			this.showPersonTriggerInfoPopup(personTriggerEintrag);
		}
	}
	
	public void resetImageAndMessage(){
		image = null;
		messageText = "";
		status = null;
		
		fireStateChanged();
		fireImageChanged();
	}

	public void setOperation(CheckInOperation op) {
		this.selectedOperation = op;

		this.fireStateChanged();
	}

	private void showPersonTriggerInfoPopup(OperatorEintrag personTriggerEintrag) {
		Object[] options = { "Als erledigt markieren", "Schliessen" };

		OperatorInfoDialogPanel operatorInfoDialogPanel = new OperatorInfoDialogPanel(personTriggerEintrag);
		int answer = JOptionPane.showOptionDialog(this.parent, operatorInfoDialogPanel, "Journaleintrag anzeigen",
				JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE, null, options, options[1]);

		if (answer != 0) {
			return;
		}

		if (!ServiceHelper.getJournalService().setPersonTriggerToDone(personTriggerEintrag)) {
			this.systemEintragLogger.addSystemEintrag("Konnte Eintrag nicht als erledigt markieren", this.getClass());
		}
	}

	public void updateCounter(OperationResponse response) {
		this.counterIn = response.getCounterIn();
		this.counterOut = response.getCounterOut();
		this.counterUrlaub = response.getCounterUrlaub();
		this.counterAngemeldet = response.getCounterAngemeldet();
	}
}
