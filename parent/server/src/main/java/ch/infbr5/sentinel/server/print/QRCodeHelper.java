package ch.infbr5.sentinel.server.print;

import java.awt.image.BufferedImage;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;

import ch.infbr5.sentinel.server.model.Ausweis;
import ch.infbr5.sentinel.server.model.Person;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.thoughtworks.xstream.core.util.Base64Encoder;

public class QRCodeHelper {

	public static BufferedImage createQrCodeImage(Ausweis a, String password, int width, int height) {
		String s = createString(a, password);
		return createImage(s, width, height);
	}

	private static BufferedImage createImage(String content, int width, int height) {
		QRCodeWriter writer = new QRCodeWriter();
		try {
			BitMatrix bitMatrix = writer.encode(content, BarcodeFormat.QR_CODE, width, height);
			return MatrixToImageWriter.toBufferedImage(bitMatrix);
		} catch (WriterException e) {
			e.printStackTrace();
		}
		return null;
	}

	private static String createString(Ausweis a, String password) {
		Person p = a.getPerson();

		String c = "";
		// Barcode
		if (a.getBarcode() != null) {
			c += "B" + a.getBarcode() + "\n";
		}
		// Grad
		if (p.getGrad() != null) {
			c += "G" + p.getGrad().toString() + "\n";
		}
		// Name
		if (p.getName() != null) {
			c += "N" + p.getName() + "\n";
		}
		// Vorname
		if (p.getVorname() != null) {
			c += "V" + p.getVorname() + "\n";
		}
		// Funktion
		c += "F" + p.getFunktion() + "\n";
		// Einheitsname
		if (p.getEinheit() != null) {
			c += "E" + p.getEinheit().getName() + "\n";
		}
		// Geburtsdatum
		if (p.getGeburtsdatum() != null) {
			SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
			c += "D" + dateFormat.format(p.getGeburtsdatum().getTime()) + "\n";
		}
		// AVH Nr
		c += "A" + p.getAhvNr() + "\n";
		// Signatur
		try {
			MessageDigest sha1 = MessageDigest.getInstance("SHA1");
			byte[] digest = sha1.digest(password.concat(c).getBytes());
			c += "S" + new Base64Encoder().encode(digest);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return c;
	}

}
