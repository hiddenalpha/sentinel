package ch.infbr5.sentinel.server.ws.importer;

import java.io.File;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

import javax.jws.HandlerChain;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;

import ch.infbr5.sentinel.server.importer.personen.PersonenImporter;
import ch.infbr5.sentinel.server.importer.personen.state.PersonenImporterStatePersister;
import ch.infbr5.sentinel.server.utils.FileHelper;
import ch.infbr5.sentinel.server.ws.importer.mapping.ColumnMappingResponse;
import ch.infbr5.sentinel.server.ws.importer.mapping.PersonenImportColumn;
import ch.infbr5.sentinel.server.ws.importer.mapping.PersonenImportColumnMapping;
import ch.infbr5.sentinel.server.ws.importer.modification.ModificationDto;

import com.google.common.collect.Lists;

@WebService(name = "PersonenImporterService", targetNamespace = "http://ws.sentinel.infbr5.ch/")
@HandlerChain(file = "/META-INF/ws-handler-chain.xml")
@SOAPBinding(style = SOAPBinding.Style.RPC)
public class PersonenImporterService {

	private static Logger log = Logger.getLogger(PersonenImporterService.class.getName()); 
	
	@WebMethod
	public String initiatImport(@WebParam(name = "filename") String filename, @WebParam(name = "data") byte[] data, @WebParam(name = "isKompletterBestand") boolean isKompletterBestand) {
		// Create key
		String sessionKey = createNewUniqueSessionKey();

		// Save data file
		String tmpFileData = getFileData(sessionKey, FileHelper.getExtension(filename));
		FileHelper.saveAsFile(tmpFileData, data);
		
		// Persist State
		PersonenImporterStatePersister persister = createPersister(sessionKey);
		persister.getState().setFilenameData(tmpFileData);
		persister.getState().setKompletterBestand(isKompletterBestand);
		persister.save();
		
		return sessionKey;
	}
	
	@WebMethod
	public String fileHasMinimalRequirements(@WebParam(name = "sessionKey") String sessionKey) {
		PersonenImporterStatePersister persister = createPersister(sessionKey);
		PersonenImporter importer = createImporter(persister);
		
		String result = importer.fileHasMinimalRequirements();
		if (result == null) {
			return "";
		} else {
			return result;
		}
	}
	
	@WebMethod
	public ColumnMappingResponse getColumnMappings(@WebParam(name = "sessionKey") String sessionKey) {
		PersonenImporterStatePersister persister = createPersister(sessionKey);
		PersonenImporter importer = createImporter(persister);
		
		// Falls keine Mappings vorhanden sind, dann erstelle Mappings
		if (importer.getColumnMappings().isEmpty()) {
			importer.calculateColumnMappings();
		}
		
		// Importer Status speichern
		saveState(persister, importer);
		
		// Response
		ColumnMappingResponse response = new ColumnMappingResponse();
		response.setColumns(toArrayColumn(importer.getColumns()));
		response.setMappings(toArrayMappings(importer.getColumnMappings()));
		
		return response;
	}

	@WebMethod
	public void setColumnMappings(@WebParam(name = "sessionKey") String sessionKey, @WebParam(name = "mappings") PersonenImportColumnMapping[] mappings) {
		PersonenImporterStatePersister persister = createPersister(sessionKey);
		
		if (hasColumnMappingsChanged(persister.getState().getColumnMappings(), mappings)) {
			PersonenImporter importer = createImporter(persister);
			importer.setColumnMappings(toListMappings(mappings));
			if (importer.isValidImportData()) {
				importer.calculateModifications();
			}
			saveState(persister, importer);
		}
	}
	
	@WebMethod
	public ModificationDto getModifications(@WebParam(name = "sessionKey") String sessionKey) {
		PersonenImporterStatePersister persister = createPersister(sessionKey);
		PersonenImporter importer = createImporter(persister);
		
		// Modifikationen berechnen, falls keine vorhanden sind
		if (importer.getModifications() == null || !importer.getModifications().hasModifications()) {
			importer.calculateModifications();
		}
		
		saveState(persister, importer);
		
		return importer.getModifications();
	}
	
	@WebMethod
	public void setModifications(@WebParam(name = "sessionKey") String sessionKey, @WebParam(name = "modificationDto") ModificationDto modificationDto) {
		PersonenImporterStatePersister persister = createPersister(sessionKey);
		persister.getState().setModifications(modificationDto);
		persister.save();
	}
	
	@WebMethod
	public boolean startImport(@WebParam(name = "sessionKey") String sessionKey) {
		PersonenImporterStatePersister persister = createPersister(sessionKey);
		PersonenImporter importer = createImporter(persister);
		
		if (importer.isValidImportData()) {
			importer.importData();
			cleanup(sessionKey);
			return true;
		} else {
			log.warning("Daten konnten nicht importiert werden, da der Import ungültig ist.");
			return false;
		}
	}
	
	@WebMethod
	public String[] getSupportedExtensions() {
		return PersonenImporter.SUPPORTED_EXTENSIONS;
	}
	
	@WebMethod
	public void abortImport(@WebParam(name = "sessionKey") String sessionKey) {
		log.info("abort import - sessionkey " + sessionKey);
		cleanup(sessionKey);
	}
	
	private void cleanup(String sessionKey) {
		PersonenImporterStatePersister persister = createPersister(sessionKey);
		String filenameData = persister.getState().getFilenameData();
		if (filenameData != null) {
			File file = new File(filenameData);
			if (file.exists()) {
				file.delete();
			}
		}
		persister.remove();
	}
	
	private String createNewUniqueSessionKey() {
		String sessionKey = UUID.randomUUID().toString();
		log.info("created new session key: " + sessionKey);
		return sessionKey;
	}
	
	private PersonenImportColumn[] toArrayColumn(List<PersonenImportColumn> list) {
		return list.toArray(new PersonenImportColumn[list.size()]);
	}
	
	private PersonenImportColumnMapping[] toArrayMappings(List<PersonenImportColumnMapping> list) {
		return list.toArray(new PersonenImportColumnMapping[list.size()]);
	}
	
	private List<PersonenImportColumnMapping> toListMappings(PersonenImportColumnMapping[] array) {
		 return Lists.newArrayList(array);
	}
	
	private PersonenImporter createImporter(PersonenImporterStatePersister persister) {
		PersonenImporter importer = new PersonenImporter(persister.getState().getFilenameData(), persister.getState().isKompletterBestand());
		importer.setColumnMappings(toListMappings(persister.getState().getColumnMappings()));
		importer.setModifications(persister.getState().getModifications());
		return importer;
	}
	
	private PersonenImporterStatePersister createPersister(String sessionKey) {
		return new PersonenImporterStatePersister(getFileState(sessionKey));
	}
	
	private void saveState(PersonenImporterStatePersister persister, PersonenImporter importer) {
		persister.getState().setFilenameData(importer.getFilenameData());
		persister.getState().setKompletterBestand(importer.isKompletterBestand());
		persister.getState().setColumns(toArrayColumn(importer.getColumns()));
		persister.getState().setColumnMappings(toArrayMappings(importer.getColumnMappings()));
		persister.getState().setModifications(importer.getModifications());
		persister.save();
	}
	
	private boolean hasMappingChanged(PersonenImportColumnMapping[] mapping1, PersonenImportColumnMapping[] mapping2) {
		boolean hasChanged = false;
		for (PersonenImportColumnMapping m1 : mapping1) {
			boolean foundOuterMapping = false;
			for (PersonenImportColumnMapping m2 : mapping2) {
				if (m1.getPersonenAttribute().equals(m2.getPersonenAttribute())) {
					if (m1.getColumn() == null && m2.getColumn() == null) {
						foundOuterMapping = true;
					} else if (m1.getColumn() == null || m2.getColumn() == null) {
						
					} else if (m1.getColumn().getIndex() == m2.getColumn().getIndex()) {
						foundOuterMapping = true;
					}
				}
			}
			if (!foundOuterMapping) {
				hasChanged = true;
			}
		}
		return hasChanged;
	}
	
	private boolean hasColumnMappingsChanged(PersonenImportColumnMapping[] mapping1, PersonenImportColumnMapping[] mapping2) {
		return (hasMappingChanged(mapping1, mapping2)) || (hasMappingChanged(mapping2, mapping1));
	}
	
	private String getFileState(String sessionKey) {
		return "import/" + sessionKey + "-state" + ".xml";
	}
	
	private String getFileData(String sessionKey, String extension) {
		return "import/" + sessionKey + "-data." + extension;
	}
	
}
