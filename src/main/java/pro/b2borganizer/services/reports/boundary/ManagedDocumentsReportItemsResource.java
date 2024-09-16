package pro.b2borganizer.services.reports.boundary;

import java.util.List;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pro.b2borganizer.services.common.control.SimpleRestProviderFilter;
import pro.b2borganizer.services.common.control.SimpleRestProviderQueryParser;
import pro.b2borganizer.services.common.control.SimpleRestProviderRepository;
import pro.b2borganizer.services.common.control.SimpleRestProviderResponseBuilder;
import pro.b2borganizer.services.reports.entity.ManagedDocumentsReportItem;

@RestController
@RequestMapping("/managed-documents-report-items")
@Slf4j
@RequiredArgsConstructor
public class ManagedDocumentsReportItemsResource {

    private final SimpleRestProviderQueryParser simpleRestProviderQueryParser;

    private final SimpleRestProviderRepository simpleRestProviderRepository;

    private final SimpleRestProviderResponseBuilder simpleRestProviderResponseBuilder;

    @GetMapping(consumes = "application/json+simpleRestProvider")
    public ResponseEntity<List<ManagedDocumentsReportItem>> findAllDocuments(@RequestParam(required = false) String sort,
                                                                            @RequestParam(required = false) String range,
                                                                            @RequestParam(required = false) String filter) {

        SimpleRestProviderQueryParser.InputParameters inputParameters = new SimpleRestProviderQueryParser.InputParameters("managed-documents-report-items", sort, range, filter);

        SimpleRestProviderFilter simpleRestProviderFilter = simpleRestProviderQueryParser.parse(inputParameters);

        SimpleRestProviderRepository.SimpleRestProviderQueryListResult<ManagedDocumentsReportItem> result = simpleRestProviderRepository.findByQuery(simpleRestProviderFilter, ManagedDocumentsReportItem.class);

        return simpleRestProviderResponseBuilder.buildListResponse(simpleRestProviderFilter, result);
    }
}
