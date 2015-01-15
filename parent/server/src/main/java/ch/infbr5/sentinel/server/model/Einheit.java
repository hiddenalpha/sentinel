package ch.infbr5.sentinel.server.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;

@Entity
@NamedQueries({
   @NamedQuery(name = Einheit.GET_EINHEIT_BY_ID_VALUE, query = "SELECT e FROM Einheit e WHERE e.id = :einheitId"),
   @NamedQuery(name = Einheit.GET_EINHEIT_BY_NAME, query = "SELECT e FROM Einheit e WHERE e.name = :einheitName"),
   @NamedQuery(name = Einheit.GET_EINHEITEN_VALUE, query = "SELECT e FROM Einheit e") })
public class Einheit {

   public static final String GET_EINHEIT_BY_ID_VALUE = "getEinheitById";

   public static final String GET_EINHEITEN_VALUE = "getEinheiten";

   public static final String GET_EINHEIT_BY_NAME = "getEinheitByName";

   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   private Long id;

   private String name;

   private String rgbColor_AusweisBackground;

   private String rgbColor_GsVb;

   private String rgbColor_TrpK;

   private String rgbColor_Einh;

   private String text_GsVb;

   private String text_TrpK;

   private String text_Einh;

   public Long getId() {
      return this.id;
   }

   public String getName() {
      return this.name;
   }

   public void setId(final Long id) {
      this.id = id;
   }

   public void setName(final String name) {
      this.name = name;
   }

   public String getRgbColor_AusweisBackground() {
      return rgbColor_AusweisBackground;
   }

   public void setRgbColor_AusweisBackground(final String rgb) {
      this.rgbColor_AusweisBackground = rgb;
   }

   public String getRgbColor_GsVb() {
      return rgbColor_GsVb;
   }

   public void setRgbColor_GsVb(final String rgbColor_GsVb) {
      this.rgbColor_GsVb = rgbColor_GsVb;
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
