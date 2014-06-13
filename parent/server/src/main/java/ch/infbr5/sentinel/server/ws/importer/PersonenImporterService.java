package ch.infbr5.sentinel.server.ws.importer;

import java.util.ArrayList;
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
		String tmpFileData = "import/" + sessionKey + "-data." + FileHelper.getExtension(filename);
		FileHelper.saveAsFile(tmpFileData, data);

		// Calculate Mapping Columns
		PersonenImporter importer = new PersonenImporter(tmpFileData, isKompletterBestand);
		List<MappingPersonenAttributeToColumn> columnMappings = importer.calculateColumnMappings();
		List<Column> columns = importer.getColumns();
		
		// Persist State
		PersonenImporterStatePersister persister = new PersonenImporterStatePersister(sessionKey);
		persister.getState().setFilenameData(tmpFileData);
		persister.getState().setMappingColumns(toArrayMappings(columnMappings));
		persister.getState().setColumns(toArrayColumn(columns));
		persister.getState().setKompletterBestand(isKompletterBestand);
		persister.save();
		
		return sessionKey;
	}
	
	@WebMethod
	public boolean startImport(@WebParam(name = "sessionKey") String sessionKey) {
		PersonenImporterStatePersister persister = new PersonenImporterStatePersister(sessionKey);
		PersonenImporter importer = new PersonenImporter(persister.getState().getFilenameData(), persister.getState().isKompletterBestand());
		importer.setMappings(toListMappings(persister.getState().getMappingColumns()));
		
		if (importer.isValidImportData()) {
			importer.importData();
			return true;
		} else {
			log.warning("Daten konnten nicht importiert werden, da der Import ungültig ist.");
			return false;
		}
	}
	
	@WebMethod
	public ColumnMappingResponse getColumnMappings(@WebParam(name = "sessionKey") String sessionKey) {
		PersonenImporterStatePersister persister = new PersonenImporterStatePersister(sessionKey);
		
		ColumnMappingResponse response = new ColumnMappingResponse();
		response.setColumns(persister.getState().getColumns());
		response.setMappings(persister.getState().getMappingColumns());
		
		return response;
	}
	
	@WebMethod
	public void setColumnMappings(@WebParam(name = "sessionKey") String sessionKey, MappingPersonenAttributeToColumn[] mappings) {
		PersonenImporterStatePersister persister = new PersonenImporterStatePersister(sessionKey);
		persister.getState().setMappingColumns(mappings);
		persister.save();
	}
	
	private String createNewUniqueSessionKey() {
		String sessionKey = UUID.randomUUID().toString();
		log.info("created new session key: " + sessionKey);
		return sessionKey;
	}
	
	private Column[] toArrayColumn(List<Column> list) {
		return list.toArray(new Column[list.size()]);
	}
	
	private MappingPersonenAttributeToColumn[] toArrayMappings(List<MappingPersonenAttributeToColumn> list) {
		return list.toArray(new MappingPersonenAttributeToColumn[list.size()]);
	}
	
	private List<MappingPersonenAttributeToColumn> toListMappings(MappingPersonenAttributeToColumn[] array) {
		 List<MappingPersonenAttributeToColumn> list = new ArrayList<>();
		 for (MappingPersonenAttributeToColumn m : array) {
			 list.add(m);
		 }
		 return list;
	}
	
}
