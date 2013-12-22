package ch.infbr5.sentinel.server.ws;

import ch.infbr5.sentinel.server.model.journal.OperatorEintrag;

public class OperationResponse {

	private String message;
	private OperationResponseStatus status;
	private String imageId;
	private PersonDetails[] personDetails;

	private Long counterIn;
	private Long counterOut;
	private Long counterUrlaub;
	private Long counterAngemeldet;

	private OperatorEintrag personTriggerEintrag;

	public Long getCounterAngemeldet() {
		return this.counterAngemeldet;
	}

	public Long getCounterIn() {
		return this.counterIn;
	}

	public Long getCounterOut() {
		return this.counterOut;
	}

	public Long getCounterUrlaub() {
		return this.counterUrlaub;
	}

	public String getImageId() {
		return this.imageId;
	}

	public String getMessage() {
		return this.message;
	}

	public PersonDetails[] getPersonDetails() {
		return this.personDetails;
	}

	public OperatorEintrag getPersonTriggerEintrag() {
		return this.personTriggerEintrag;
	}

	public OperationResponseStatus getStatus() {
		return this.status;
	}

	public void setCounterAngemeldet(Long counterAngemeldet) {
		this.counterAngemeldet = counterAngemeldet;
	}

	public void setCounterIn(Long counterIn) {
		this.counterIn = counterIn;
	}

	public void setCounterOut(Long counterOut) {
		this.counterOut = counterOut;
	}

	public void setCounterUrlaub(Long counterUrlaub) {
		this.counterUrlaub = counterUrlaub;
	}

	public void setImageId(String imageId) {
		this.imageId = imageId;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public void setPersonDetails(PersonDetails[] personDetails) {
		this.personDetails = personDetails;
	}

	public void setPersonTriggerEintrag(OperatorEintrag operatorEintrag) {
		this.personTriggerEintrag = operatorEintrag;
	}

	public void setStatus(OperationResponseStatus status) {
		this.status = status;
	}
}
