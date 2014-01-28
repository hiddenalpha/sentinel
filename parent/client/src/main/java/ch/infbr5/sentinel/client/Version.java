package ch.infbr5.sentinel.client;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

public class Version {

	private static Version v = null;
	private String version = "DevVersion";
	private String buildTimestamp = "DevTimestamp";

	public static Version get() {
		if (v != null) {
			return v;
		} else {
			v = new Version();
		}
		return v;
	}

	private Version() {
		try {
			URL manifestURL = Version.class
					.getResource("/META-INF/MANIFEST.MF");
			
			Class<Version> clazz = Version.class;
			String className = clazz.getSimpleName() + ".class";
			String classPath = clazz.getResource(className).toString();
			if (!classPath.startsWith("jar")) {
			  // Class not from JAR
			  return;
			}
			String manifestPath = classPath.substring(0, classPath.lastIndexOf("!") + 1) + 
			    "/META-INF/MANIFEST.MF";	

			if (manifestURL != null) {
				InputStream is = new URL(manifestPath).openStream();
				Manifest mf = new Manifest(is);
				Attributes a = mf.getMainAttributes();
				version = a.getValue("Implementation-Version");
				buildTimestamp = a.getValue("BuildTimestamp");
				is.close();
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public String getVersion() {
		return version;
	}

	public String getBuildTimestamp() {
		return buildTimestamp;
	}

}
