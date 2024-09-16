package pro.b2borganizer.services.reports.boundary;

import java.text.MessageFormat;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
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
import pro.b2borganizer.services.documents.control.ManagedDocumentRepository;
import pro.b2borganizer.services.documents.control.RequiredDocumentsRepository;
import pro.b2borganizer.services.documents.entity.ManagedDocument;
import pro.b2borganizer.services.documents.entity.RequiredDocument;
import pro.b2borganizer.services.reports.control.ManagedDocumentsReportItemRepository;
import pro.b2borganizer.services.reports.control.ManagedDocumentsReportMapper;
import pro.b2borganizer.services.reports.control.ManagedDocumentsReportRepository;
import pro.b2borganizer.services.reports.entity.ManagedDocumentsReport;
import pro.b2borganizer.services.reports.entity.ManagedDocumentsReportItem;
import pro.b2borganizer.services.reports.entity.NewManagedDocumentsReport;
import pro.b2borganizer.services.reports.entity.PredefinedMailMonthlyReport;

@RestController
@RequestMapping("/managed-documents-reports")
@Slf4j
@RequiredArgsConstructor
public class ManagedDocumentsReportResource {

    private final SimpleRestProviderQueryParser simpleRestProviderQueryParser;

    private final SimpleRestProviderRepository simpleRestProviderRepository;

    private final SimpleRestProviderResponseBuilder simpleRestProviderResponseBuilder;

    private final ManagedDocumentsReportMapper managedDocumentsReportMapper;

    private final ManagedDocumentsReportRepository managedDocumentsReportRepository;

    private final RequiredDocumentsRepository requiredDocumentsRepository;

    private final ManagedDocumentRepository managedDocumentRepository;

    private final ManagedDocumentsReportItemRepository managedDocumentsReportItemRepository;

    @PostMapping(consumes = "application/json+simpleRestProvider")
    public ResponseEntity<ManagedDocumentsReport> create(@Valid @RequestBody String data) throws JsonProcessingException {
        log.info("Creating new email monthly report = {}.", data);

        return createInternal(simpleRestProviderQueryParser.parse(data, NewManagedDocumentsReport.class));
    }


    private ResponseEntity<ManagedDocumentsReport> createInternal(NewManagedDocumentsReport newManagedDocumentsReport) {
        log.info("Creating new managed documents report = {}.", newManagedDocumentsReport);

        ManagedDocumentsReport managedDocumentsReport = managedDocumentsReportMapper.map(newManagedDocumentsReport);

        ManagedDocumentsReport saved = managedDocumentsReportRepository.save(managedDocumentsReport);

        Set<ManagedDocumentsReportItem> items = generateItems(managedDocumentsReport);

        managedDocumentsReportItemRepository.saveAll(items);

        log.info("New managed documents report created = {}.", managedDocumentsReport);

        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    private Set<ManagedDocumentsReportItem> generateItems(ManagedDocumentsReport managedDocumentsReport) {
        Map<String, RequiredDocument>  requiredDocuments = requiredDocumentsRepository.findAll().stream()
                .collect(Collectors.toMap(RequiredDocument::getId, requiredDocument -> requiredDocument));

        List<ManagedDocument> managedDocuments = managedDocumentRepository.findByAssignedToYearAndAssignedToMonth(managedDocumentsReport.getYear(), managedDocumentsReport.getMonth());

        Set<ManagedDocumentsReportItem> items = managedDocuments.stream().map(managedDocument -> {
            ManagedDocumentsReportItem item = new ManagedDocumentsReportItem();
            item.setManagedDocumentsReportId(managedDocumentsReport.getId());
            item.setManagedDocumentId(managedDocument.getId());
            item.setManagedDocumentReceived(managedDocument.getReceived());
            item.setManagedDocumentFileName(managedDocument.getManagedFile().getFileName());
            item.setRequiredDocumentId(managedDocument.getRequiredDocumentId());
            item.setManagedDocumentPreviews(managedDocument.getManagedFilePreviews());

            if (requiredDocuments.containsKey(managedDocument.getRequiredDocumentId())) {
                RequiredDocument requiredDocument = requiredDocuments.get(managedDocument.getRequiredDocumentId());
                item.setRequiredDocumentName(requiredDocument.getName());
            }
            return item;
        }).collect(Collectors.toSet());

        Set<String> foundRequiredDocumentsIds = items.stream()
                .filter(ManagedDocumentsReportItem::hasRequiredDocument)
                .map(ManagedDocumentsReportItem::getRequiredDocumentId)
                .collect(Collectors.toSet());

        requiredDocuments.values().stream()
                .filter(requiredDocument -> !foundRequiredDocumentsIds.contains(requiredDocument.getId()))
                .forEach(requiredDocument -> {
                    ManagedDocumentsReportItem item = new ManagedDocumentsReportItem();
                    item.setManagedDocumentsReportId(managedDocumentsReport.getId());
                    item.setManagedDocumentId(null);
                    item.setManagedDocumentReceived(null);
                    item.setManagedDocumentFileName(null);
                    item.setRequiredDocumentId(requiredDocument.getId());
                    item.setRequiredDocumentName(requiredDocument.getName());
                    item.setManagedDocumentPreviews(null);
                    items.add(item);
                });

        return items;
    }

    @GetMapping(consumes = "application/json+simpleRestProvider")
    public ResponseEntity<List<ManagedDocumentsReport>> findAll(@RequestParam(required = false) String sort,
                                                           @RequestParam(required = false) String range,
                                                           @RequestParam(required = false) String filter) {

        SimpleRestProviderQueryParser.InputParameters inputParameters = new SimpleRestProviderQueryParser.InputParameters("managed-documents-reports", sort, range, filter);

        SimpleRestProviderFilter<SimpleFilter> simpleRestProviderFilter = simpleRestProviderQueryParser.parse(inputParameters, SimpleFilter.class);

        SimpleRestProviderRepository.SimpleRestProviderQueryListResult<ManagedDocumentsReport> result = simpleRestProviderRepository.findByQuery(simpleRestProviderFilter, ManagedDocumentsReport.class);

        return simpleRestProviderResponseBuilder.buildListResponse(simpleRestProviderFilter, result);
    }

    @GetMapping(consumes = "application/json+simpleRestProvider", value = "/{id}")
    public ResponseEntity<ManagedDocumentsReport> getOne(@PathVariable String id) {
        Optional<ManagedDocumentsReport> found = simpleRestProviderRepository.getOne(id, ManagedDocumentsReport.class);

        return found.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping(consumes = "application/json+simpleRestProvider", value = "/{id}")
    public ResponseEntity<Void> deleteOne(@PathVariable String id) {
        log.info("Deleting managed documents report with id = {}.", id);

        ManagedDocumentsReport managedDocumentsReport = managedDocumentsReportRepository.findOptionalById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, MessageFormat.format("Entity = {0} with id = {1} has not been found!", ManagedDocumentsReport.class, id)));

        log.info("Deleting managed documents report items for managed documents report with id = {}.", id);
        managedDocumentsReportItemRepository.deleteByManagedDocumentsReportId(managedDocumentsReport.getId());

        managedDocumentsReportRepository.delete(managedDocumentsReport);
        log.info("Managed documents report with id = {} has been deleted.", id);

        return ResponseEntity.noContent().build();
    }
}
