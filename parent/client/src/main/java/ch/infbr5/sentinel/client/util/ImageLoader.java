package ch.infbr5.sentinel.client.util;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.apache.log4j.Logger;

import ch.infbr5.sentinel.client.config.ConfigurationLocalHelper;

public class ImageLoader {

	private static Logger log = Logger.getLogger(ImageLoader.class);

	/**
	 * L�dt das Bild. Diese Methode ist optimiert. Falls der Client und Server
	 * auf einem System arbeiten, wird das Bild direkt aus dem Server Ordner
	 * geladen und nicht �ber den Network Stack geschickt.
	 *
	 * @param imageId ID des Bildes.
	 * @return BufferedImage Das Bild der Person oder null.
	 */
	public static BufferedImage loadImage(String imageId) {
		if (isEmptyString(imageId)) {
			return null;
		}

		ConfigurationLocalHelper config = ConfigurationLocalHelper.getConfig();

		try {
			if (config.isLocalMode() && !isEmptyString(config.getLocalImagePath())) {
				String path = config.getLocalImagePath() + "\\" + imageId + ".jpg";
				log.debug("Lade Bild [" + imageId + "] direkt lokal von " + path);
				return ImageIO.read(new File(path));
			} else {
				byte[] data = ServiceHelper.getSentinelService().getPersonImage(imageId);
				log.debug("Lade Bild [" + imageId + "] von Server");
				return ImageIO.read(new ByteArrayInputStream(data));
			}
		} catch (IOException e) {
			log.error(e);
			return null;
		}
	}

	private static boolean isEmptyString(String s) {
		return s == null || s.isEmpty();
	}
}
