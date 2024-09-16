package pro.b2borganizer.services.documents.boundary;

import java.text.MessageFormat;
import java.time.LocalDate;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import pro.b2borganizer.services.common.control.SimpleRestProviderFilter;
import pro.b2borganizer.services.common.control.SimpleRestProviderQueryParser;
import pro.b2borganizer.services.common.control.SimpleRestProviderRepository;
import pro.b2borganizer.services.common.control.SimpleRestProviderResponseBuilder;
import pro.b2borganizer.services.documents.control.ManagedDocumentRepository;
import pro.b2borganizer.services.documents.entity.ManagedDocument;
import pro.b2borganizer.services.documents.entity.UpdateManagedDocumentFields;
import pro.b2borganizer.services.documents.entity.UpdatedManagedDocument;

@RestController
@RequestMapping("/managed-documents")
@RequiredArgsConstructor
@Slf4j
public class ManagedDocumentsResource {

    private final ManagedDocumentRepository managedDocumentRepository;

    private final MongoTemplate mongoTemplate;

    private final ObjectMapper objectMapper;

    private final SimpleRestProviderQueryParser simpleRestProviderQueryParser;

    private final SimpleRestProviderRepository simpleRestProviderRepository;

    private final SimpleRestProviderResponseBuilder simpleRestProviderResponseBuilder;

    @GetMapping
    public ResponseEntity<List<ManagedDocument>> findAll(@RequestParam(required = false) LocalDate from, @RequestParam(required = false) LocalDate to) {
        log.info("Find all managed documents from {} to {}.", from, to);

        Query query = new Query();

        if (from != null && to != null) {
            query.addCriteria(Criteria.where("received").gte(from).lt(to));
        } else if (from != null) {
            query.addCriteria(Criteria.where("received").gte(from));
        } else if (to != null) {
            query.addCriteria(Criteria.where("received").lt(to));
        }

        List<ManagedDocument> managedDocuments = mongoTemplate.find(query, ManagedDocument.class);

        return ResponseEntity
                .status(HttpStatus.OK)
                .header("Content-Range", "managed-documents 30")
                .body(managedDocuments);
    }

    @GetMapping(consumes = "application/json+simpleRestProvider")
    public ResponseEntity<List<ManagedDocument>> findAll(@RequestParam(required = false) String sort,
                                                         @RequestParam(required = false) String range,
                                                         @RequestParam(required = false) String filter) throws JsonProcessingException {

        log.info("Find all managed documents sort = {}, range = {}, filter = {}.", sort, range, filter);

        SimpleRestProviderQueryParser.InputParameters inputParameters = new SimpleRestProviderQueryParser.InputParameters("managed-documents", sort, range, filter);

        SimpleRestProviderFilter simpleRestProviderFilter = simpleRestProviderQueryParser.parse(inputParameters);

        SimpleRestProviderRepository.SimpleRestProviderQueryListResult<ManagedDocument> result = simpleRestProviderRepository.findByQuery(simpleRestProviderFilter, ManagedDocument.class);

        return simpleRestProviderResponseBuilder.buildListResponse(simpleRestProviderFilter, result);
    }

    @GetMapping("/{id}")
    public ManagedDocument get(@PathVariable(value = "id") String id) {
        log.info("Get managed document = {}", id);

        return managedDocumentRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, MessageFormat.format("Managed document with id = {0} not found.", id)));
    }

    @PutMapping(value = "/{id}", consumes = "application/json+simpleRestProvider")
    public ManagedDocument update(@PathVariable(value = "id") String id, @RequestBody String updated) throws JsonProcessingException {
        UpdatedManagedDocument updatedManagedDocument = objectMapper.readValue(updated, UpdatedManagedDocument.class);

        log.info("Updating managed document = {} {}.", id, updatedManagedDocument);

        ManagedDocument managedDocument = managedDocumentRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, MessageFormat.format("Managed document with id = {0} not found.", id)));

        managedDocument.setAssignedToMonth(updatedManagedDocument.getAssignedToMonth());
        managedDocument.setAssignedToYear(updatedManagedDocument.getAssignedToYear());
        managedDocument.setComment(updatedManagedDocument.getComment());
        managedDocument.setRequiredDocumentId(updatedManagedDocument.getRequiredDocumentId());
        managedDocument.setRequiredDocumentSelectionType(updatedManagedDocument.getRequiredDocumentSelectionType());

        return managedDocumentRepository.save(managedDocument);
    }

    @DeleteMapping(value = "/{id}", consumes = "application/json+simpleRestProvider")
    public void delete(@PathVariable(value = "id") String id) throws JsonProcessingException {
        log.info("Deleting managed document = {}.", id);

        ManagedDocument managedDocument = managedDocumentRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, MessageFormat.format("Managed document with id = {0} not found.", id)));

        managedDocumentRepository.delete(managedDocument);
    }

    @PatchMapping("/{id}")
    public void update(@PathVariable(value = "id") String id, @RequestBody UpdateManagedDocumentFields updateManagedDocumentFields) {
        log.info("Updating managed document = {} with fields: {}", id, updateManagedDocumentFields);

        managedDocumentRepository.findOptionalById(id)
                .ifPresentOrElse(managedDocument -> {
                    if (updateManagedDocumentFields.getComment() != null) {
                        managedDocument.setComment(updateManagedDocumentFields.getComment().get());
                    }

                    managedDocumentRepository.save(managedDocument);
                }, () -> {
                    throw new ResponseStatusException(HttpStatus.NOT_FOUND, MessageFormat.format("Managed document with id = {0} not found.", id));
                });
    }
}
