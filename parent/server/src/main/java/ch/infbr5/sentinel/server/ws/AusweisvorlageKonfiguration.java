package ch.infbr5.sentinel.server.ws;

public class AusweisvorlageKonfiguration {

	// QR Code auf der Rückseite
	private boolean showQRCode;

	// Spezial Fläche auf der Rückseite
	private boolean showAreaBackside;

	// HTML Color
	private String colorAreaBackside;

	// Hinetergrund Farbe HTML
	private String colorBackground;

	private byte[] logo;

	private byte[] defaultWasserzeichen;

	private byte[] wasserzeichen;

	private boolean useUserWasserzeichen;

	private boolean useUserLogo;

	public boolean isShowQRCode() {
		return showQRCode;
	}

	public void setShowQRCode(boolean showQRCode) {
		this.showQRCode = showQRCode;
	}

	public boolean isShowAreaBackside() {
		return showAreaBackside;
	}

	public void setShowAreaBackside(boolean showAreaBackside) {
		this.showAreaBackside = showAreaBackside;
	}

	public String getColorAreaBackside() {
		return colorAreaBackside;
	}

	public void setColorAreaBackside(String colorAreaBackside) {
		this.colorAreaBackside = colorAreaBackside;
	}

	public String getColorBackground() {
		return colorBackground;
	}

	public void setColorBackground(String colorBackground) {
		this.colorBackground = colorBackground;
	}

	public byte[] getLogo() {
		return logo;
	}

	public void setLogo(byte[] logo) {
		this.logo = logo;
	}

	public boolean isUseUserWasserzeichen() {
		return useUserWasserzeichen;
	}

	public void setUseUserWasserzeichen(boolean useUserWasserzeichen) {
		this.useUserWasserzeichen = useUserWasserzeichen;
	}

	public byte[] getDefaultWasserzeichen() {
		return defaultWasserzeichen;
	}

	public void setDefaultWasserzeichen(byte[] defaultWasserzeichen) {
		this.defaultWasserzeichen = defaultWasserzeichen;
	}

	public byte[] getWasserzeichen() {
		return wasserzeichen;
	}

	public void setWasserzeichen(byte[] wasserzeichen) {
		this.wasserzeichen = wasserzeichen;
	}

	public boolean isUseUserLogo() {
		return useUserLogo;
	}

	public void setUseUserLogo(boolean useUserLogo) {
		this.useUserLogo = useUserLogo;
	}

}
