package ch.infbr5.sentinel.server.gui;

import java.util.ArrayList;

import javax.swing.JFrame;

import net.miginfocom.swing.MigLayout;

import org.apache.log4j.spi.LoggingEvent;

import ch.infbr5.sentinel.common.gui.table.FilterTablePanel;

public class ApplicationFrame {

	public static ApplicationFrame app;

	private JFrame frame;

	private LoggerModel loggerModel;

	private FilterTablePanel filterTablePanel;

	public ApplicationFrame() {
		frame = new JFrame("Sentinel-Server");
		frame.setSize(600, 400);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		loggerModel = new LoggerModel(new ArrayList<LoggingEvent>());
		filterTablePanel = new FilterTablePanel(new LoggerTable(loggerModel), null);

		frame.setLayout(new MigLayout());
		frame.add(filterTablePanel, "push, grow");

		app = this;
	}

	public void show() {
		frame.setVisible(true);
	}

	public void addText(LoggingEvent event) {
		loggerModel.add(event);
	}

}
