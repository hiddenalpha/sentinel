package ch.infbr5.sentinel.server.model;

public enum Grad {

	REKR("Rekr"), SDT("Sdt"), GFR("Gfr"), OBGFR("Obgfr"), KPL("Kpl"), WM("Wm"), OBWM(
			"Obwm"), FW("Fw"), FOUR("Four"), HPTFW("Hptfw"), ADJ("Adj Uof"), STABSADJ(
			"Stabsadj"), HPTADJ("Hptadj"), CHEFADJ("Chefadj"), LT("Lt"), OBLT(
			"Oblt"), FACHOF("Fachof"), HPTM("Hptm"), HPTM_IGST("Hptm i Gst"), MAJ(
			"Maj"), MAJ_IGST("Maj i Gst"), OBERSTLT("Oberstlt"), OBERSTLT_IGST(
			"Oberstlt i Gst"), OBERST("Oberst"), OBERST_IGST("Oberst i Gst"), BR(
			"Br"), DIV("Div"), KKDT("KKdt"), REGIERUNGSRAT("Regierungsrat"), BUNDESRAT(
			"Bundesrat"), OHNE("-");

	public static Grad getGrad(String gradtext) {

		if (gradtext != null) {
			for (int i = 0; i < Grad.values().length; i++) {
				Grad g = Grad.values()[i];

				// I GST muessen zu 100% uebereinstimmen
				if (gradtext.endsWith("i Gst")) {
					if (g.toString().equalsIgnoreCase(gradtext)) {
						return g;
					}
					// sonst geht auch HPTM Asg
				} else {
					if (gradtext.toLowerCase().startsWith(
							g.toString().toLowerCase())) {
						return g;
					}
				}
			}
		}

		return null;
	}

	private String gradtext;

	private Grad(String gradtext) {
		this.gradtext = gradtext;
	}

	@Override
	public String toString() {
		return this.gradtext;
	}
}
