package pro.b2borganizer.services.templates.control;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.fasterxml.jackson.core.type.TypeReference;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;
import pro.b2borganizer.services.documents.entity.ManagedDocument;
import pro.b2borganizer.services.templates.entity.Template;
import pro.b2borganizer.services.templates.entity.TemplateType;
import pro.b2borganizer.services.templates.entity.TemplateVariable;

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
        template.setVariables(Set.of(
                new TemplateVariable("month", Integer.class.getTypeName(), null,"Month."),
                new TemplateVariable("year", Integer.class.getTypeName(), null, "Year."),
                new TemplateVariable("description", String.class.getTypeName(), null, "Additional description to be added to your monthly report."),
                new TemplateVariable("managedDocuments", ManagedDocument.class.getTypeName(), List.class.getTypeName(), "List of managed documents to be added to your monthly report.")
            )
        );
        templateRepository.save(template);

        Template reportErrorTemplate = new Template();
        reportErrorTemplate.setCode("ERROR_REPORT");
        reportErrorTemplate.setName("Error report");
        reportErrorTemplate.setPath("mail-templates/error-report.html");
        reportErrorTemplate.setContentType("text/html");
        reportErrorTemplate.setTemplateType(TemplateType.EMAIL);
        reportErrorTemplate.setVariables(Set.of(
                new TemplateVariable("mailMessageId", String.class.getTypeName(), null, "Mail message id."),
                new TemplateVariable("message", String.class.getTypeName(), null, "Additional description to be added to your monthly report."),
                new TemplateVariable("stacktrace", String.class.getTypeName(), null, "Additional description to be added to your monthly report.")
            )
        );
        templateRepository.save(reportErrorTemplate);

    }

}
