package ch.infbr5.sentinel.server.ws;

public class EinheitDetails {

   private String name;
   private Long id;

   private String rgbColor_BackgroundAusweis;

   private String rgbColor_GsVb;
   private String rgbColor_TrpK;
   private String rgbColor_Einh;

   private String text_GsVb;
   private String text_TrpK;
   private String text_Einh;

   public String getName() {
      return this.name;
   }

   public void setName(final String name) {
      this.name = name;
   }

   public void setId(final Long id) {
      this.id = id;
   }

   public Long getId() {
      return id;
   }

   public String getRgbColor_GsVb() {
      return rgbColor_GsVb;
   }

   public void setRgbColor_GsVb(final String rgbColor_GsVb) {
      this.rgbColor_GsVb = rgbColor_GsVb;
   }

   public String getRgbColor_BackgroundAusweis() {
      return rgbColor_BackgroundAusweis;
   }

   public void setRgbColor_BackgroundAusweis(final String rgbColor_BackgroundAusweis) {
      this.rgbColor_BackgroundAusweis = rgbColor_BackgroundAusweis;
   }

   public String getRgbColor_TrpK() {
      return rgbColor_TrpK;
   }

   public void setRgbColor_TrpK(final String rgbColor_TrpK) {
      this.rgbColor_TrpK = rgbColor_TrpK;
   }

   public String getRgbColor_Einh() {
      return rgbColor_Einh;
   }

   public void setRgbColor_Einh(final String rgbColor_Einh) {
      this.rgbColor_Einh = rgbColor_Einh;
   }

   public String getText_GsVb() {
      return text_GsVb;
   }

   public void setText_GsVb(final String text_GsVb) {
      this.text_GsVb = text_GsVb;
   }

   public String getText_TrpK() {
      return text_TrpK;
   }

   public void setText_TrpK(final String text_TrpK) {
      this.text_TrpK = text_TrpK;
   }

   public String getText_Einh() {
      return text_Einh;
   }

   public void setText_Einh(final String text_Einh) {
      this.text_Einh = text_Einh;
   }

}
