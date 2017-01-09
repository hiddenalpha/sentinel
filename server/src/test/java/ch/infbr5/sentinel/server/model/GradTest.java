package ch.infbr5.sentinel.server.model;

import org.junit.Assert;
import org.junit.Test;

public class GradTest {

   @Test
   public void getGrad() {
      checkGrad("General", Grad.GENERAL);
      checkGrad("sdt", Grad.SDT);
      checkGrad("general", Grad.GENERAL);
      checkGrad("plt", Grad.OBLT);
      checkGrad("ten col", Grad.OBERSTLT);
      checkGrad("Adj Uof", Grad.ADJ);
      checkGrad("-", Grad.OHNE);
      checkGrad("DDDD", Grad.OHNE);
      checkGrad("Oberstlt i Gst", Grad.OBERSTLT_IGST);
   }

   private void checkGrad(final String bezeichnung, final Grad grad) {
      final Grad g = Grad.getGrad(bezeichnung);
      Assert.assertEquals(grad, g);
   }

}
