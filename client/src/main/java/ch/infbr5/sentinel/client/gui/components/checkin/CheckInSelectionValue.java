package ch.infbr5.sentinel.client.gui.components.checkin;

public class CheckInSelectionValue {

	private String barcode;
	private String displayName;

	public String getBarcode() {
		return this.barcode;
	}

	public String getDisplayName() {
		return this.displayName;
	}

	public void setBarcode(String barcode) {
		this.barcode = barcode;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	@Override
	public String toString() {
		return this.displayName;
	}
}
