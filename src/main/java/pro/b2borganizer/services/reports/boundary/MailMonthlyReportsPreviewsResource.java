package pro.b2borganizer.services.reports.boundary;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pro.b2borganizer.services.reports.entity.MailMonthlyReport;
import pro.b2borganizer.services.reports.entity.NewMailMonthlyReport;
import pro.b2borganizer.services.reports.control.MailMonthlyReportContentGenerator;
import pro.b2borganizer.services.reports.control.MailMonthlyReportMapper;

import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping("/mail-monthly-reports-previews")
@Slf4j
@RequiredArgsConstructor
public class MailMonthlyReportsPreviewsResource {

    private final MailMonthlyReportMapper mailMonthlyReportMapper;

    private final MailMonthlyReportContentGenerator mailMonthlyReportContentGenerator;

    @PostMapping(produces = "text/html")
    public ResponseEntity<String> create(@Valid @RequestBody NewMailMonthlyReport newMailMonthlyReport) {
        MailMonthlyReport mailMonthlyReport = mailMonthlyReportMapper.map(newMailMonthlyReport);

        return ResponseEntity.status(OK)
                .contentType(MediaType.parseMediaType(mailMonthlyReport.getContentType()))
                .body(mailMonthlyReportContentGenerator.generate(mailMonthlyReport));
    }
}
