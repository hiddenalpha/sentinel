package ch.infbr5.sentinel.server.print.util;

import java.awt.Color;
import java.text.SimpleDateFormat;

import com.itextpdf.text.pdf.Barcode39;

import ch.infbr5.sentinel.server.model.Ausweis;

public class BarCodeHelper {

	public static java.awt.Image createBarCode(Ausweis ausweis) {
		Barcode39 code39 = new Barcode39();
		code39.setCode(ausweis.getBarcode());
		return code39.createAwtImage(Color.black, Color.white);
	}

	public static String createBarCodeText(Ausweis ausweis) {
		String barcodeText = "";
		if (ausweis.getGueltigVon() != null) {
			SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
			barcodeText = " ** " + dateFormat.format(ausweis.getGueltigVon()) + " ** ";
		}
		return barcodeText;
	}

}
