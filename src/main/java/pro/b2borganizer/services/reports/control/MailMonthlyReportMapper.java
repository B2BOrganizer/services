package pro.b2borganizer.services.reports.control;

import java.text.MessageFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import pro.b2borganizer.services.documents.control.ManagedDocumentRepository;
import pro.b2borganizer.services.reports.entity.MailMonthlyReport;
import pro.b2borganizer.services.reports.entity.MailMonthlyReportStatus;
import pro.b2borganizer.services.reports.entity.NewMailMonthlyReport;
import pro.b2borganizer.services.templates.control.TemplateRepository;
import pro.b2borganizer.services.templates.entity.Template;
import pro.b2borganizer.services.documents.entity.ManagedDocument;

@Component
@RequiredArgsConstructor
public class MailMonthlyReportMapper {

    private final TemplateRepository templateRepository;

    private final ManagedDocumentRepository managedDocumentRepository;

    public MailMonthlyReport map(NewMailMonthlyReport newMailMonthlyReport) {
        Optional<Template> foundTemplate = templateRepository.findByCode(newMailMonthlyReport.getTemplateCode());

        if (foundTemplate.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, MessageFormat.format("Template code = {0} not found!", newMailMonthlyReport.getTemplateCode()));
        }

        YearMonth yearMonth = YearMonth.of(newMailMonthlyReport.getYear(), newMailMonthlyReport.getMonth());
        LocalDate from = yearMonth.atDay(1);
        LocalDate to = yearMonth.atEndOfMonth();

        List<ManagedDocument> managedDocuments = managedDocumentRepository.findByReceivedBetween(from, to);

        Template template = foundTemplate.get();

        MailMonthlyReport mailMonthlyReport = new MailMonthlyReport();
        mailMonthlyReport.setStatus(MailMonthlyReportStatus.CREATED);
        mailMonthlyReport.setCreated(LocalDateTime.now());
        mailMonthlyReport.setSent(null);
        mailMonthlyReport.setSendTo(newMailMonthlyReport.getSendTo());
        mailMonthlyReport.setTemplateId(template.getId());
        mailMonthlyReport.setContentType(template.getContentType());
        mailMonthlyReport.setTemplateVariables(newMailMonthlyReport.getTemplateVariables());
        mailMonthlyReport.setSubject(newMailMonthlyReport.getSubject());
        mailMonthlyReport.setMonth(newMailMonthlyReport.getMonth());
        mailMonthlyReport.setYear(newMailMonthlyReport.getYear());
        mailMonthlyReport.setManagedDocuments(managedDocuments);

        return mailMonthlyReport;
    }
}
