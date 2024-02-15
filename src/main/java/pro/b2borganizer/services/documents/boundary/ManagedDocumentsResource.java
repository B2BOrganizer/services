package pro.b2borganizer.services.documents.boundary;

import java.text.MessageFormat;
import java.time.LocalDate;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.client.model.Filters;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.Headers;
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
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;
import pro.b2borganizer.services.documents.control.ManagedDocumentRepository;
import pro.b2borganizer.services.documents.entity.ManagedDocument;
import pro.b2borganizer.services.documents.entity.ManagedDocumentsFilter;
import pro.b2borganizer.services.documents.entity.UpdateManagedDocumentFields;
import pro.b2borganizer.services.documents.entity.UpdatedManagedDocument;
import pro.b2borganizer.services.mails.entity.MailMessage;

@RestController
@RequestMapping("/managed-documents")
@RequiredArgsConstructor
@Slf4j
public class ManagedDocumentsResource {

    private final ManagedDocumentRepository managedDocumentRepository;

    private final MongoTemplate mongoTemplate;

    private final ObjectMapper objectMapper;

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

        int[] ranges = objectMapper.readValue(range, int[].class);
        int pageSize = ranges[1] - ranges[0];
        int pageNumber = ranges[0] / pageSize;

        ManagedDocumentsFilter managedDocumentsFilter = objectMapper.readValue(filter, ManagedDocumentsFilter.class);

        log.info("Find all managed documents {} {} {}.", sort, ranges, managedDocumentsFilter);

        PageRequest pageRequest = PageRequest.of(pageNumber, pageSize);

        Query query = new Query();

        Criteria criteria = new Criteria();
        if (managedDocumentsFilter.getAssignedToYear() != null) {
            criteria.and("assignedToYear").is(managedDocumentsFilter.getAssignedToYear());
        }
        if (managedDocumentsFilter.getAssignedToMonth() != null) {
            criteria.and("assignedToMonth").is(managedDocumentsFilter.getAssignedToMonth());
        }
        query.addCriteria(criteria);

        long totalElements = mongoTemplate.count(query, ManagedDocument.class);

        List<ManagedDocument> result = mongoTemplate.find(query.with(pageRequest), ManagedDocument.class);

        return ResponseEntity
                .status(HttpStatus.OK)
                .header("Content-Range", String.format("managed-documents %s-%s/%s", ranges[0], ranges[1], totalElements))
                .body(result);
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
