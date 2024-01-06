package pro.b2borganizer.services.templates.boundary;

import java.util.Map;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;
import pro.b2borganizer.services.templates.control.TemplateRepository;
import pro.b2borganizer.services.templates.entity.Template;

@Service
@RequiredArgsConstructor
public class TemplateParser {

    private final SpringTemplateEngine springTemplateEngine;

    private final TemplateRepository templateRepository;

    public String parseById(String templateId, Map<String, Object> variables) {
        Template template = templateRepository.findById(templateId)
                .orElseThrow();

        return parse(template, variables);
    }

    private String parse(Template template, Map<String, Object> variables) {
        Context context = new Context();
        context.setVariables(variables);

        return springTemplateEngine.process(template.getPath(), context);
    }

    public String parseByCode(String templateCode, Map<String, Object> variables) {
        Template template = templateRepository.findByCode(templateCode)
                .orElseThrow();

        return parse(template, variables);
    }
}
