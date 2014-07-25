package ch.infbr5.sentinel.client.util;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import ch.infbr5.sentinel.client.config.ConfigurationLocalHelper;

public class ImageCreator {

	public static BufferedImage createImage(String imageId) {

		ConfigurationLocalHelper c = ConfigurationLocalHelper.getConfig();

		if (imageId != null && !imageId.isEmpty()) {
			try {
				if ((c.isLocalMode()) && (!c.getLocalImagePath().equals(""))) {

					return ImageIO.read(new File(c.getLocalImagePath() + "\\" + imageId + ".jpg"));

				} else {

					byte[] data = ServiceHelper.getSentinelService().getPersonImage(imageId);
					return ImageIO.read(new ByteArrayInputStream(data));
				}

			} catch (IOException e) {
				e.printStackTrace();

				return null;
			}
		} else {
			return null;
		}
	}
}
