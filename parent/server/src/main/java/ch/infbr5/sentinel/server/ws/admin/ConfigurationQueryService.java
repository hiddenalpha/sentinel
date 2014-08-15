package ch.infbr5.sentinel.server.ws.admin;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import javax.annotation.Resource;
import javax.jws.HandlerChain;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.persistence.EntityManager;
import javax.xml.ws.WebServiceContext;

import org.apache.log4j.Logger;

import ch.infbr5.sentinel.common.config.ConfigConstants;
import ch.infbr5.sentinel.server.db.EntityManagerHelper;
import ch.infbr5.sentinel.server.db.ImageStore;
import ch.infbr5.sentinel.server.db.PdfStore;
import ch.infbr5.sentinel.server.db.QueryHelper;
import ch.infbr5.sentinel.server.exporter.AusweisDatenWriter;
import ch.infbr5.sentinel.server.exporter.KonfigurationsDatenWriter;
import ch.infbr5.sentinel.server.importer.AusweisDatenReader;
import ch.infbr5.sentinel.server.importer.KonfigurationsDatenReader;
import ch.infbr5.sentinel.server.mapper.Mapper;
import ch.infbr5.sentinel.server.model.Ausweis;
import ch.infbr5.sentinel.server.model.Checkpoint;
import ch.infbr5.sentinel.server.model.ConfigurationValue;
import ch.infbr5.sentinel.server.model.Einheit;
import ch.infbr5.sentinel.server.model.Grad;
import ch.infbr5.sentinel.server.model.ObjectFactory;
import ch.infbr5.sentinel.server.model.Person;
import ch.infbr5.sentinel.server.model.PrintJob;
import ch.infbr5.sentinel.server.model.Zone;
import ch.infbr5.sentinel.server.model.Zutrittsregel;
import ch.infbr5.sentinel.server.print.IdentityCardRenderer;
import ch.infbr5.sentinel.server.print.PdfAusweisBoxInventar;
import ch.infbr5.sentinel.server.print.PdfAusweisListe;
import ch.infbr5.sentinel.server.utils.FileHelper;
import ch.infbr5.sentinel.server.ws.CheckpointDetails;
import ch.infbr5.sentinel.server.ws.ConfigurationDetails;
import ch.infbr5.sentinel.server.ws.EinheitDetails;
import ch.infbr5.sentinel.server.ws.IPCams;
import ch.infbr5.sentinel.server.ws.PersonDetails;
import ch.infbr5.sentinel.server.ws.PrintJobDetails;
import ch.infbr5.sentinel.server.ws.ServerSetupInformation;
import ch.infbr5.sentinel.server.ws.ZoneDetails;

import com.google.common.collect.Lists;

@WebService(name = "ConfigurationQueryService", targetNamespace = "http://ws.sentinel.infbr5.ch/")
@HandlerChain(file = "/META-INF/ws-handler-chain.xml")
@SOAPBinding(style = SOAPBinding.Style.RPC)
public class ConfigurationQueryService {

	private static final Logger log = Logger.getLogger(ConfigurationQueryService.class);

	@Resource
	private WebServiceContext context;

	@WebMethod
	public ConfigurationResponse getEinheiten() {
		List<Einheit> einheiten = getQueryHelper().getEinheiten();
		List<EinheitDetails> einheitenDetails = Lists.transform(einheiten, Mapper.mapEinheitToEinheitDetails());

		ConfigurationResponse response = new ConfigurationResponse();
		response.setEinheitDetails(einheitenDetails);
		return response;
	}

	@WebMethod
	public void updateEinheit(@WebParam(name = "EinheitDetails") EinheitDetails details) {
		Einheit einheit = Mapper.mapEinheitDetailsToEinheit().apply(details);
		getEntityManager().persist(einheit);
	}

	@WebMethod
	public boolean removeEinheit(@WebParam(name = "einheitId") Long einheitId) {
		if (einheitId != null && einheitId > 0) {
			Einheit einheit = getQueryHelper().getEinheitById(einheitId);
			if (einheit != null) {
				getEntityManager().remove(einheit);
				return true;
			}
		}
		return false;
	}

	@WebMethod
	public ConfigurationResponse getPersonen() {
		List<Person> personen = getQueryHelper().getPersonen();
		List<PersonDetails> personenDetails = Lists.transform(personen, Mapper.mapPersonToPersonDetails());

		ConfigurationResponse response = new ConfigurationResponse();
		response.setPersonDetails(personenDetails);
		return response;
	}

	@WebMethod
	public ConfigurationResponse getPersonByAhvNr(String ahvNr) {
		List<PersonDetails> personenDetails = Lists.newArrayList();

		Person person = getQueryHelper().getPerson(ahvNr);
		if (person != null) {
			PersonDetails personDetail = Mapper.mapPersonToPersonDetails().apply(person);
			personenDetails.add(personDetail);
		}

		ConfigurationResponse response = new ConfigurationResponse();
		response.setPersonDetails(personenDetails);
		return response;
	}

	@WebMethod
	public void updatePerson(@WebParam(name = "PersonDetails") PersonDetails pd) {
		Person person = null;
		if (pd.getId() != null && pd.getId() > 0) {
			person = getQueryHelper().getPerson(pd.getId());
		}
		if (person == null) {
			person = new Person();
		}

		person.setId(pd.getId());
		person.setAhvNr(pd.getAhvNr());
		person.setName(pd.getName());
		person.setVorname(pd.getVorname());
		person.setGrad(Grad.getGrad(pd.getGrad()));
		person.setFunktion(pd.getFunktion());
		person.setGeburtsdatum(pd.getGeburtsdatum());
		person.setEinheit(getEinheit(pd.getEinheitId()));
		getEntityManager().persist(person);

		if (pd.getImage() != null) {
			ImageStore.saveJpegImage(person.getAhvNr(), pd.getImage());
		}
	}

	@WebMethod
	public boolean removePerson(@WebParam(name = "personId") Long personId) {
		if (personId != null && personId > 0) {
			Person person = getQueryHelper().getPerson(personId);
			if (person != null) {
				getEntityManager().remove(person);
				return true;
			}
		}
		return false;
	}

	@WebMethod
	public ServerSetupInformation getServerSetupInformationFromConfigFile(byte[] data, String password) {
		ServerSetupInformation info = new ServerSetupInformation();
		KonfigurationsDatenReader reader = new KonfigurationsDatenReader(data, password);
		info.setCheckpointName("");
		info.setZonenName("");
		for (ConfigurationValue value : reader.readData()) {
			if (value.getKey().equals(ConfigConstants.ADMIN_PASSWORD)) {
				info.setAdminPassword(value == null ? "" : value.getStringValue());
			} else if (value.getKey().equals(ConfigConstants.SUPERUSER_PASSWORD)) {
				info.setSuperUserPassword(value == null ? "" : value.getStringValue());
			} else if (value.getKey().equals(ConfigConstants.IDENTITY_CARD_PASSWORD)) {
				info.setIdentityCardPassword(value == null ? "" : value.getStringValue());
			} else if (value.getKey().startsWith(ConfigConstants.URL_IPCAM_)) {
				info.getIpCamUrls().add(value.getStringValue());
			}
		}
		return info;
	}

	@WebMethod
	public ServerSetupInformation getServerSetupInformation() {
		ServerSetupInformation info = new ServerSetupInformation();

		// Checkpoint
		List<Checkpoint> checkpoints = getQueryHelper().getCheckpoints();
		if (checkpoints.isEmpty()) {
			info.setCheckpointConfigured(false);
			info.setCheckpointName(ConfigConstants.DEFAULT_CHECKPOINT_NAME);
		} else {
			info.setCheckpointConfigured(true);
			info.setCheckpointName(checkpoints.get(0).getName());
		}

		// Zone
		List<Zone> zonen = getQueryHelper().getZonen();
		if (zonen.isEmpty()) {
			info.setZoneConfigured(false);
			info.setZonenName(ConfigConstants.DEFAULT_ZONEN_NAME);
		} else {
			info.setZoneConfigured(true);
			info.setZonenName(zonen.get(0).getName());
		}

		// Admin Password
		ConfigurationValue value = getQueryHelper().findConfigurationValueByKey(ConfigConstants.ADMIN_PASSWORD);
		info.setAdminPassword(value == null ? "" : value.getStringValue());

		// Superuser Password
		value = getQueryHelper().findConfigurationValueByKey(ConfigConstants.SUPERUSER_PASSWORD);
		info.setSuperUserPassword(value == null ? "" : value.getStringValue());

		// Identity Password
		value = getQueryHelper().findConfigurationValueByKey(ConfigConstants.IDENTITY_CARD_PASSWORD);
		info.setIdentityCardPassword(value == null ? "" : value.getStringValue());

		// IP Cams
		List<ConfigurationValue> values = getQueryHelper().findConfigurationValue(ConfigConstants.URL_IPCAM_ALL);
		for (ConfigurationValue v : values) {
			info.getIpCamUrls().add(v.getStringValue());
		}

		info.calculateServerSetup();

		return info;
	}

	@WebMethod
	public IPCams getIPCams() {
		IPCams cams = new IPCams();
		List<ConfigurationValue> values = getQueryHelper().findConfigurationValue(ConfigConstants.URL_IPCAM_ALL);
		for (ConfigurationValue v : values) {
			cams.getCams().add(v.getStringValue());
		}
		return cams;
	}

	@WebMethod
	public void setIPCams(IPCams ipcams) {
		List<ConfigurationValue> values = getQueryHelper().findConfigurationValue(ConfigConstants.URL_IPCAM_ALL);
		for (ConfigurationValue v : values) {
			getEntityManager().remove(v);
		}
		int i = 1;
		for (String url : ipcams.getCams()) {
			ConfigurationValue v = new ConfigurationValue();
			v.setKey(ConfigConstants.URL_IPCAM_ + i);
			v.setStringValue(url);
			getEntityManager().persist(v);
			i++;
		}
	}

	@WebMethod
	public void applyServerSetupInformation(ServerSetupInformation info) {
		// Zone
		if (!info.isZoneConfigured()) {
			Zutrittsregel regel = ObjectFactory.createZutrittsregel();
			getEntityManager().persist(regel);
			List<Zutrittsregel> regeln = new ArrayList<Zutrittsregel>();
			regeln.add(regel);
			Zone zone = ObjectFactory.createZone(info.getZonenName(), regeln, false);
			getEntityManager().persist(zone);
		}

		// Checkpoint
		if (!info.isCheckpointConfigured()) {
			List<Zone> checkInZonen = new ArrayList<Zone>();
			checkInZonen.add(getQueryHelper().getZonen().get(0));
			List<Zone> checkOutZonen = new ArrayList<Zone>();
			Checkpoint checkpoint = ObjectFactory.createCheckpoint(info.getCheckpointName(), checkInZonen,
					checkOutZonen);
			getEntityManager().persist(checkpoint);
		}

		// Admin Password
		ConfigurationValue value = getQueryHelper().findConfigurationValueByKey(ConfigConstants.ADMIN_PASSWORD);
		if (value == null) {
			value = new ConfigurationValue();
			value.setKey(ConfigConstants.ADMIN_PASSWORD);
		}
		value.setStringValue(info.getAdminPassword());
		getEntityManager().persist(value);

		// Superuser Password
		value = getQueryHelper().findConfigurationValueByKey(ConfigConstants.SUPERUSER_PASSWORD);
		if (value == null) {
			value = new ConfigurationValue();
			value.setKey(ConfigConstants.SUPERUSER_PASSWORD);
		}
		value.setStringValue(info.getSuperUserPassword());
		getEntityManager().persist(value);

		// Identity Password
		value = getQueryHelper().findConfigurationValueByKey(ConfigConstants.IDENTITY_CARD_PASSWORD);
		if (value == null) {
			value = new ConfigurationValue();
			value.setKey(ConfigConstants.IDENTITY_CARD_PASSWORD);
		}
		value.setStringValue(info.getIdentityCardPassword());
		getEntityManager().persist(value);

		// IP Cams
		List<ConfigurationValue> values = getQueryHelper().findConfigurationValue(ConfigConstants.URL_IPCAM_ALL);
		for (ConfigurationValue v : values) {
			getEntityManager().remove(v);
		}
		int i = 1;
		for (String url : info.getIpCamUrls()) {
			ConfigurationValue v = new ConfigurationValue();
			v.setKey(ConfigConstants.URL_IPCAM_ + i);
			v.setStringValue(url);
			getEntityManager().persist(v);
			i++;
		}
	}

	/*
	 * @WebMethod public InitialConfig calculateInitialConfig(byte[]
	 * exportedConfigFile, String password1, byte[] exportedAusweisdaten, String
	 * password2) {
	 *
	 * log.debug("Erstelle Initial Config"); InitialConfig initialConfig = new
	 * InitialConfig();
	 *
	 * // Generelle Daten initialConfig.setCheckpointName("Haupteingang");
	 * initialConfig.setZonenName("Kommandoposten");
	 *
	 * // Konfigurations Daten if (exportedConfigFile != null &&
	 * exportedConfigFile.length > 0) { log.debug("Konfigurationsdatei laden");
	 * KonfigurationsDatenReader reader = new
	 * KonfigurationsDatenReader(exportedConfigFile, password1);
	 * List<ConfigurationValue> values = reader.readData(); for
	 * (ConfigurationValue value : values) { if
	 * (value.getKey().equals(ConfigConstants.SUPERUSER_PASSWORD)) {
	 * initialConfig.setSuperUserPw(value.getStringValue()); } else if
	 * (value.getKey().equals(ConfigConstants.ADMIN_PASSWORD)) {
	 * initialConfig.setAdminPw(value.getStringValue()); } else if
	 * (value.getKey().equals(ConfigConstants.IDENTITY_CARD_PASSWORD)) {
	 * initialConfig.setIdendityCardPwConfigFile(value.getStringValue()); } else
	 * { initialConfig.getDetails().add(Mapper.
	 * mapConfigurationValuetoConfigurationDetails().apply(value)); } } }
	 *
	 * // Ausweisdaten (Wasserzeichen, Vorlage) if (exportedAusweisdaten != null
	 * && exportedAusweisdaten.length > 0) { log.debug("Ausweisdaten laden");
	 * AusweisDatenReader reader = new AusweisDatenReader(exportedAusweisdaten,
	 * password2);
	 * initialConfig.setVorlageAusweisDaten(reader.readAusweisVorlage());
	 * initialConfig.setWasserzeichenAusweisDaten(reader.readWasserzeichen()); }
	 *
	 * // Per Default immer die Sentinel Vorgaben verwenden
	 * initialConfig.setTakeDefaultVorlage(true);
	 * initialConfig.setTakeDefaultWasserzeichen(true);
	 *
	 * // Effektives PW if (initialConfig.getIdendityCardPwAusweisDaten() !=
	 * null && !initialConfig.getIdendityCardPwAusweisDaten().isEmpty()) {
	 * initialConfig
	 * .setIdendityCardPwEffective(initialConfig.getIdendityCardPwAusweisDaten
	 * ()); } else { initialConfig.setIdendityCardPwEffective(initialConfig.
	 * getIdendityCardPwConfigFile()); }
	 *
	 * // Default Wasserzeichen und Vorlage
	 * initialConfig.setWasserzeichenDefaultDaten
	 * (IdentityCardRenderer.getDefaultWasserzeichen());
	 * initialConfig.setVorlageDefaultDaten
	 * (IdentityCardRenderer.getDefaultAusweisvorlage());
	 *
	 * return initialConfig; }
	 *
	 * @WebMethod public void applyInitialConfig(InitialConfig config) {
	 *
	 * // Zutrittsregeln Zutrittsregel regel =
	 * ObjectFactory.createZutrittsregel(); getEntityManager().persist(regel);
	 *
	 * List<Zutrittsregel> regeln = new ArrayList<Zutrittsregel>();
	 * regeln.add(regel);
	 *
	 * // Zone Zone zone = ObjectFactory.createZone(config.getZonenName(),
	 * regeln, false); getEntityManager().persist(zone);
	 *
	 * // Checkpoint List<Zone> checkInZonen = new ArrayList<Zone>();
	 * checkInZonen.add(zone); List<Zone> checkOutZonen = new ArrayList<Zone>();
	 * Checkpoint checkpoint =
	 * ObjectFactory.createCheckpoint(config.getCheckpointName(), checkInZonen,
	 * checkOutZonen); getEntityManager().persist(checkpoint);
	 *
	 * // Configurations List<ConfigurationValue> values = Lists.newArrayList();
	 *
	 * removeConfigValue(ConfigConstants.SUPERUSER_PASSWORD); ConfigurationValue
	 * value = new ConfigurationValue();
	 * value.setKey(ConfigConstants.SUPERUSER_PASSWORD);
	 * value.setStringValue(config.getSuperUserPw()); values.add(value);
	 *
	 * removeConfigValue(ConfigConstants.ADMIN_PASSWORD); value = new
	 * ConfigurationValue(); value.setKey(ConfigConstants.ADMIN_PASSWORD);
	 * value.setStringValue(config.getAdminPw()); values.add(value);
	 *
	 * removeConfigValue(ConfigConstants.IDENTITY_CARD_PASSWORD); value = new
	 * ConfigurationValue();
	 * value.setKey(ConfigConstants.IDENTITY_CARD_PASSWORD);
	 * value.setStringValue(config.getIdendityCardPwEffective());
	 * values.add(value);
	 *
	 * for (ConfigurationDetails detail : config.getDetails()) {
	 * values.add(Mapper
	 * .mapConfigurationDetailsToConfigurationValue().apply(detail)); }
	 *
	 * getQueryHelper().persistAllConfiguration(values);
	 *
	 * // Vorlage if (!config.isTakeDefaultVorlage()) { try {
	 * Files.write(config.getVorlageEffective(), new
	 * File(FileHelper.FILE_AUSWEISVORLAGE_JPG)); } catch (Exception e) {
	 * log.error(e); } }
	 *
	 * // Wasserzeichen if (!config.isTakeDefaultWasserzeichen()) { try {
	 * Files.write(config.getWasserzeichenEffective(), new
	 * File(FileHelper.FILE_WASSERZEICHEN_PNG)); } catch (Exception e) {
	 * log.error(e); } } }
	 */

	/*
	 * private void removeConfigValue(String key) { for (ConfigurationValue
	 * value : getQueryHelper().getConfigurationValues()) { if
	 * (value.getKey().equals(key)) { getEntityManager().remove(value); } } }
	 */

	@WebMethod
	public ConfigurationResponse getZonen() {
		List<Zone> zonen = getQueryHelper().getZonen();

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
	public ConfigurationResponse getCheckpoints() {
		List<Checkpoint> checkpoints = getQueryHelper().getCheckpoints();

		CheckpointDetails[] checkpointsDetails = new CheckpointDetails[checkpoints.size()];
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
	public void updateCheckpoint(@WebParam(name = "CheckpointDetails") CheckpointDetails cd) {
		Checkpoint checkpoint = null;
		if ((cd.getId() != null) && (cd.getId() > 0)) {
			checkpoint = getQueryHelper().getCheckpoint(cd.getId());
		}
		if (checkpoint == null) {
			checkpoint = ObjectFactory.createCheckpoint("", null, null);
		}

		checkpoint.setName(cd.getName());
		getEntityManager().persist(checkpoint);
	}

	@WebMethod
	public boolean removeCheckpoint(@WebParam(name = "checkpointId") Long checkpointId) {
		if ((checkpointId != null) && (checkpointId > 0)) {
			Checkpoint checkpoint = getQueryHelper().getCheckpoint(checkpointId);
			if (checkpoint != null) {
				getEntityManager().remove(checkpoint);
				return true;
			}
		}
		return false;
	}

	@WebMethod
	public void updateConfigurationValue(ConfigurationDetails c) {
		ConfigurationValue config = null;
		if ((c.getId() != null) && (c.getId() > 0)) {
			config = getQueryHelper().getConfigurationValueById(c.getId());
		}
		if (config == null) {
			config = ObjectFactory.createConfigurationValue(c.getKey(), c.getStringValue(), c.getLongValue(),
					c.getValidFor());
		} else {
			config.setKey(c.getKey());
			config.setValidFor(c.getValidFor());
			config.setLongValue(c.getLongValue());
			config.setStringValue(c.getStringValue());
		}
		getEntityManager().persist(config);

	}

	@WebMethod
	public ConfigurationResponse getConfigurationValue(@WebParam(name = "checkpointId") Long checkpointId,
			@WebParam(name = "key") String key) {

		// TODO Angeblich gibt Checkpoint abhängige Konfigurationen, super!
		// Jedoch wird die Checkpoint abhängigkeit über den Namen des
		// Checkpoints statt über die ID aufgelöst.
		// Falls GültigFür leer ist, dann ist die Konfiguration global
		// Ich denke hier braucht es ein Refactoring um die Strukturen klarer zu
		// machen.

		ConfigurationResponse response = new ConfigurationResponse();

		List<ConfigurationValue> configurationValues = getQueryHelper().findConfigurationValue(key);
		List<ConfigurationDetails> temp = new ArrayList<ConfigurationDetails>();

		Checkpoint checkpoint = getQueryHelper().getCheckpoint(checkpointId);
		if (checkpoint != null) {

			for (int i = 0; i < configurationValues.size(); i++) {
				String regex = configurationValues.get(i).getValidFor();
				// Falls Config für den Checkpoint gültig ist
				if (regex != null) {
					if (regex.equals("") || (isValidRegex(regex) && checkpoint.getName().matches(regex))) {
						temp.add(convert(configurationValues.get(i)));
					}
				}
			}

			response.setConfigurationDetails(temp.toArray(new ConfigurationDetails[0]));
		} else {
			response.setConfigurationDetails(new ConfigurationDetails[0]);
		}

		return response;
	}

	@WebMethod
	public ConfigurationResponse getGlobalConfigurationValue(@WebParam(name = "key") String key) {
		ConfigurationResponse response = new ConfigurationResponse();

		List<ConfigurationValue> configurationValues = getQueryHelper().findConfigurationValue(key);
		ConfigurationDetails cds[] = new ConfigurationDetails[1];
		if (configurationValues.size() > 0) {
			cds[0] = convert(configurationValues.get(0));
			response.setConfigurationDetails(cds);
		}
		return response;
	}

	@WebMethod
	public ConfigurationResponse getGlobalConfigurationValues(@WebParam(name = "key") String key) {
		ConfigurationResponse response = new ConfigurationResponse();
		List<ConfigurationValue> configurationValues = getQueryHelper().findConfigurationValue(key);
		ConfigurationDetails[] details = new ConfigurationDetails[configurationValues.size()];
		int i = 0;
		for (ConfigurationValue value : configurationValues) {
			details[i] = convert(value);
			i++;
		}
		response.setConfigurationDetails(details);
		return response;
	}

	@WebMethod
	public ConfigurationResponse getConfigurationValues() {

		List<ConfigurationValue> configurationValues = getQueryHelper().getConfigurationValues();

		ConfigurationDetails[] configurationDetails = new ConfigurationDetails[configurationValues.size()];
		for (int i = 0; i < configurationValues.size(); i++) {
			configurationDetails[i] = convert(configurationValues.get(i));
		}

		ConfigurationResponse response = new ConfigurationResponse();
		response.setConfigurationDetails(configurationDetails);
		return response;
	}

	@WebMethod
	public boolean removeConfiguration(@WebParam(name = "configurationId") Long configurationId) {
		if ((configurationId != null) && (configurationId > 0)) {
			ConfigurationValue config = getQueryHelper().getConfigurationValueById(configurationId);
			if (config != null) {
				getEntityManager().remove(config);
				return true;
			}
		}
		return false;
	}

	@WebMethod
	public ConfigurationResponse getPrintJobs() {
		List<PrintJob> printJobs = getQueryHelper().getPrintJobs();

		PrintJobDetails[] printJobDetails = new PrintJobDetails[printJobs.size()];
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

		PrintJob job = getQueryHelper().getPrintJobs(id).get(0);
		printJobDetails[0] = convert(job);
		printJobDetails[0].setPdf(PdfStore.loadPdf(job.getPintJobFile()));

		response.setPrintJobDetails(printJobDetails);
		return response;
	}

	@WebMethod
	public int anzahlAusstehendeZuDruckendeAusweise() {
		return getQueryHelper().findAusweiseZumDrucken().size();
	}

	@WebMethod
	public ConfigurationResponse printAusweise() {
		ConfigurationResponse response = new ConfigurationResponse();

		List<Ausweis> ausweise = getQueryHelper().findAusweiseZumDrucken();
		String password = "";
		List<ConfigurationValue> passwordList = getQueryHelper().findConfigurationValue(
				ConfigConstants.IDENTITY_CARD_PASSWORD);
		if (passwordList.size() > 0) {
			password = passwordList.get(0).getStringValue();
		}

		IdentityCardRenderer renderer = new IdentityCardRenderer(getEntityManager(), ausweise, password);
		PrintJob job = renderer.print();
		if (job != null) {
			PrintJobDetails[] printJobDetails = new PrintJobDetails[1];
			printJobDetails[0] = convert(job);
			printJobDetails[0].setPdf(PdfStore.loadPdf(job.getPintJobFile()));
			response.setPrintJobDetails(printJobDetails);
		}
		return response;
	}

	@WebMethod
	public ConfigurationResponse printAusweisListe(boolean nurMitAusweis, boolean nachEinheit, String einheitName) {
		ConfigurationResponse response = new ConfigurationResponse();

		PdfAusweisListe ausweisList = new PdfAusweisListe(getEntityManager(), nurMitAusweis, nachEinheit, einheitName);
		PrintJob job = ausweisList.print();
		if (job != null) {
			PrintJobDetails[] printJobDetails = new PrintJobDetails[1];
			printJobDetails[0] = convert(job);
			printJobDetails[0].setPdf(PdfStore.loadPdf(job.getPintJobFile()));
			response.setPrintJobDetails(printJobDetails);
		}

		return response;
	}

	@WebMethod
	public ConfigurationResponse printAusweisboxInventar(String einheitName) {
		ConfigurationResponse response = new ConfigurationResponse();

		PdfAusweisBoxInventar ausweisBoxListen = new PdfAusweisBoxInventar(getEntityManager(), einheitName);
		PrintJob job = ausweisBoxListen.print();
		if (job != null) {
			PrintJobDetails[] printJobDetails = new PrintJobDetails[1];
			printJobDetails[0] = convert(job);
			printJobDetails[0].setPdf(PdfStore.loadPdf(job.getPintJobFile()));
			response.setPrintJobDetails(printJobDetails);
		}

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
	public byte[] exportPersonData(String password) {
		List<Person> result = getQueryHelper().getPersonen();
		return AusweisDatenWriter.export(password, result);
	}

	@WebMethod
	public boolean importPersonData(byte[] data, String password) {
		AusweisDatenReader reader = new AusweisDatenReader(data, password);
		try {
			List<Person> personen = reader.readData();
			reader.importBilder();
			if (!personen.isEmpty()) {
				getQueryHelper().removeAllPersonData();
				getQueryHelper().persistAllPersonData(personen);
			}
			return true;
		} catch (RuntimeException e) {
			log.error(e);
			return false;
		}
	}

	@WebMethod
	public byte[] exportConfigData(@WebParam(name = "password") String password) {
		log.debug("Exportiere Konfiguration");
		List<ConfigurationValue> values = getQueryHelper().getConfigurationValues();
		return KonfigurationsDatenWriter.export(password, values);
	}

	@WebMethod
	public boolean importConfigData(@WebParam(name = "data") byte[] data, @WebParam(name = "password") String password) {
		log.debug("Importiere Konfiguration");
		KonfigurationsDatenReader reader = new KonfigurationsDatenReader(data, password);
		List<ConfigurationValue> values = reader.readData();
		if (!reader.hasError()) {
			getQueryHelper().removeAllConfiguration();
			getQueryHelper().persistAllConfiguration(values);
			log.debug("Es wurde " + values.size() + " Konfigurationswerte importiert.");
		}
		return !reader.hasError();
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
		return getQueryHelper().getEinheitById(einheitId);
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

	private EntityManager getEntityManager() {
		return EntityManagerHelper.getEntityManager(context);
	}

	private QueryHelper getQueryHelper() {
		return new QueryHelper(getEntityManager());
	}

}
