package ch.infbr5.sentinel.server.importer.personen.state;

import java.io.File;
import java.io.IOException;

import com.google.common.base.Charsets;
import com.google.common.io.Files;
import com.thoughtworks.xstream.XStream;

public class PersonenImporterStatePersister {

	private PersonenImporterState state;

	private String file;

	public PersonenImporterStatePersister(String file) {
		this.file = file;
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

		if (new File(file).exists()) {
			new File(file).delete();
		}

		try {
			Files.write(xml, new File(file), Charsets.UTF_8);
		} catch (IOException e) {
			e.printStackTrace();
		}
		// FileHelper.saveAsFile(file, xml.getBytes());
	}

	public void remove() {
		File f = new File(file);
		f.delete();
	}

}
