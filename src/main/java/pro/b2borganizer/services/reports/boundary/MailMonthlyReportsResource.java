package pro.b2borganizer.services.reports.boundary;

import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pro.b2borganizer.services.common.control.SimpleRestProviderFilter;
import pro.b2borganizer.services.common.control.SimpleRestProviderQueryParser;
import pro.b2borganizer.services.common.control.SimpleRestProviderRepository;
import pro.b2borganizer.services.common.control.SimpleRestProviderResponseBuilder;
import pro.b2borganizer.services.reports.control.MailMonthlyReportRepository;
import pro.b2borganizer.services.reports.entity.MailMonthlyReportCreatedEvent;
import pro.b2borganizer.services.reports.entity.MailMonthlyReportsFilter;
import pro.b2borganizer.services.reports.entity.NewMailMonthlyReport;
import pro.b2borganizer.services.reports.control.MailMonthlyReportMapper;
import pro.b2borganizer.services.reports.entity.MailMonthlyReport;

@RestController
@RequestMapping("/mail-monthly-reports")
@Slf4j
@RequiredArgsConstructor
public class MailMonthlyReportsResource {

    private final MailMonthlyReportRepository mailMonthlyReportRepository;

    private final ApplicationEventPublisher applicationEventPublisher;

    private final MailMonthlyReportMapper mailMonthlyReportMapper;

    private final ObjectMapper objectMapper;

    private final SimpleRestProviderQueryParser simpleRestProviderQueryParser;

    private final SimpleRestProviderRepository simpleRestProviderRepository;

    private final SimpleRestProviderResponseBuilder simpleRestProviderResponseBuilder;

    @PostMapping
    public ResponseEntity<MailMonthlyReport> create(@Valid @RequestBody NewMailMonthlyReport newMailMonthlyReport) {
        return createInternal(newMailMonthlyReport);
    }

    private ResponseEntity<MailMonthlyReport> createInternal(NewMailMonthlyReport newMailMonthlyReport) {
        log.info("Creating new email monthly report = {}.", newMailMonthlyReport);

        MailMonthlyReport mailMonthlyReport = mailMonthlyReportMapper.map(newMailMonthlyReport);

        MailMonthlyReport saved = mailMonthlyReportRepository.save(mailMonthlyReport);

        log.info("New email monthly report created = {}.", mailMonthlyReport);

        applicationEventPublisher.publishEvent(MailMonthlyReportCreatedEvent.builder().mailMonthlyReportId(saved.getId()).build());

        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @PostMapping(consumes = "application/json+simpleRestProvider")
    public ResponseEntity<MailMonthlyReport> create(@Valid @RequestBody String data) throws JsonProcessingException {
        log.info("Creating new email monthly report = {}.", data);

        return createInternal(objectMapper.readValue(data, NewMailMonthlyReport.class));
    }

    @GetMapping(consumes = "application/json+simpleRestProvider")
    public ResponseEntity<List<MailMonthlyReport>> findAll(@RequestParam(required = false) String sort,
                                                           @RequestParam(required = false) String range,
                                                           @RequestParam(required = false) String filter) {

        SimpleRestProviderQueryParser.InputParameters inputParameters = new SimpleRestProviderQueryParser.InputParameters("mail-monthly-reports", sort, range, filter);

        SimpleRestProviderFilter<MailMonthlyReportsFilter> simpleRestProviderFilter = simpleRestProviderQueryParser.parse(inputParameters, MailMonthlyReportsFilter.class);

        SimpleRestProviderRepository.SimpleRestProviderQueryListResult<MailMonthlyReport> result = simpleRestProviderRepository.findByQuery(simpleRestProviderFilter, MailMonthlyReport.class);

        return simpleRestProviderResponseBuilder.buildListResponse(simpleRestProviderFilter, result);
    }
}
