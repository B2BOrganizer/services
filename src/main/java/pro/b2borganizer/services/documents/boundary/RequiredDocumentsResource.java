package pro.b2borganizer.services.documents.boundary;

import java.text.MessageFormat;
import java.util.List;
import java.util.Optional;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
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
import pro.b2borganizer.services.common.entity.SimpleFilter;
import pro.b2borganizer.services.documents.control.RequiredMonthlyDocumentsMapper;
import pro.b2borganizer.services.documents.control.RequiredDocumentsRepository;
import pro.b2borganizer.services.documents.entity.NewRequiredDocument;
import pro.b2borganizer.services.documents.entity.RequiredDocument;
import pro.b2borganizer.services.documents.entity.UpdatedRequiredDocument;
import pro.b2borganizer.services.reports.entity.ManagedDocumentsReport;

@RestController
@RequestMapping("/required-documents")
@Slf4j
@RequiredArgsConstructor
public class RequiredDocumentsResource {

    private final SimpleRestProviderQueryParser simpleRestProviderQueryParser;

    private final SimpleRestProviderRepository simpleRestProviderRepository;

    private final SimpleRestProviderResponseBuilder simpleRestProviderResponseBuilder;

    private final RequiredMonthlyDocumentsMapper requiredMonthlyDocumentsMapper;

    private final RequiredDocumentsRepository requiredDocumentsRepository;

    @PostMapping
    public ResponseEntity<RequiredDocument> create(@Valid @RequestBody NewRequiredDocument newRequiredDocument) {
        return createInternal(newRequiredDocument);
    }

    private ResponseEntity<RequiredDocument> createInternal(NewRequiredDocument newRequiredDocument) {
        RequiredDocument requiredDocument = requiredMonthlyDocumentsMapper.map(newRequiredDocument);

        RequiredDocument saved = requiredDocumentsRepository.save(requiredDocument);

        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @PostMapping(consumes = "application/json+simpleRestProvider")
    public ResponseEntity<RequiredDocument> create(@Valid @RequestBody String data) {
        NewRequiredDocument newRequiredDocument = simpleRestProviderQueryParser.parse(data, NewRequiredDocument.class);

        return createInternal(newRequiredDocument);
    }

    @GetMapping(consumes = "application/json+simpleRestProvider")
    public ResponseEntity<List<RequiredDocument>> findAll(@RequestParam(required = false) String sort,
                                                          @RequestParam(required = false) String range,
                                                          @RequestParam(required = false) String filter) {

        SimpleRestProviderQueryParser.InputParameters inputParameters = new SimpleRestProviderQueryParser.InputParameters("required-monthly-documents", sort, range, filter);

        SimpleRestProviderFilter simpleRestProviderFilter = simpleRestProviderQueryParser.parse(inputParameters);

        SimpleRestProviderRepository.SimpleRestProviderQueryListResult<RequiredDocument> result = simpleRestProviderRepository.findByQuery(simpleRestProviderFilter, RequiredDocument.class);

        return simpleRestProviderResponseBuilder.buildListResponse(simpleRestProviderFilter, result);
    }

    @GetMapping(consumes = "application/json+simpleRestProvider", value = "/{id}")
    public ResponseEntity<RequiredDocument> getOne(@PathVariable String id) {
        Optional<RequiredDocument> found = simpleRestProviderRepository.getOne(id, RequiredDocument.class);

        return found.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping(consumes = "application/json+simpleRestProvider", value = "/{id}")
    public ResponseEntity<Void> deleteOne(@PathVariable String id) {
        log.info("Deleting required document with id = {}.", id);
        RequiredDocument requiredDocument = requiredDocumentsRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, MessageFormat.format("Entity = {0} with id = {1} has not been found!", RequiredDocument.class, id)));

        requiredDocumentsRepository.delete(requiredDocument);

        log.info("Deleted required document with id = {}.", id);

        return ResponseEntity.noContent().build();
    }

    @PutMapping(value = "/{id}", consumes = "application/json+simpleRestProvider")
    public RequiredDocument update(@PathVariable(value = "id") String id, @RequestBody String updated) {
        UpdatedRequiredDocument updatedRequiredDocument = simpleRestProviderQueryParser.parse(updated, UpdatedRequiredDocument.class);

        log.info("Updating = {} {}.", id, updatedRequiredDocument);

        RequiredDocument requiredDocument = requiredDocumentsRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, MessageFormat.format("Entity = {0} with id = {1} has not been found!", RequiredDocument.class, id)));

        RequiredDocument toUpdate = requiredMonthlyDocumentsMapper.map(updatedRequiredDocument, requiredDocument);

        return requiredDocumentsRepository.save(toUpdate);
    }

}
