package pro.b2borganizer.services.reports.boundary;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pro.b2borganizer.services.reports.control.MailMonthlyReportRepository;
import pro.b2borganizer.services.reports.entity.MailMonthlyReportCreatedEvent;
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

    @PostMapping
    public void create(@Valid @RequestBody NewMailMonthlyReport newMailMonthlyReport) {
        log.info("Creating new email monthly report = {}.", newMailMonthlyReport);

        MailMonthlyReport mailMonthlyReport = mailMonthlyReportMapper.map(newMailMonthlyReport);

        MailMonthlyReport saved = mailMonthlyReportRepository.save(mailMonthlyReport);

        log.info("New email monthly report created = {}.", mailMonthlyReport);

        applicationEventPublisher.publishEvent(MailMonthlyReportCreatedEvent.builder().mailMonthlyReportId(saved.getId()).build());
    }
}
