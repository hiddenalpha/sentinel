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
import ch.infbr5.sentinel.server.ServerConfiguration;
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
import ch.infbr5.sentinel.server.print.IdendityCardRenderer;
import ch.infbr5.sentinel.server.print.PdfAusweisBoxInventar;
import ch.infbr5.sentinel.server.print.PdfAusweisListe;
import ch.infbr5.sentinel.server.ws.AusweisvorlageKonfiguration;
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

	/**
	 * Gibt alle Einheiten zurück.
	 * @return Alle Einheiten.
	 */
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
		Einheit einheit = getQueryHelper().getEinheitById(details.getId());
		einheit.setName(details.getName());
		einheit.setRgbColor_Einh(details.getRgbColor_Einh());
		einheit.setRgbColor_GsVb(details.getRgbColor_GsVb());
		einheit.setRgbColor_TrpK(details.getRgbColor_TrpK());
		einheit.setText_Einh(details.getText_Einh());
		einheit.setText_GsVb(details.getText_GsVb());
		einheit.setText_TrpK(details.getText_TrpK());
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
		// Default Daten laden oder bereits konfigurierte Daten laden
		ServerSetupInformation info = getServerSetupInformation();

		// Nun gegenebenfalls Daten überschreiben
		KonfigurationsDatenReader reader = new KonfigurationsDatenReader(data, password);
		for (ConfigurationValue value : reader.readData()) {
			if (value.getKey().equals(ConfigConstants.PASSWORD_ADMIN)) {
				if (value.getStringValue() != null && !value.getStringValue().isEmpty()) {
					info.setAdminPassword(value.getStringValue());
				}
			} else if (value.getKey().equals(ConfigConstants.PASSWORD_SUPERUSER)) {
				if (value.getStringValue() != null && !value.getStringValue().isEmpty()) {
					info.setSuperUserPassword(value.getStringValue());
				}
			} else if (value.getKey().equals(ConfigConstants.PASSWORD_IDENTITY_CARD)) {
				if (value.getStringValue() != null && !value.getStringValue().isEmpty()) {
					info.setIdentityCardPassword(value.getStringValue());
				}
			} else if (value.getKey().startsWith(ConfigConstants.URL_IPCAM_)) {
				if (value.getStringValue() != null && !value.getStringValue().isEmpty()) {
					if (!info.getIpCamUrls().contains(value.getStringValue())) {
						info.getIpCamUrls().add(value.getStringValue());
					}
				}
			} else if (value.getKey().equals(ConfigConstants.AUSWEISVORLAGE_BACKGROUND_COLOR)) {
				if (value.getStringValue() != null && !value.getStringValue().isEmpty()) {
					info.getAusweisvorlageConfig().setColorBackground(value.getStringValue());
				}
			} else if (value.getKey().equals(ConfigConstants.AUSWEISVORLAGE_COLOR_AREA_BACKSIDE)) {
				if (value.getStringValue() != null && !value.getStringValue().isEmpty()) {
					info.getAusweisvorlageConfig().setColorAreaBackside(value.getStringValue());
				}
			} else if (value.getKey().equals(ConfigConstants.AUSWEISVORLAGE_SHOW_AREA_BACKSIDE)) {
				if (value.getStringValue() != null && !value.getStringValue().isEmpty()) {
					info.getAusweisvorlageConfig().setShowAreaBackside(Boolean.parseBoolean(value.getStringValue()));
				}
			} else if (value.getKey().equals(ConfigConstants.AUSWEISVORLAGE_SHOW_QR_CODE)) {
				if (value.getStringValue() != null && !value.getStringValue().isEmpty()) {
					info.getAusweisvorlageConfig().setShowQRCode(Boolean.parseBoolean(value.getStringValue()));
				}
			} else if (value.getKey().equals(ConfigConstants.AUSWEISVORLAGE_USE_USER_LOGO)) {
				if (value.getStringValue() != null && !value.getStringValue().isEmpty()) {
					info.getAusweisvorlageConfig().setUseUserLogo(Boolean.parseBoolean(value.getStringValue()));
				}
			} else if (value.getKey().equals(ConfigConstants.AUSWEISVORLAGE_USE_USER_WASSERZEICHEN)) {
				if (value.getStringValue() != null && !value.getStringValue().isEmpty()) {
					info.getAusweisvorlageConfig().setUseUserWasserzeichen(Boolean.parseBoolean(value.getStringValue()));
				}
			}
		}

		byte[] logo = reader.getLogo();
		if (logo != null && logo.length > 0) {
			info.getAusweisvorlageConfig().setLogo(logo);
		}

		byte[] wasserzeichen = reader.getWasserzeichen();
		if (wasserzeichen != null && wasserzeichen.length > 0) {
			info.getAusweisvorlageConfig().setWasserzeichen(wasserzeichen);
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
			info.setCheckpointName(ServerConfiguration.DEFAULT_CHECKPOINT_NAME);
		} else {
			info.setCheckpointConfigured(true);
			info.setCheckpointName(checkpoints.get(0).getName());
		}

		// Zone
		List<Zone> zonen = getQueryHelper().getZonen();
		if (zonen.isEmpty()) {
			info.setZoneConfigured(false);
			info.setZonenName(ServerConfiguration.DEFAULT_ZONEN_NAME);
		} else {
			info.setZoneConfigured(true);
			info.setZonenName(zonen.get(0).getName());
		}

		// Admin Password
		info.setAdminPassword(getConfigurationValueString(ConfigConstants.PASSWORD_ADMIN, ServerConfiguration.DEFAULT_ADMIN_PASSWORD));

		// Superuser Password
		info.setSuperUserPassword(getConfigurationValueString(ConfigConstants.PASSWORD_SUPERUSER, ServerConfiguration.DEFAULT_SUPERUSER_PASSWORD));

		// Achtung, das IdendityCardPassword ist momentan immer fix gesetzt!
		info.setIdentityCardPassword(ServerConfiguration.DEFAULT_IDENTITY_CARD_PASSWORD);

		// Ausweisvorlage Konfiguration
		info.setAusweisvorlageConfig(createAusweisvorlageKonfiguration());

		// IP Cams
		List<ConfigurationValue> values = getQueryHelper().findConfigurationValue(ConfigConstants.URL_IPCAM_ALL);
		for (ConfigurationValue value : values) {
			info.getIpCamUrls().add(value.getStringValue());
		}

		// Calculate Server ist konfiguriert
		if (!info.isCheckpointConfigured() || !info.isZoneConfigured()) {
			info.setServerIsConfigured(false);
		} else {
			List<String> keys = Lists.newArrayList();
			keys.add(ConfigConstants.PASSWORD_ADMIN);
			keys.add(ConfigConstants.PASSWORD_IDENTITY_CARD);
			keys.add(ConfigConstants.PASSWORD_SUPERUSER);
			keys.add(ConfigConstants.AUSWEISVORLAGE_BACKGROUND_COLOR);
			keys.add(ConfigConstants.AUSWEISVORLAGE_COLOR_AREA_BACKSIDE);
			keys.add(ConfigConstants.AUSWEISVORLAGE_SHOW_AREA_BACKSIDE);
			keys.add(ConfigConstants.AUSWEISVORLAGE_SHOW_QR_CODE);
			keys.add(ConfigConstants.AUSWEISVORLAGE_USE_USER_LOGO);
			keys.add(ConfigConstants.AUSWEISVORLAGE_USE_USER_WASSERZEICHEN);
			info.setServerIsConfigured(checkConfigurationsExists(keys));
		}


		return info;
	}

	private boolean checkConfigurationsExists(List<String> keys) {
		for (String key : keys) {
			String value = getConfigurationValueString(key, null);
			if (value == null || value.isEmpty()) {
				return false;
			}
		}
		return true;
	}

	private AusweisvorlageKonfiguration createAusweisvorlageKonfiguration() {
		AusweisvorlageKonfiguration config = new AusweisvorlageKonfiguration();

		config.setColorBackground(getConfigurationValueString(ConfigConstants.AUSWEISVORLAGE_BACKGROUND_COLOR, ServerConfiguration.AUSWEISVORLAGE_BACKGROUND_COLOR));

		config.setShowAreaBackside(getConfigurationValueBoolean(ConfigConstants.AUSWEISVORLAGE_SHOW_AREA_BACKSIDE, ServerConfiguration.AUSWEISVORLAGE_SHOW_AREA_BACKSIDE));
		config.setColorAreaBackside(getConfigurationValueString(ConfigConstants.AUSWEISVORLAGE_COLOR_AREA_BACKSIDE, ServerConfiguration.AUSWEISVORLAGE_COLOR_AREA_BACKSIDE));

		config.setShowQRCode(getConfigurationValueBoolean(ConfigConstants.AUSWEISVORLAGE_SHOW_QR_CODE, ServerConfiguration.AUSWEISVORLAGE_SHOW_QR_CODE));

		config.setUseUserLogo(getConfigurationValueBoolean(ConfigConstants.AUSWEISVORLAGE_USE_USER_LOGO, ServerConfiguration.AUSWEISVORLAGE_USE_USER_LOGO));
		config.setUseUserWasserzeichen(getConfigurationValueBoolean(ConfigConstants.AUSWEISVORLAGE_USE_USER_WASSERZEICHEN, ServerConfiguration.AUSWEISVORLAGE_USE_USER_WASSERZEICHEN));

		config.setDefaultWasserzeichen(ServerConfiguration.getDefaultWasserzeichen());
		config.setWasserzeichen(ServerConfiguration.getUserWasserzeichen());

		config.setLogo(ServerConfiguration.getUserLogo());

		return config;
	}

	private String getConfigurationValueString(String key, String defaultValue) {
		ConfigurationValue value = getQueryHelper().findConfigurationValueByKey(key);
		if (value == null || value.getStringValue() == null || value.getStringValue().isEmpty()) {
			return defaultValue;
		}
		return value.getStringValue();
	}

	private boolean getConfigurationValueBoolean(String key, boolean defaultValue) {
		ConfigurationValue value = getQueryHelper().findConfigurationValueByKey(key);
		if (value == null || value.getStringValue() == null || value.getStringValue().isEmpty()) {
			return defaultValue;
		}
		return Boolean.parseBoolean(value.getStringValue());
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
		ConfigurationValue value = getQueryHelper().findConfigurationValueByKey(ConfigConstants.PASSWORD_ADMIN);
		if (value == null) {
			value = new ConfigurationValue();
			value.setKey(ConfigConstants.PASSWORD_ADMIN);
		}
		value.setStringValue(info.getAdminPassword());
		getEntityManager().persist(value);

		// Superuser Password
		value = getQueryHelper().findConfigurationValueByKey(ConfigConstants.PASSWORD_SUPERUSER);
		if (value == null) {
			value = new ConfigurationValue();
			value.setKey(ConfigConstants.PASSWORD_SUPERUSER);
		}
		value.setStringValue(info.getSuperUserPassword());
		getEntityManager().persist(value);

		// Identity Password
		value = getQueryHelper().findConfigurationValueByKey(ConfigConstants.PASSWORD_IDENTITY_CARD);
		if (value == null) {
			value = new ConfigurationValue();
			value.setKey(ConfigConstants.PASSWORD_IDENTITY_CARD);
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

		// Ausweisvorlage
		applyAusweisvorlageKonfiguration(info.getAusweisvorlageConfig());
	}

	private void applyAusweisvorlageKonfiguration(AusweisvorlageKonfiguration config) {
		setConfigurationValueString(ConfigConstants.AUSWEISVORLAGE_BACKGROUND_COLOR, config.getColorBackground());

		setConfigurationValueString(ConfigConstants.AUSWEISVORLAGE_COLOR_AREA_BACKSIDE, config.getColorAreaBackside());
		setConfigurationValueBoolean(ConfigConstants.AUSWEISVORLAGE_SHOW_AREA_BACKSIDE, config.isShowAreaBackside());

		setConfigurationValueBoolean(ConfigConstants.AUSWEISVORLAGE_SHOW_QR_CODE, config.isShowQRCode());

		setConfigurationValueBoolean(ConfigConstants.AUSWEISVORLAGE_USE_USER_LOGO, config.isUseUserLogo());
		setConfigurationValueBoolean(ConfigConstants.AUSWEISVORLAGE_USE_USER_WASSERZEICHEN, config.isUseUserWasserzeichen());

		ServerConfiguration.saveUserLogo(config.getLogo());
		ServerConfiguration.saveUserWasserzeichen(config.getWasserzeichen());
	}

	private void setConfigurationValueBoolean(String key, boolean value) {
		ConfigurationValue configValue = getQueryHelper().findConfigurationValueByKey(key);
		if (configValue == null) {
			configValue = new ConfigurationValue();
			configValue.setKey(key);
		}
		configValue.setStringValue(String.valueOf(value));
		getEntityManager().persist(configValue);
	}

	private void setConfigurationValueString(String key, String value) {
		ConfigurationValue configValue = getQueryHelper().findConfigurationValueByKey(key);
		if (configValue == null) {
			configValue = new ConfigurationValue();
			configValue.setKey(key);
		}
		configValue.setStringValue(value);
		getEntityManager().persist(configValue);
	}

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
					if (regex == null || (regex.equals("") || (isValidRegex(regex) && checkpoint.getName().matches(regex)))) {
						temp.add(convert(configurationValues.get(i)));
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
				ConfigConstants.PASSWORD_IDENTITY_CARD);
		if (passwordList.size() > 0) {
			password = passwordList.get(0).getStringValue();
		}

		AusweisvorlageKonfiguration config = createAusweisvorlageKonfiguration();

		IdendityCardRenderer renderer = new IdendityCardRenderer(ausweise, password, config);
		PrintJob job = renderer.print();
		if (job != null) {
			// In diesem Fall hat es Funktioniert und die Ausweise wurden erstellt.
			for (Ausweis ausweis : ausweise) {
				ausweis.setErstellt(true);
				getEntityManager().persist(ausweis);
			}

			getEntityManager().persist(job);

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

		List<Person> personen = getQueryHelper().getPersonen(nurMitAusweis, nachEinheit, einheitName);
		PdfAusweisListe ausweisList = new PdfAusweisListe(personen, nurMitAusweis, nachEinheit, einheitName);
		PrintJob job = ausweisList.print();
		if (job != null) {
			getEntityManager().persist(job);

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

		List<Person> personen = getQueryHelper().getPersonen(true, true, einheitName);
		PdfAusweisBoxInventar ausweisBoxListen = new PdfAusweisBoxInventar(personen, einheitName);
		PrintJob job = ausweisBoxListen.print();
		if (job != null) {
			getEntityManager().persist(job);

			PrintJobDetails[] printJobDetails = new PrintJobDetails[1];
			printJobDetails[0] = convert(job);
			printJobDetails[0].setPdf(PdfStore.loadPdf(job.getPintJobFile()));
			response.setPrintJobDetails(printJobDetails);
		}

		return response;
	}

	@WebMethod
	// TODO Man sollte nicht mit toString() arbeiten. Besser wäre wohl getGradText().
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
			log.error(exception);
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
