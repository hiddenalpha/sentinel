package ch.infbr5.sentinel.server.db;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

import org.apache.log4j.Logger;

public class ImageStore {

	private static Logger log = Logger.getLogger(ImageStore.class);

	private static final String FOLDER_NAME = "images";

	public static Image getImage(String imageId) {
		Image image = null;
		try {
			image = ImageIO.read(new File(ImageStore.createFilename(imageId)));
		} catch (IOException e) {
			log.error(e);
		}
		return image;
	}

	public static ImageIcon getImageIcon(String ahvNr, int width, int height) {
		ImageIcon imageIcon = null;
		if (ImageStore.hasImage(ahvNr)) {
			imageIcon = new ImageIcon(ImageStore.createFilename(ahvNr));
		}
		return imageIcon;
	}

	public static boolean hasImage(String ahvNr) {
		File jpegFile = new File(ImageStore.createFilename(ahvNr));
		return jpegFile.exists();
	}

	public static byte[] loadJpegImage(String ahvNr) {
		// create file object
		File file = new File(ImageStore.createFilename(ahvNr));

		if (!file.exists()) {
			return null;
		}

		try {
			// create FileInputStream object
			FileInputStream fin = new FileInputStream(file);

			byte fileContent[] = new byte[(int) file.length()];

			fin.read(fileContent);
			fin.close();

			return fileContent;

		} catch (FileNotFoundException e) {
			log.error("File not found" + e);
		} catch (IOException ioe) {
			log.error("Exception while reading the file " + ioe);
		}

		return null;
	}


	public static BufferedImage byteArrayToBufferedImage(byte[] binaryData) {
		if (binaryData == null || binaryData.length == 0) {
			return null;
		}

		BufferedImage image = null;
		try {
			ByteArrayInputStream bais = new ByteArrayInputStream(binaryData);
			image = ImageIO.read(bais);
		} catch (IOException e) {
			log.error(e);
		}
		return image;
	}

	public static boolean saveJpegImage(String ahvNr, byte[] data) {
		try {
			if ((data != null) && (data.length > 0)) {
				String filename = createFilename(ahvNr);

				createParentDirectoryIfRequired(filename);

				FileOutputStream fos = new FileOutputStream(filename);
				fos.write(data);
				fos.close();
			}

			return true;

		} catch (Exception e) {
			log.error(e);

			return false;
		}
	}

	private static void createParentDirectoryIfRequired(String filename) {
		File fileHelper = new File(filename);
		String parentDirectory = fileHelper.getParent();
		File directoryHelper = new File(parentDirectory);
		boolean directoryExists = directoryHelper.exists();
		if (!directoryExists) {
			directoryHelper.mkdirs();
		}
	}

	private static String createFilename(String ahvNr) {
		String folder = "."+File.separator+FOLDER_NAME+File.separator;
		return folder + ahvNr + ".jpg";
	}

	public static String getLocalImagePath() {
		return new File(FOLDER_NAME).getAbsolutePath();
	}


}
