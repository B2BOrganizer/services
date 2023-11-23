package pro.b2borganizer.services.templates.boundary;

import java.text.MessageFormat;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import pro.b2borganizer.services.templates.control.TemplateRepository;
import pro.b2borganizer.services.templates.entity.Template;
import pro.b2borganizer.services.templates.entity.TemplatePreviewInput;

@RestController
@RequestMapping("/templates-previews")
@RequiredArgsConstructor
@Slf4j
public class TemplatesPreviewsResource {

    private final TemplateRepository templateRepository;

    private final TemplateParser templateParser;

    @PostMapping
    public ResponseEntity<String> create(@RequestBody TemplatePreviewInput templatePreviewInput) {
        log.info("Creating template preview for template code = {} and variables = {}.", templatePreviewInput.getTemplateCode(), templatePreviewInput.getVariables());

        Template template = templateRepository.findByCode(templatePreviewInput.getTemplateCode())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, MessageFormat.format("Template code = {0} not found!", templatePreviewInput.getTemplateCode())));

        return ResponseEntity.status(HttpStatus.OK)
                .contentType(MediaType.parseMediaType(template.getContentType()))
                .body(templateParser.parse(template.getId(), templatePreviewInput.getVariables()));
    }
}
