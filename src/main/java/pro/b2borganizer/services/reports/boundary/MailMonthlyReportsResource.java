package pro.b2borganizer.services.reports.boundary;

import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pro.b2borganizer.services.documents.entity.ManagedDocument;
import pro.b2borganizer.services.documents.entity.ManagedDocumentsFilter;
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

    private final MongoTemplate mongoTemplate;

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
                                                           @RequestParam(required = false) String filter) throws JsonProcessingException {

        int[] ranges = objectMapper.readValue(range, int[].class);
        int pageSize = ranges[1] - ranges[0];
        int pageNumber = ranges[0] / pageSize;

        MailMonthlyReportsFilter mailMonthlyReportsFilter = objectMapper.readValue(filter, MailMonthlyReportsFilter.class);

        log.info("Find all mail monthly reports {} {} {}.", sort, ranges, mailMonthlyReportsFilter);

        PageRequest pageRequest = PageRequest.of(pageNumber, pageSize);

        Query query = new Query();

        Criteria criteria = new Criteria();
        if (mailMonthlyReportsFilter.getYear() != null) {
            criteria.and("year").is(mailMonthlyReportsFilter.getYear());
        }
        if (mailMonthlyReportsFilter.getMonth() != null) {
            criteria.and("month").is(mailMonthlyReportsFilter.getMonth());
        }
        query.addCriteria(criteria);

        long totalElements = mongoTemplate.count(query, MailMonthlyReport.class);

        List<MailMonthlyReport> result = mongoTemplate.find(query.with(pageRequest), MailMonthlyReport.class);

        return ResponseEntity
                .status(HttpStatus.OK)
                .header("Content-Range", String.format("mail-monthly-reports %s-%s/%s", ranges[0], ranges[1], totalElements))
                .body(result);
    }
}
