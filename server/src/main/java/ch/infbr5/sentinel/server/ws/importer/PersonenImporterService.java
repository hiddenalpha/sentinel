package ch.infbr5.sentinel.server.ws.importer;

import java.io.File;
import java.util.List;
import java.util.UUID;

import javax.annotation.Resource;
import javax.jws.HandlerChain;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.persistence.EntityManager;
import javax.xml.ws.WebServiceContext;

import org.apache.log4j.Logger;

import ch.infbr5.sentinel.server.db.EntityManagerHelper;
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

   private static Logger log = Logger.getLogger(PersonenImporterService.class);

   @Resource
   private WebServiceContext context;

   @WebMethod
   public String initiatImport(@WebParam(name = "filename") final String filename,
         @WebParam(name = "data") final byte[] data,
         @WebParam(name = "isKompletterBestand") final boolean isKompletterBestand) {
      // Create key
      final String sessionKey = createNewUniqueSessionKey();

      // Save data file
      final String tmpFileData = getFileData(sessionKey, FileHelper.getExtension(filename));
      FileHelper.saveAsFile(tmpFileData, data);

      // Persist State
      final PersonenImporterStatePersister persister = createPersister(sessionKey);
      persister.getState().setFilenameData(tmpFileData);
      persister.getState().setKompletterBestand(isKompletterBestand);
      persister.save();

      log.trace("initiat new import sessionkey " + sessionKey + " kompletterbestand " + isKompletterBestand);

      return sessionKey;
   }

   @WebMethod
   public String fileHasMinimalRequirements(@WebParam(name = "sessionKey") final String sessionKey) {
      final PersonenImporterStatePersister persister = createPersister(sessionKey);
      final PersonenImporter importer = createImporter(persister);

      final String result = importer.fileHasMinimalRequirements();
      if (result == null) {
         return "";
      } else {
         return result;
      }
   }

   @WebMethod
   public ColumnMappingResponse getColumnMappings(@WebParam(name = "sessionKey") final String sessionKey) {
      final PersonenImporterStatePersister persister = createPersister(sessionKey);
      final PersonenImporter importer = createImporter(persister);

      // Falls keine Mappings vorhanden sind, dann erstelle Mappings
      if (importer.getColumnMappings().isEmpty()) {
         importer.calculateColumnMappings();
      }

      // Importer Status speichern
      saveState(persister, importer);

      // Response
      final ColumnMappingResponse response = new ColumnMappingResponse();
      response.setColumns(toArrayColumn(importer.getColumns()));
      response.setMappings(toArrayMappings(importer.getColumnMappings()));

      return response;
   }

   @WebMethod
   public void setColumnMappings(@WebParam(name = "sessionKey") final String sessionKey,
         @WebParam(name = "mappings") final PersonenImportColumnMapping[] mappings) {
      final PersonenImporterStatePersister persister = createPersister(sessionKey);

      if (hasColumnMappingsChanged(persister.getState().getColumnMappings(), mappings)) {
         final PersonenImporter importer = createImporter(persister);
         importer.setColumnMappings(toListMappings(mappings));
         if (importer.isValidImportData()) {
            importer.calculateModifications();
         }
         saveState(persister, importer);
      }
   }

   @WebMethod
   public ModificationDto getModifications(@WebParam(name = "sessionKey") final String sessionKey) {
      final PersonenImporterStatePersister persister = createPersister(sessionKey);
      final PersonenImporter importer = createImporter(persister);

      // Modifikationen berechnen, falls keine vorhanden sind
      if (importer.getModifications() == null || !importer.getModifications().hasModifications()) {
         importer.calculateModifications();
      }

      saveState(persister, importer);

      return importer.getModifications();
   }

   @WebMethod
   public void setModifications(@WebParam(name = "sessionKey") final String sessionKey,
         @WebParam(name = "modificationDto") final ModificationDto modificationDto) {
      final PersonenImporterStatePersister persister = createPersister(sessionKey);
      persister.getState().setModifications(modificationDto);
      persister.save();
   }

   @WebMethod
   public boolean startImport(@WebParam(name = "sessionKey") final String sessionKey) {
      final PersonenImporterStatePersister persister = createPersister(sessionKey);
      final PersonenImporter importer = createImporter(persister);

      if (importer.isValidImportData()) {
         importer.importData();
         cleanup(sessionKey);
         return true;
      } else {
         log.warn("Daten konnten nicht importiert werden, da der Import ungueltig ist.");
         return false;
      }
   }

   @WebMethod
   public String[] getSupportedExtensions() {
      return PersonenImporter.SUPPORTED_EXTENSIONS;
   }

   @WebMethod
   public void abortImport(@WebParam(name = "sessionKey") final String sessionKey) {
      log.debug("abort import - sessionkey " + sessionKey);
      cleanup(sessionKey);
   }

   private void cleanup(final String sessionKey) {
      final PersonenImporterStatePersister persister = createPersister(sessionKey);
      final String filenameData = persister.getState().getFilenameData();
      if (filenameData != null) {
         final File file = new File(filenameData);
         if (file.exists()) {
            file.delete();
         }
      }
      persister.remove();
   }

   private String createNewUniqueSessionKey() {
      final String sessionKey = UUID.randomUUID().toString();
      log.trace("created new session key: " + sessionKey);
      return sessionKey;
   }

   private PersonenImportColumn[] toArrayColumn(final List<PersonenImportColumn> list) {
      return list.toArray(new PersonenImportColumn[list.size()]);
   }

   private PersonenImportColumnMapping[] toArrayMappings(final List<PersonenImportColumnMapping> list) {
      return list.toArray(new PersonenImportColumnMapping[list.size()]);
   }

   private List<PersonenImportColumnMapping> toListMappings(final PersonenImportColumnMapping[] array) {
      return Lists.newArrayList(array);
   }

   private PersonenImporter createImporter(final PersonenImporterStatePersister persister) {
      final PersonenImporter importer = new PersonenImporter(getEntityManager(),
            persister.getState().getFilenameData(), persister.getState().isKompletterBestand());
      importer.setColumnMappings(toListMappings(persister.getState().getColumnMappings()));
      importer.setModifications(persister.getState().getModifications());
      return importer;
   }

   private PersonenImporterStatePersister createPersister(final String sessionKey) {
      return new PersonenImporterStatePersister(getFileState(sessionKey));
   }

   private void saveState(final PersonenImporterStatePersister persister, final PersonenImporter importer) {
      persister.getState().setFilenameData(importer.getFilenameData());
      persister.getState().setKompletterBestand(importer.isKompletterBestand());
      persister.getState().setColumns(toArrayColumn(importer.getColumns()));
      persister.getState().setColumnMappings(toArrayMappings(importer.getColumnMappings()));
      persister.getState().setModifications(importer.getModifications());
      persister.save();
   }

   private boolean hasMappingChanged(final PersonenImportColumnMapping[] mapping1,
         final PersonenImportColumnMapping[] mapping2) {
      boolean hasChanged = false;
      for (final PersonenImportColumnMapping m1 : mapping1) {
         boolean foundOuterMapping = false;
         for (final PersonenImportColumnMapping m2 : mapping2) {
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

   private boolean hasColumnMappingsChanged(final PersonenImportColumnMapping[] mapping1,
         final PersonenImportColumnMapping[] mapping2) {
      return (hasMappingChanged(mapping1, mapping2)) || (hasMappingChanged(mapping2, mapping1));
   }

   private String getFileState(final String sessionKey) {
      return "import/" + sessionKey + "-state" + ".xml";
   }

   private String getFileData(final String sessionKey, final String extension) {
      return "import/" + sessionKey + "-data." + extension;
   }

   private EntityManager getEntityManager() {
      return EntityManagerHelper.getEntityManager(context);
   }

}
