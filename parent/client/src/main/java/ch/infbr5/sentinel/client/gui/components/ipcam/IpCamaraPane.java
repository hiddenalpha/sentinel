package ch.infbr5.sentinel.client.gui.components.ipcam;

import java.awt.AWTEvent;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.AWTEventListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;
import java.util.Date;

import javax.swing.JPanel;
import javax.swing.Timer;

import net.miginfocom.swing.MigLayout;

public class IpCamaraPane extends JPanel implements ActionListener, AWTEventListener {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	private Timer timer;

	private Long lastEventTime;
	private int delay;
	private int refreshRate;
	private boolean isGlassPane;

	private URL[] camUrls;
	private int noOfCamsWidth;
	private int noOfCamsHeight;
	private IpCamaraReceiver receiver;

	public IpCamaraPane(int delay, int refreshRate, URL[] camUrls) {
		this.setLayout(new MigLayout());

		this.delay = delay;
		this.refreshRate = refreshRate;
		this.camUrls = camUrls;
		this.isGlassPane = (delay != 0);

		this.initComponents();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (this.isVisible()) {
			this.repaint();
		} else {
			if (this.isGlassPane && ((new Date().getTime() - this.lastEventTime) > 1000 * this.delay)) {
				this.setVisible(true);
			}
		}
	}

	@Override
	public void eventDispatched(AWTEvent event) {
		if (this.isVisible()) {
			this.setVisible(false);
		}

		this.lastEventTime = new Date().getTime();
	}

	private void initComponents() {
		this.receiver = IpCamaraReceiver.getInstance();
		this.receiver.setCamUrls(this.camUrls);
		this.receiver.setRefreshRate(this.refreshRate);

		this.timer = new Timer(this.refreshRate * 100, this);
		this.timer.start();

		if (this.isGlassPane) {
			this.lastEventTime = new Date().getTime();
			Toolkit.getDefaultToolkit().addAWTEventListener(this, AWTEvent.MOUSE_EVENT_MASK + AWTEvent.KEY_EVENT_MASK);
		}

		/*
		 * Anzahl Kamera Bilder pro Dimension berechnen.
		 */
		this.noOfCamsWidth = (int) Math.sqrt(this.camUrls.length);
		this.noOfCamsHeight = this.noOfCamsWidth;

		if ((this.noOfCamsWidth * this.noOfCamsHeight) < this.camUrls.length) {
			this.noOfCamsWidth += 1;
		}

		if ((this.noOfCamsWidth * this.noOfCamsHeight) < this.camUrls.length) {
			this.noOfCamsHeight += 1;
		}
	}

	@Override
	protected void paintComponent(Graphics g) {
		g.setColor(Color.BLACK);
		g.fillRect(0, 0, this.getWidth(), this.getHeight());

		int imageHeight;
		int imageWidth;

		int maxImageHeight = this.getHeight() / this.noOfCamsHeight;
		int maxImageWidth = this.getWidth() / this.noOfCamsWidth;

		for (int i = 0; i < this.noOfCamsWidth; i++) {
			for (int j = 0; j < this.noOfCamsHeight; j++) {
				if (this.camUrls.length > (i + j * this.noOfCamsWidth)) {
					Image image = this.receiver.getImage(i + j * this.noOfCamsWidth);
						float scaleHeight = (float) maxImageHeight / (float) image.getHeight(null);
						float scaleWidth = (float) maxImageWidth / (float) image.getWidth(null);

						if (scaleHeight < scaleWidth) {
							imageHeight = maxImageHeight;
							imageWidth = image.getWidth(null) * maxImageHeight / image.getHeight(null);
						} else {
							imageHeight = image.getHeight(null) * maxImageWidth / image.getWidth(null);
							imageWidth = maxImageWidth;
						}

						g.drawImage(image, i * maxImageWidth, j * maxImageHeight, imageWidth, imageHeight, null);
				}
			}
		}
	}
}
