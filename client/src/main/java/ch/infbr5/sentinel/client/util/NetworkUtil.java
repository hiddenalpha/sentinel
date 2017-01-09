package ch.infbr5.sentinel.client.util;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.apache.log4j.Logger;

public class NetworkUtil {

	private static Logger log = Logger.getLogger(NetworkUtil.class);

	public static boolean isLocalAdress(String ip) {
		try {
			InetAddress[] addresses = InetAddress.getAllByName(InetAddress.getLocalHost().getHostName());
			for (InetAddress address : addresses) {
				if (address.getHostAddress().toString().equals(ip)) {
					return true;
				}
			}
		} catch (UnknownHostException e) {
			log.error(e);
		}
		return false;
	}

}
