package pro.b2borganizer.services.documents.boundary;

import java.text.MessageFormat;
import java.time.LocalDate;
import java.util.List;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import pro.b2borganizer.services.documents.control.ManagedDocumentRepository;
import pro.b2borganizer.services.documents.entity.ManagedDocument;
import pro.b2borganizer.services.documents.entity.UpdateManagedDocumentFields;
import pro.b2borganizer.services.mails.entity.MailMessage;

@RestController
@RequestMapping("/managed-documents")
@RequiredArgsConstructor
@Slf4j
public class ManagedDocumentsResource {

    private final ManagedDocumentRepository managedDocumentRepository;

    private final MongoTemplate mongoTemplate;

    @GetMapping
    public List<ManagedDocument> findAll(@RequestParam(required = false) LocalDate from,
                                         @RequestParam(required = false) LocalDate to) {

        log.info("Find all managed documents from {} to {}.", from, to);

        Query query = new Query();

        if (from != null && to != null) {
            query.addCriteria(Criteria.where("received").gte(from).lt(to));
        } else if (from != null) {
            query.addCriteria(Criteria.where("received").gte(from));
        } else if (to != null) {
            query.addCriteria(Criteria.where("received").lt(to));
        }

        return mongoTemplate.find(query, ManagedDocument.class);
    }

    @RequestMapping("/{id}")
    @PatchMapping
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
