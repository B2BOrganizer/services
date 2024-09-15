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
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
import pro.b2borganizer.services.reports.control.ManagedDocumentsReportMapper;
import pro.b2borganizer.services.reports.control.ManagedDocumentsReportRepository;
import pro.b2borganizer.services.reports.entity.ManagedDocumentsReport;
import pro.b2borganizer.services.reports.entity.ManagedDocumentsReportItem;
import pro.b2borganizer.services.reports.entity.ManagedDocumentsReportItemsFilter;
import pro.b2borganizer.services.reports.entity.NewManagedDocumentsReport;

@RestController
@RequestMapping("/managed-documents-report-items")
@Slf4j
@RequiredArgsConstructor
public class ManagedDocumentsReportItemsResource {

    private final SimpleRestProviderQueryParser simpleRestProviderQueryParser;

    private final SimpleRestProviderRepository simpleRestProviderRepository;

    private final SimpleRestProviderResponseBuilder simpleRestProviderResponseBuilder;

    private final ManagedDocumentsReportMapper managedDocumentsReportMapper;

    private final ManagedDocumentsReportRepository managedDocumentsReportRepository;

    private final RequiredDocumentsRepository requiredDocumentsRepository;

    private final ManagedDocumentRepository managedDocumentRepository;

    @GetMapping(consumes = "application/json+simpleRestProvider")
    public ResponseEntity<List<ManagedDocumentsReportItem>> findAllDocuments(@RequestParam(required = false) String sort,
                                                                            @RequestParam(required = false) String range,
                                                                            @RequestParam(required = false) String filter) {

        SimpleRestProviderQueryParser.InputParameters inputParameters = new SimpleRestProviderQueryParser.InputParameters("managed-documents-report-items", sort, range, filter);

        SimpleRestProviderFilter<ManagedDocumentsReportItemsFilter> simpleRestProviderFilter = simpleRestProviderQueryParser.parse(inputParameters, ManagedDocumentsReportItemsFilter.class);

        SimpleRestProviderRepository.SimpleRestProviderQueryListResult<ManagedDocumentsReportItem> result = simpleRestProviderRepository.findByQuery(simpleRestProviderFilter, ManagedDocumentsReportItem.class);

        return simpleRestProviderResponseBuilder.buildListResponse(simpleRestProviderFilter, result);
    }
}
