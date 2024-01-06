package pro.b2borganizer.services.reports.control;

import java.util.HashMap;
import java.util.Map;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import pro.b2borganizer.services.reports.entity.MailMonthlyReport;
import pro.b2borganizer.services.templates.boundary.TemplateParser;

@Component
@RequiredArgsConstructor
public class MailMonthlyReportContentGenerator {

    private final TemplateParser templateParser;

    public String generate(MailMonthlyReport mailMonthlyReport) {
        Map<String, Object> variables = new HashMap<>();
        variables.put("month", mailMonthlyReport.getMonth());
        variables.put("year", mailMonthlyReport.getYear());
        variables.put("managedDocuments", mailMonthlyReport.getManagedDocuments());
        variables.putAll(mailMonthlyReport.getTemplateVariables());

        return templateParser.parseById(mailMonthlyReport.getTemplateId(), variables);
    }
}
