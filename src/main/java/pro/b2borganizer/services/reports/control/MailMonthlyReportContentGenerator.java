package pro.b2borganizer.services.reports.control;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import pro.b2borganizer.services.documents.entity.ManagedDocument;
import pro.b2borganizer.services.reports.entity.MailMonthlyReport;
import pro.b2borganizer.services.templates.boundary.TemplateParser;

@Component
@RequiredArgsConstructor
public class MailMonthlyReportContentGenerator {

    private final TemplateParser templateParser;

    public String generate(MailMonthlyReport mailMonthlyReport, List<ManagedDocument> managedDocuments) {
        Map<String, Object> variables = new HashMap<>();
        variables.put("month", mailMonthlyReport.getMonth());
        variables.put("year", mailMonthlyReport.getYear());
        variables.put("managedDocuments", managedDocuments);
        variables.putAll(mailMonthlyReport.getTemplateVariables());

        return templateParser.parseById(mailMonthlyReport.getTemplateId(), variables);
    }
}
