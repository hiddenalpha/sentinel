package ch.infbr5.sentinel.server.model;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Signature;

import org.junit.Ignore;

import com.thoughtworks.xstream.core.util.Base64Encoder;
@Ignore
public class SecTest {

	public static void main(String[] args) {

		String plaintext = "This is the message being signed";

		/* Generate a DSA signature */

		try {

			// Generate new key
			// KeyPair keyPair =
			// KeyPairGenerator.getInstance("RSA").generateKeyPair();
			// PrivateKey privateKey = keyPair.getPrivate();
			//
			// String privateKeyBase64 = new
			// Base64Encoder().encode(keyPair.getPrivate().getEncoded());
			// String publicKeyBase64 = new
			// Base64Encoder().encode(keyPair.getPublic().getEncoded());
			//
			// /* Save the public key in a file */
			//
			// FileOutputStream keyfos = new FileOutputStream("private.key");
			// keyfos.write(privateKeyBase64.getBytes());
			// keyfos.close();
			//
			// keyfos = new FileOutputStream("public.key");
			// keyfos.write(publicKeyBase64.getBytes());
			// keyfos.close();

			/* Generate a DSA signature */

			
				try {

					/* Generate a key pair */

					KeyPairGenerator keyGen = KeyPairGenerator.getInstance("DSA", "SUN");
					SecureRandom random = SecureRandom.getInstance("SHA1PRNG", "SUN");

					keyGen.initialize(512, random);

					KeyPair pair = keyGen.generateKeyPair();
					PrivateKey priv = pair.getPrivate();
					PublicKey pub = pair.getPublic();

					/*
					 * Create a Signature object and initialize it with the
					 * private key
					 */

					Signature dsa = Signature.getInstance("SHA1withDSA", "SUN");
					Signature dsa2 = Signature.getInstance("SHA1withDSA", "SUN");
					
					dsa.initSign(priv);
					dsa2.initVerify(pub);

					/*
					 * Now that all the data to be signed has been read in,
					 * generate a signature for it
					 */
					dsa.update(plaintext.getBytes(), 0, plaintext.length());
					byte[] realSig = dsa.sign();
					String result =new Base64Encoder().encode(realSig);
					System.out.println(result);
					
					
					System.out.println(result
							);
					
					MessageDigest sha1b = MessageDigest.getInstance("SHA1");
					byte[] digestb = sha1b.digest("Das ist ein Test bal bal".getBytes());
					String resultb = new Base64Encoder().encode(digestb);
					System.out.println(resultb			);
					
					
					
					
					if (resultb.equals(result)){
						System.out.println("OK");
					} else {
						System.out.println("FALSE");
					}
					

				} catch (Exception e) {
					System.err.println("Caught exception " + e.toString());
				}

//			FileInputStream keyfis = new FileInputStream("private.key");
//			byte[] encKey = new byte[keyfis.available()];
//			keyfis.read(encKey);
//
//			// Compute signature
//			Signature instance = Signature.getInstance("SHA1withRSA");
//			instance.initSign(privateKey);
//			instance.update((plaintext).getBytes());
//			byte[] signature = instance.sign();
//
//			// Compute digest
//			MessageDigest sha1 = MessageDigest.getInstance("SHA1");
//			byte[] digest = sha1.digest(signature);
//
//			// Encrypt digest
//			Cipher cipher = Cipher.getInstance("RSA");
//			cipher.init(Cipher.ENCRYPT_MODE, privateKey);
//			byte[] cipherText = cipher.doFinal(digest);
//
//			// Display results
//			System.out.println("Input data: " + plaintext);
//			System.out.println("Digest: " + bytes2String(digest));
//			System.out.println("Cipher text: " + bytes2String(cipherText));
//			System.out.println("Signature: " + bytes2String(signature));

		} catch (Exception e) {
			e.printStackTrace();
		}

	};

	private static String bytes2String(byte[] bytes) {
		StringBuilder string = new StringBuilder();
		for (byte b : bytes) {
			String hexString = Integer.toHexString(0x00FF & b);
			string.append(hexString.length() == 1 ? "0" + hexString : hexString);
		}
		return string.toString();
	}

}
