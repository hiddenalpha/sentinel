package ch.infbr5.sentinel.common.util;

import java.awt.Image;

public class ImageUtil {
	public static Image scaleImage(Image image, int maxWidth, int maxHeight) {

		if (image.getWidth(null) < maxWidth && image.getHeight(null) < maxHeight) {
			return image;
		}

	    double ratioX = (double) maxWidth / image.getWidth(null);
	    double ratioY = (double) maxHeight / image.getHeight(null);
	    double ratio = Math.min(ratioX, ratioY);

	    int newWidth = (int)(image.getWidth(null) * ratio);
	    int newHeight = (int)(image.getHeight(null) * ratio);

	    return image.getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH);
	}
}
