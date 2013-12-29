package ch.infbr5.sentinel.testutils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import org.apache.commons.io.FileUtils;
import org.fest.swing.edt.GuiActionRunner;
import org.fest.swing.edt.GuiQuery;
import org.fest.swing.fixture.FrameFixture;

import ch.infbr5.sentinel.client.gui.ApplicationFrame;
import ch.infbr5.sentinel.server.ServerControl;

public class Helper {

	private static ServerControl server;

	public static void setupRuntime() {

		writeClientProperties();

		server = new ServerControl(false, true);
		server.start("127.0.0.1");
	}

	public static void cleanupRuntime() {
		server.stop();
		cleanupRuntimeDir();
	}

	public static void cleanupRuntimeDir() {
		new File("sentinel.properties").delete();
		new File("derby.log").delete();
	}
	
	public static FrameFixture getWindow(){
		ApplicationFrame frame = GuiActionRunner.execute(new GuiQuery<ApplicationFrame>() {
			protected ApplicationFrame executeInEDT() {
				return new ApplicationFrame();
			}
		});
		return new FrameFixture(frame);
	}

	private static void writeClientProperties() {

		Properties applicationProps = new Properties();
		applicationProps.setProperty("CheckpointId", "1");
		applicationProps.setProperty("ServerHostname", "127.0.0.1");
		applicationProps.setProperty("AdminMode", "true");
		try {
			FileOutputStream out;
			out = new FileOutputStream("sentinel.properties");
			applicationProps.store(out, "---Only for testing---");
			out.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
