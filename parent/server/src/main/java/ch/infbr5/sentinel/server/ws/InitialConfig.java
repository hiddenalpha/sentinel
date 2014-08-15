package ch.infbr5.sentinel.server.ws;

import java.util.List;

import com.google.common.collect.Lists;

public class InitialConfig {

	private String checkpointName;

	private String zonenName;

	private String adminPw;

	private String superUserPw;

	private String idendityCardPwEffective;

	private String idendityCardPwConfigFile;

	private String idendityCardPwAusweisDaten;

	private boolean takeDefaultVorlage;

	private byte[] vorlageEffective;

	private byte[] vorlageAusweisDaten;

	private byte[] vorlageDefaultDaten;

	private boolean takeDefaultWasserzeichen;

	private byte[] wasserzeichenEffective;

	private byte[] wasserzeichenAusweisDaten;

	private byte[] wasserzeichenDefaultDaten;

	private List<ConfigurationDetails> details = Lists.newArrayList();

	public String getCheckpointName() {
		return checkpointName;
	}

	public void setCheckpointName(String checkpointName) {
		this.checkpointName = checkpointName;
	}

	public String getZonenName() {
		return zonenName;
	}

	public void setZonenName(String zonenName) {
		this.zonenName = zonenName;
	}

	public String getAdminPw() {
		return adminPw;
	}

	public void setAdminPw(String adminPw) {
		this.adminPw = adminPw;
	}

	public String getSuperUserPw() {
		return superUserPw;
	}

	public void setSuperUserPw(String superUserPw) {
		this.superUserPw = superUserPw;
	}

	public String getIdendityCardPwConfigFile() {
		return idendityCardPwConfigFile;
	}

	public void setIdendityCardPwConfigFile(String idendityCardPwConfigFile) {
		this.idendityCardPwConfigFile = idendityCardPwConfigFile;
	}

	public String getIdendityCardPwAusweisDaten() {
		return idendityCardPwAusweisDaten;
	}

	public void setIdendityCardPwAusweisDaten(String idendityCardPwAusweisDaten) {
		this.idendityCardPwAusweisDaten = idendityCardPwAusweisDaten;
	}

	public byte[] getVorlageAusweisDaten() {
		return vorlageAusweisDaten;
	}

	public void setVorlageAusweisDaten(byte[] vorlageAusweisDaten) {
		this.vorlageAusweisDaten = vorlageAusweisDaten;
	}

	public byte[] getVorlageDefaultDaten() {
		return vorlageDefaultDaten;
	}

	public void setVorlageDefaultDaten(byte[] vorlageDefaultDaten) {
		this.vorlageDefaultDaten = vorlageDefaultDaten;
	}

	public byte[] getWasserzeichenAusweisDaten() {
		return wasserzeichenAusweisDaten;
	}

	public void setWasserzeichenAusweisDaten(byte[] wasserzeichenAusweisDaten) {
		this.wasserzeichenAusweisDaten = wasserzeichenAusweisDaten;
	}

	public byte[] getWasserzeichenDefaultDaten() {
		return wasserzeichenDefaultDaten;
	}

	public void setWasserzeichenDefaultDaten(byte[] wasserzeichenDefaultDaten) {
		this.wasserzeichenDefaultDaten = wasserzeichenDefaultDaten;
	}

	public String getIdendityCardPwEffective() {
		return idendityCardPwEffective;
	}

	public void setIdendityCardPwEffective(String idendityCardPwEffective) {
		this.idendityCardPwEffective = idendityCardPwEffective;
	}

	public boolean isTakeDefaultVorlage() {
		return takeDefaultVorlage;
	}

	public void setTakeDefaultVorlage(boolean takeDefaultVorlage) {
		this.takeDefaultVorlage = takeDefaultVorlage;
	}

	public byte[] getVorlageEffective() {
		return vorlageEffective;
	}

	public void setVorlageEffective(byte[] vorlageEffective) {
		this.vorlageEffective = vorlageEffective;
	}

	public boolean isTakeDefaultWasserzeichen() {
		return takeDefaultWasserzeichen;
	}

	public void setTakeDefaultWasserzeichen(boolean takeDefaultWasserzeichen) {
		this.takeDefaultWasserzeichen = takeDefaultWasserzeichen;
	}

	public byte[] getWasserzeichenEffective() {
		return wasserzeichenEffective;
	}

	public void setWasserzeichenEffective(byte[] wasserzeichenEffective) {
		this.wasserzeichenEffective = wasserzeichenEffective;
	}

	public List<ConfigurationDetails> getDetails() {
		return details;
	}

	public void setDetails(List<ConfigurationDetails> details) {
		this.details = details;
	}

}
