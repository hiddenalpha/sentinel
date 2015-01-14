package ch.infbr5.sentinel.server.model;

public enum Grad {

   // Mannschaft
   REKR("Rekr", "Recr", "Recl"),
   SDT("Sdt", "Sdt", "Sdt"),
   GFR("Gfr", "App", "App"),
   OBGFR("Obgfr", "App chef", "App capo"),

   // Unteroffiziere
   KPL("Kpl", "Cpl", "Cpl"),
   WM("Wm", "Sgt", "Sgt"),
   OBWM("Obwm", "Sgt chef", "Sgt capo"),

   // Höhere Unteroffiziere
   FW("Fw", "Sgtm", "Sgtm"),
   HPTFW("Hptfw", "Sgtm chef", "Sgtm capo"),
   FOUR("Four", "Four", "Fur"),
   ADJ("Adj Uof", "Adj sof", "Aiut suff"),
   STABSADJ("Stabsadj", "Adj EM", "Aiut SM"),
   HPTADJ("Hptadj", "Adj maj", "Aiut magg"),
   CHEFADJ("Chefadj", "Adj chef", "Aiut capo"),

   // Offiziere
   LT("Lt", "Lt", "Ten"),
   OBLT("Oblt", "Plt", "I ten"),
   HPTM("Hptm", "Cap", "Cap"),
   MAJ("Maj", "Maj", "Magg"),
   OBERSTLT("Oberstlt", "Lt col", "Ten col"),
   OBERST("Oberst", "Col", "Col"),

   // Offiziere im Generalstab
   HPTM_IGST("Hptm i Gst", "Cap EMG", "Cap SMG"),
   MAJ_IGST("Maj i Gst", "Maj EMG", "Maj SMG"),
   OBERSTLT_IGST("Oberstlt i Gst", "Lt col EMG", "Lt col SMG"),
   OBERST_IGST("Oberst i Gst", "Lt col EMG", "Ten col SMG"),

   // Höhere Stabsoffiziere
   BR("Br", "Br", "Br"),
   DIV("Div", "Div", "Div"),
   KKDT("KKdt", "Cdt C", "Cdt C"),

   // Oberbefehlshaber der Armee
   GENERAL("General", "Général", "Generale"),

   // Andere
   FACHOF("Fachof", "Of sup Adj", "Of sup Aiut"),
   REGIERUNGSRAT("Regierungsrat", "conseiller", "assessore"),
   BUNDESRAT("Bundesrat", "conseil fédéral", "consiglio federale"),
   OHNE("-", "-", "-");

   /**
    * Gibt den dazugehörigen Grad zurück. Erlaubt sind De, Fr und It inputs.
    * Case insensitive!
    *
    * @param bezeichnung
    *           Bezeichnung in De, Fr oder It.
    * @return Passender Grad falls kein Match gefunden den Grad.OHNE.
    */
   public static Grad getGrad(String bezeichnung) {
      if (bezeichnung == null || bezeichnung.isEmpty()) {
         return OHNE;
      }

      // Prüfen auf einen exact match.
      for (final Grad g : values()) {
         if (g.bezeichnungDe.equalsIgnoreCase(bezeichnung)) {
            return g;
         } else if (g.bezeichnungFr.equalsIgnoreCase(bezeichnung)) {
            return g;
         } else if (g.bezeichnungIt.equalsIgnoreCase(bezeichnung)) {
            return g;
         }
      }

      // Nun wurde noch keiner gefunden, prüfen
      bezeichnung = bezeichnung.toLowerCase();
      for (final Grad g : values()) {
         if (bezeichnung.startsWith(g.bezeichnungDe.toLowerCase())) {
            return g;
         } else if (bezeichnung.startsWith(g.bezeichnungFr.toLowerCase())) {
            return g;
         } else if (bezeichnung.startsWith(g.bezeichnungIt.toLowerCase())) {
            return g;
         }
      }

      return OHNE;
   }

   private String bezeichnungDe;

   private String bezeichnungFr;

   private String bezeichnungIt;

   private Grad(final String bezeichnungDe, final String bezeichnungFr, final String bezeichnungIt) {
      this.bezeichnungDe = bezeichnungDe;
      this.bezeichnungFr = bezeichnungFr;
      this.bezeichnungIt = bezeichnungIt;
   }

   public String getGradText() {
      return bezeichnungDe;
   }

   @Override
   public String toString() {
      return bezeichnungDe;
   }
}
