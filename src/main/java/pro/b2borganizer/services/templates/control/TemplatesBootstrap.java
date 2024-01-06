package pro.b2borganizer.services.templates.control;

import java.util.Map;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;
import pro.b2borganizer.services.templates.entity.Template;
import pro.b2borganizer.services.templates.entity.TemplateType;

@Component
@RequiredArgsConstructor
public class TemplatesBootstrap implements ApplicationListener<ContextRefreshedEvent> {

    private final TemplateRepository templateRepository;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        templateRepository.deleteAll();

        Template template = new Template();
        template.setCode("MONTHLY_DOCUMENTS_REPORT");
        template.setName("Monthly documents report");
        template.setPath("mail-templates/monthly-report.html");
        template.setContentType("text/html");
        template.setTemplateType(TemplateType.EMAIL);
        template.setTemplateVariables(Map.of(
                "month", "(Integer) Month.",
                "year", "(Integer) Year.",
                "description", "(String) Additional description to be added to your monthly report.",
                "managedDocuments", "(List<pro.raszkowski.myb2bspace.services.documents.entity.ManagedDocument>) List of managed documents to be added to your monthly report."
        ));
        templateRepository.save(template);

        Template reportErrorTemplate = new Template();
        reportErrorTemplate.setCode("ERROR_REPORT");
        reportErrorTemplate.setName("Error report");
        reportErrorTemplate.setPath("mail-templates/error-report.html");
        reportErrorTemplate.setContentType("text/html");
        reportErrorTemplate.setTemplateType(TemplateType.EMAIL);
        reportErrorTemplate.setTemplateVariables(Map.of(
                "mailMessageId", "(String)",
                "message", "(String) Additional description to be added to your monthly report.",
                "stacktrace", "(String) Additional description to be added to your monthly report."
        ));
        templateRepository.save(reportErrorTemplate);

    }

}
