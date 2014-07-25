package ch.infbr5.sentinel.client.gui.components.ipcam;

import java.awt.Image;
import java.io.IOException;
import java.net.URL;
import java.util.Timer;
import java.util.TimerTask;

import javax.imageio.ImageIO;

import ch.infbr5.sentinel.client.gui.components.checkin.AusweisInfoPanel;

public class IpCamaraReceiver {

	private class MyTimerTask extends TimerTask {
		@Override
		public void run() {
			if (IpCamaraReceiver.this.camUrls != null) {
				for (int i = 0; i < IpCamaraReceiver.this.camUrls.length; i++) {
					try {
						Image image = ImageIO.read(IpCamaraReceiver.this.camUrls[i]);
						if (image != null) {
							IpCamaraReceiver.this.images[i] = image;
						} else {
							// case wenn url was not an image
							IpCamaraReceiver.this.images[i] = IpCamaraReceiver.this.defaultImage;
						}
					} catch (IOException e) {
						IpCamaraReceiver.this.images[i] = IpCamaraReceiver.this.defaultImage;
					}
				}
			}

		}
	}
	public static IpCamaraReceiver getInstance() {
		if (IpCamaraReceiver.instance == null) {
			IpCamaraReceiver.instance = new IpCamaraReceiver();
		}

		return IpCamaraReceiver.instance;
	}
	private Timer timer;
	private URL[] camUrls;
	private Image[] images;

	private Image defaultImage;

	private static IpCamaraReceiver instance;

	private IpCamaraReceiver() {
		URL imageURL = AusweisInfoPanel.class.getResource("/images/ipCamNoConnection.jpg");
		try {
			this.defaultImage = ImageIO.read(imageURL);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public Image getImage(int nr) {
		return this.images[nr];
	}

	public void setCamUrls(URL[] camUrls) {
		this.camUrls = camUrls;
		this.images = new Image[camUrls.length];
		for (int i = 0; i < this.images.length; i++) {
			this.images[i] = this.defaultImage;
		}
	}

	public void setRefreshRate(int refreshRate) {
		if (this.timer != null) {
			this.timer.cancel();
		}
		this.timer = new Timer();
		this.timer.schedule(new MyTimerTask(), refreshRate * 100, refreshRate * 100);
	}

}
