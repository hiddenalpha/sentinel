package ch.infbr5.sentinel.server.ws.admin;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import javax.jws.HandlerChain;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;

import ch.infbr5.sentinel.server.db.EntityManagerHelper;
import ch.infbr5.sentinel.server.db.ImageStore;
import ch.infbr5.sentinel.server.db.PdfStore;
import ch.infbr5.sentinel.server.db.QueryHelper;
import ch.infbr5.sentinel.server.exporter.AusweisDatenWriter;
import ch.infbr5.sentinel.server.importer.AusweisDatenReader;
import ch.infbr5.sentinel.server.model.Checkpoint;
import ch.infbr5.sentinel.server.model.ConfigurationValue;
import ch.infbr5.sentinel.server.model.Einheit;
import ch.infbr5.sentinel.server.model.Grad;
import ch.infbr5.sentinel.server.model.ObjectFactory;
import ch.infbr5.sentinel.server.model.Person;
import ch.infbr5.sentinel.server.model.PrintJob;
import ch.infbr5.sentinel.server.model.Zone;
import ch.infbr5.sentinel.server.print.IdentityCardRenderer;
import ch.infbr5.sentinel.server.print.PdfAusweisBoxInventar;
import ch.infbr5.sentinel.server.print.PdfAusweisListe;
import ch.infbr5.sentinel.server.utils.FileHelper;
import ch.infbr5.sentinel.server.ws.CheckpointDetails;
import ch.infbr5.sentinel.server.ws.ConfigurationDetails;
import ch.infbr5.sentinel.server.ws.EinheitDetails;
import ch.infbr5.sentinel.server.ws.PersonDetails;
import ch.infbr5.sentinel.server.ws.PrintJobDetails;
import ch.infbr5.sentinel.server.ws.ZoneDetails;

@WebService(name = "ConfigurationQueryService", targetNamespace = "http://ws.sentinel.infbr5.ch/")
@HandlerChain(file = "/META-INF/ws-handler-chain.xml")
@SOAPBinding(style = SOAPBinding.Style.RPC)
public class ConfigurationQueryService {

	@WebMethod
	public ConfigurationResponse getEinheiten() {
		List<Einheit> einheiten = QueryHelper.getEinheiten();

		EinheitDetails[] einheitDetails = new EinheitDetails[einheiten.size()];
		for (int i = 0; i < einheiten.size(); i++) {
			Einheit einheit = einheiten.get(i);

			EinheitDetails einheitDetail = new EinheitDetails();

			einheitDetail.setId(einheit.getId());
			einheitDetail.setName(einheit.getName());
			einheitDetail.setRgbColor_GsVb(einheit.getRgbColor_GsVb());
			einheitDetail.setRgbColor_TrpK(einheit.getRgbColor_TrpK());
			einheitDetail.setRgbColor_Einh(einheit.getRgbColor_Einh());
			einheitDetail.setText_GsVb(einheit.getText_GsVb());
			einheitDetail.setText_TrpK(einheit.getText_TrpK());
			einheitDetail.setText_Einh(einheit.getText_Einh());

			einheitDetails[i] = einheitDetail;
		}

		ConfigurationResponse response = new ConfigurationResponse();
		response.setEinheitDetails(einheitDetails);

		return response;
	}

	@WebMethod
	public void updateEinheit(
			@WebParam(name = "EinheitDetails") EinheitDetails details) {
		Einheit einheit = null;
		if ((details.getId() != null) && (details.getId() > 0)) {
			einheit = QueryHelper.getEinheitById(details.getId());
		}
		if (einheit == null) {
			einheit = ObjectFactory.createEinheit("");
		}

		einheit.setName(details.getName());
		einheit.setId(details.getId());
		einheit.setRgbColor_GsVb(details.getRgbColor_GsVb());
		einheit.setRgbColor_TrpK(details.getRgbColor_TrpK());
		einheit.setRgbColor_Einh(details.getRgbColor_Einh());
		einheit.setText_GsVb(details.getText_GsVb());
		einheit.setText_TrpK(details.getText_TrpK());
		einheit.setText_Einh(details.getText_Einh());

		EntityManagerHelper.getEntityManager().persist(einheit);
	}

	@WebMethod
	public boolean removeEinheit(@WebParam(name = "einheitId") Long einheitId) {
		if ((einheitId != null) && (einheitId > 0)) {
			Einheit einheit = QueryHelper.getEinheitById(einheitId);
			if (einheit != null) {
				EntityManagerHelper.getEntityManager().remove(einheit);
				return true;
			}
		}

		return false;
	}

	@WebMethod
	public ConfigurationResponse getZonen() {
		List<Zone> zonen = QueryHelper.getZonen();

		ZoneDetails[] zoneDetails = new ZoneDetails[zonen.size()];
		for (int i = 0; i < zonen.size(); i++) {
			Zone zone = zonen.get(i);

			ZoneDetails zoneDetail = new ZoneDetails();

			zoneDetail.setId(zone.getId());
			zoneDetail.setName(zone.getName());
			zoneDetail.setUndOpRegeln(zone.isUndOpRegeln());

			zoneDetails[i] = zoneDetail;
		}

		ConfigurationResponse response = new ConfigurationResponse();
		response.setZoneDetails(zoneDetails);

		return response;
	}

	@WebMethod
	public ConfigurationResponse getPersonen() {
		List<Person> personen = QueryHelper.getPersonen();

		PersonDetails[] personenDetails = new PersonDetails[personen.size()];
		for (int i = 0; i < personen.size(); i++) {
			Person person = personen.get(i);

			PersonDetails personDetail = new PersonDetails();

			personDetail.setId(person.getId());
			if (person.getValidAusweis() != null) {
				personDetail.setBarcode(person.getValidAusweis().getBarcode());
			}
			personDetail.setName(person.getName());
			personDetail.setVorname(person.getVorname());
			personDetail.setAhvNr(person.getAhvNr());
			if (ImageStore.hasImage(person.getAhvNr())) {
				personDetail.setImageId(person.getAhvNr());
			}

			Einheit einheit = person.getEinheit();
			personDetail.setEinheitId(einheit != null ? einheit.getId() : -1);
			personDetail.setEinheitText(einheit != null ? einheit.getName()
					: "");
			personDetail.setFunktion(person.getFunktion());
			personDetail.setGeburtsdatum(person.getGeburtsdatum());
			Grad grad = person.getGrad();
			personDetail.setGrad(grad != null ? grad.toString() : "");

			personenDetails[i] = personDetail;
		}

		ConfigurationResponse response = new ConfigurationResponse();
		response.setPersonDetails(personenDetails);

		return response;
	}

	@WebMethod
	public ConfigurationResponse getPersonByAhvNr(String ahvNr) {
		Person person = QueryHelper.getPerson(ahvNr);
		PersonDetails[] personenDetails;

		if (person != null) {

			personenDetails = new PersonDetails[1];

			PersonDetails personDetail = new PersonDetails();

			personDetail.setId(person.getId());
			personDetail.setName(person.getName());
			personDetail.setVorname(person.getVorname());
			personDetail.setAhvNr(person.getAhvNr());
			if (ImageStore.hasImage(person.getAhvNr())) {
				personDetail.setImageId(person.getAhvNr());
			}

			Einheit einheit = person.getEinheit();
			personDetail.setEinheitId(einheit != null ? einheit.getId() : -1);
			personDetail.setFunktion(person.getFunktion());
			personDetail.setGeburtsdatum(person.getGeburtsdatum());
			Grad grad = person.getGrad();
			personDetail.setGrad(grad != null ? grad.toString() : "");

			personenDetails[0] = personDetail;

		} else {
			personenDetails = new PersonDetails[0];

		}

		ConfigurationResponse response = new ConfigurationResponse();
		response.setPersonDetails(personenDetails);

		return response;
	}

	@WebMethod
	public void updatePerson(@WebParam(name = "PersonDetails") PersonDetails pd) {
		Person person = null;
		if ((pd.getId() != null) && (pd.getId() > 0)) {
			person = QueryHelper.getPerson(pd.getId());
		}
		if (person == null) {
			person = ObjectFactory.createPerson(null, null, null, null, null,
					null, null);
		}

		person.setName(pd.getName());
		person.setAhvNr(pd.getAhvNr());
		person.setEinheit(getEinheit(pd.getEinheitId()));
		person.setFunktion(pd.getFunktion());
		person.setGeburtsdatum(pd.getGeburtsdatum());
		person.setGrad(Grad.getGrad(pd.getGrad()));
		person.setId(pd.getId());
		person.setVorname(pd.getVorname());
		EntityManagerHelper.getEntityManager().persist(person);

		if (pd.getImage() != null) {
			ImageStore.saveJpegImage(person.getAhvNr(), pd.getImage());
		}
	}

	@WebMethod
	public boolean removePerson(@WebParam(name = "personId") Long personId) {
		if ((personId != null) && (personId > 0)) {
			Person person = QueryHelper.getPerson(personId);
			if (person != null) {
				EntityManagerHelper.getEntityManager().remove(person);
				return true;
			}
		}
		return false;
	}

	@WebMethod
	public ConfigurationResponse getCheckpoints() {
		List<Checkpoint> checkpoints = QueryHelper.getCheckpoints();

		CheckpointDetails[] checkpointsDetails = new CheckpointDetails[checkpoints
				.size()];
		for (int i = 0; i < checkpoints.size(); i++) {
			Checkpoint checkpoint = checkpoints.get(i);

			CheckpointDetails checkpointDetail = new CheckpointDetails();

			checkpointDetail.setId(checkpoint.getId());
			checkpointDetail.setName(checkpoint.getName());

			checkpointsDetails[i] = checkpointDetail;
		}

		ConfigurationResponse response = new ConfigurationResponse();
		response.setCheckpointDetails(checkpointsDetails);

		return response;
	}

	@WebMethod
	public void updateCheckpoint(
			@WebParam(name = "CheckpointDetails") CheckpointDetails cd) {
		Checkpoint checkpoint = null;
		if ((cd.getId() != null) && (cd.getId() > 0)) {
			checkpoint = QueryHelper.getCheckpoint(cd.getId());
		}
		if (checkpoint == null) {
			checkpoint = ObjectFactory.createCheckpoint("", null, null);
		}

		checkpoint.setName(cd.getName());
		EntityManagerHelper.getEntityManager().persist(checkpoint);
	}

	@WebMethod
	public boolean removeCheckpoint(
			@WebParam(name = "checkpointId") Long checkpointId) {
		if ((checkpointId != null) && (checkpointId > 0)) {
			Checkpoint checkpoint = QueryHelper.getCheckpoint(checkpointId);
			if (checkpoint != null) {
				EntityManagerHelper.getEntityManager().remove(checkpoint);
				return true;
			}
		}
		return false;
	}

	@WebMethod
	public void updateConfigurationValue(ConfigurationDetails c) {
		ConfigurationValue config = null;
		if ((c.getId() != null) && (c.getId() > 0)) {
			config = QueryHelper.getConfigurationValueById(c.getId());
		}
		if (config == null) {
			config = ObjectFactory.createConfigurationValue(c.getKey(),
					c.getStringValue(), c.getLongValue(), c.getValidFor());
		} else {
			config.setKey(c.getKey());
			config.setValidFor(c.getValidFor());
			config.setLongValue(c.getLongValue());
			config.setStringValue(c.getStringValue());
		}
		EntityManagerHelper.getEntityManager().persist(config);

	}

	@WebMethod
	public ConfigurationResponse getConfigurationValue(
			@WebParam(name = "checkpointId") Long checkpointId,
			@WebParam(name = "key") String key) {

		ConfigurationResponse response = new ConfigurationResponse();

		List<ConfigurationValue> configurationValues = QueryHelper
				.findConfigurationValue(key);
		List<ConfigurationDetails> temp = new ArrayList<ConfigurationDetails>();

		Checkpoint checkpoint = QueryHelper.getCheckpoint(checkpointId);
		if (checkpoint != null) {

			for (int i = 0; i < configurationValues.size(); i++) {
				String regex = configurationValues.get(i).getValidFor();
				// Falls Config für den Checkpoint gültig ist
				if ((regex != null) && (checkpoint != null)) {
					if (regex.equals("")
							|| (isValidRegex(regex) && checkpoint.getName()
									.matches(regex))) {
						temp.add(convert(configurationValues.get(i)));
					}
				}
			}

			response.setConfigurationDetails(temp
					.toArray(new ConfigurationDetails[0]));
		} else {
			response.setConfigurationDetails(new ConfigurationDetails[0]);
		}

		return response;
	}

	@WebMethod
	public ConfigurationResponse getGlobalConfigurationValue(
			@WebParam(name = "key") String key) {
		ConfigurationResponse response = new ConfigurationResponse();

		List<ConfigurationValue> configurationValues = QueryHelper
				.findConfigurationValue(key);
		ConfigurationDetails cds[] = new ConfigurationDetails[1];
		if (configurationValues.size() > 0) {
			cds[0] = convert(configurationValues.get(0));
			response.setConfigurationDetails(cds);
		}
		return response;
	}

	@WebMethod
	public ConfigurationResponse getConfigurationValues() {

		List<ConfigurationValue> configurationValues = QueryHelper
				.getConfigurationValues();

		ConfigurationDetails[] configurationDetails = new ConfigurationDetails[configurationValues
				.size()];
		for (int i = 0; i < configurationValues.size(); i++) {
			configurationDetails[i] = convert(configurationValues.get(i));
		}

		ConfigurationResponse response = new ConfigurationResponse();
		response.setConfigurationDetails(configurationDetails);
		return response;
	}

	@WebMethod
	public boolean removeConfiguration(
			@WebParam(name = "configurationId") Long configurationId) {
		if ((configurationId != null) && (configurationId > 0)) {
			ConfigurationValue config = QueryHelper
					.getConfigurationValueById(configurationId);
			if (config != null) {
				EntityManagerHelper.getEntityManager().remove(config);
				return true;
			}
		}
		return false;
	}

	@WebMethod
	public ConfigurationResponse getPrintJobs() {

		List<PrintJob> printJobs = QueryHelper.getPrintJobs();

		PrintJobDetails[] printJobDetails = new PrintJobDetails[printJobs
				.size()];
		for (int i = 0; i < printJobs.size(); i++) {
			printJobDetails[i] = convert(printJobs.get(i));
		}

		ConfigurationResponse response = new ConfigurationResponse();
		response.setPrintJobDetails(printJobDetails);
		return response;
	}

	@WebMethod
	public ConfigurationResponse getPrintJob(Long id) {
		PrintJobDetails[] printJobDetails = new PrintJobDetails[1];
		ConfigurationResponse response = new ConfigurationResponse();

		PrintJob job = QueryHelper.getPrintJobs(id).get(0);
		printJobDetails[0] = convert(job);
		printJobDetails[0].setPdf(PdfStore.loadPdf(job.getPintJobFile()));

		response.setPrintJobDetails(printJobDetails);
		return response;
	}

	@WebMethod
	public ConfigurationResponse printAusweise() {
		PrintJobDetails[] printJobDetails = new PrintJobDetails[1];
		ConfigurationResponse response = new ConfigurationResponse();

		IdentityCardRenderer renderer = new IdentityCardRenderer();
		PrintJob job = renderer.print();
		printJobDetails[0] = convert(job);

		printJobDetails[0].setPdf(PdfStore.loadPdf(job.getPintJobFile()));

		response.setPrintJobDetails(printJobDetails);
		return response;
	}

	@WebMethod
	public ConfigurationResponse printAusweisListe(boolean nurMitAusweis,
			boolean nachEinheit, String einheitName) {
		PrintJobDetails[] printJobDetails = new PrintJobDetails[1];
		ConfigurationResponse response = new ConfigurationResponse();

		PdfAusweisListe ausweisList = new PdfAusweisListe(nurMitAusweis,
				nachEinheit, einheitName);
		PrintJob job = ausweisList.print();
		printJobDetails[0] = convert(job);

		printJobDetails[0].setPdf(PdfStore.loadPdf(job.getPintJobFile()));

		response.setPrintJobDetails(printJobDetails);
		return response;
	}

	@WebMethod
	public ConfigurationResponse printAusweisboxInventar(String einheitName) {
		PrintJobDetails[] printJobDetails = new PrintJobDetails[1];
		ConfigurationResponse response = new ConfigurationResponse();

		PdfAusweisBoxInventar ausweisBoxListen = new PdfAusweisBoxInventar(
				einheitName);
		PrintJob job = ausweisBoxListen.print();
		printJobDetails[0] = convert(job);

		printJobDetails[0].setPdf(PdfStore.loadPdf(job.getPintJobFile()));

		response.setPrintJobDetails(printJobDetails);
		return response;
	}

	@WebMethod
	public String[] getGradValues() {
		String[] result = new String[Grad.values().length];
		for (int i = 0; i < Grad.values().length; i++) {
			result[i] = Grad.values()[i].toString();
		}
		return result;
	}

	@WebMethod
	public byte[] exportConfigData(String password) {
		return null;
	}

	@WebMethod
	public byte[] exportPersonData(String password) {
		return AusweisDatenWriter.export(password);
	}

	@WebMethod
	public boolean importPersonData(byte[] data, String password) {
		AusweisDatenReader reader = new AusweisDatenReader(data, password);
		if (reader.isValidPassword()) {
			reader.read();
			return true;
		} else {
			return false;
		}
	}

	@WebMethod
	public boolean importConfigData(byte[] data, String password) {
		return true;
	}

	@WebMethod
	public boolean importAusweisVorlage(byte[] data) {
		return FileHelper.saveAsFile(FileHelper.FILE_AUSWEISVORLAGE_JPG, data);
	}

	@WebMethod
	public boolean importWasserzeichen(byte[] data) {
		return FileHelper.saveAsFile(FileHelper.FILE_WASSERZEICHEN_PNG, data);
	}

	@WebMethod
	public String getLocalImagePath() {
		return ImageStore.getLocalImagePath();
	}

	private Einheit getEinheit(Long einheitId) {
		return QueryHelper.getEinheitById(einheitId);
	}

	private boolean isValidRegex(String pattern) {
		try {
			Pattern.compile(pattern);
		} catch (PatternSyntaxException exception) {
			// TODO LOG
			System.err.println(exception.getDescription());
			return false;
		}
		return true;
	}

	private PrintJobDetails convert(PrintJob job) {
		PrintJobDetails pjd = new PrintJobDetails();
		pjd.setPrintJobId(job.getId());
		pjd.setPintJobFile(job.getPintJobFile());
		pjd.setPrintJobDesc(job.getPrintJobDesc());
		pjd.setPrintJobDate(job.getPrintJobDate());
		return pjd;
	}

	private ConfigurationDetails convert(ConfigurationValue config) {
		ConfigurationDetails configurationDetail = new ConfigurationDetails();
		configurationDetail.setId(config.getId());
		configurationDetail.setKey(config.getKey());
		configurationDetail.setLongValue(config.getLongValue());
		configurationDetail.setStringValue(config.getStringValue());
		configurationDetail.setValidFor(config.getValidFor());

		return configurationDetail;
	}
}
