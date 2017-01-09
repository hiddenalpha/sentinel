package ch.infbr5.sentinel.server.ws;

import java.util.List;

import com.google.common.collect.Lists;

public class IPCams {

	private List<String> cams = Lists.newArrayList();

	public List<String> getCams() {
		return cams;
	}

	public void setCams(List<String> cams) {
		this.cams = cams;
	}

}
