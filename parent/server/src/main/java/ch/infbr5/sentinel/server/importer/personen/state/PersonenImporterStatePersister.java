package ch.infbr5.sentinel.server.importer.personen.state;

import java.io.File;

import ch.infbr5.sentinel.server.utils.FileHelper;

import com.thoughtworks.xstream.XStream;

public class PersonenImporterStatePersister {

	private PersonenImporterState state;
	
	private String file;
	
	public PersonenImporterStatePersister(String sessionKey) {
		file = "import/" + sessionKey + "-state" + ".xml";
		
		if ((new File(file)).exists()) {
			XStream xStream = new XStream();
			state = (PersonenImporterState) xStream.fromXML(new File(file));
		} else {
			state = new PersonenImporterState();
		}
	}
	
	public PersonenImporterState getState() {
		return state;
	}
	
	public void save() {
		XStream xStream = new XStream();
		String xml = xStream.toXML(state);
		FileHelper.saveAsFile(file, xml.getBytes());
	}
	
}
