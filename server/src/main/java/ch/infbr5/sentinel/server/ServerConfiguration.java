package ch.infbr5.sentinel.server;

import java.io.File;
import java.io.IOException;

import org.apache.log4j.Logger;

import ch.infbr5.sentinel.server.utils.FileHelper;

import com.google.common.io.ByteStreams;
import com.google.common.io.Files;

public class ServerConfiguration {

   private static final Logger LOGGER = Logger.getLogger(ServerConfiguration.class);
   
	// Default Values
	public static final String IP_ADDRESS = "localhost";

	public static final String PORT = "8080";

	public static final boolean DEBUG_MODE = false;

	public static final boolean HEADLESS = false;

	public static final String AUSWEISVORLAGE_BACKGROUND_COLOR = "#f5ed26";

	public static final String AUSWEISVORLAGE_COLOR_AREA_BACKSIDE = "#f5ed26";

	public static final boolean AUSWEISVORLAGE_SHOW_AREA_BACKSIDE = false;

	public static final boolean AUSWEISVORLAGE_SHOW_QR_CODE = true;

	public static final boolean AUSWEISVORLAGE_USE_USER_WASSERZEICHEN = false;

	public static final boolean AUSWEISVORLAGE_USE_USER_LOGO = false;

	public static final String DEFAULT_ADMIN_PASSWORD = "admin";

	public static final String DEFAULT_SUPERUSER_PASSWORD = "super";

	public static final String DEFAULT_IDENTITY_CARD_PASSWORD = "1nf8r5!";

	public static final String DEFAULT_CHECKPOINT_NAME = "Haupteingang";

	public static final String DEFAULT_ZONEN_NAME = "Kommandoposten";

	public static final String RESOURCE_PATH_AUSWEISVORLAGE_WASSERZEICHEN = "/images/emblem.png";

	public static byte[] getDefaultWasserzeichen() {
		try {
			return ByteStreams.toByteArray(ServerConfiguration.class.getResourceAsStream(RESOURCE_PATH_AUSWEISVORLAGE_WASSERZEICHEN));
		} catch (IOException e) {
			throw new IllegalStateException("Default Wasserzeichen nicht korrekt abgelegt", e);
		}
	}

	public static byte[] getUserWasserzeichen() {
		try {
			if (doesUserWasserzeichenExists()) {
				return Files.toByteArray(getFileUserWasserzeichen());
			}
		} catch (IOException e) {
			throw new IllegalStateException("Fehler beim Lesen des benutzerdefinierten Wasserzeichen", e);
		}
		return null;
	}

	public static byte[] getUserLogo() {
		try {
			if (doesUserLogoExists()) {
				return Files.toByteArray(getFileUserLogo());
			}
		} catch (IOException e) {
			throw new IllegalStateException(e);
		}
		return null;
	}

	private static boolean doesUserWasserzeichenExists() {
		return getFileUserWasserzeichen().exists();
	}

	private static boolean doesUserLogoExists() {
		return getFileUserLogo().exists();
	}

	public static void saveUserLogo(byte[] data) {
		if (data != null && data.length > 0) {
			try {
				Files.write(data, getFileUserLogo());
			} catch (IOException e) {
			   LOGGER.error(e);
			}
		}
	}

	public static void saveUserWasserzeichen(byte[] data) {
		if (data != null && data.length > 0) {
			try {
				Files.write(data, getFileUserWasserzeichen());
			} catch (IOException e) {
			   LOGGER.error(e);
			}
		}
	}

	private static File getFileUserWasserzeichen() {
		return new File(FileHelper.FILE_AUSWEISVORLAGE_WASSERZEICHEN);
	}

	private static File getFileUserLogo() {
		return new File(FileHelper.FILE_AUSWEISVORLAGE_LOGO);
	}

}
