package pro.b2borganizer.services.templates.boundary;

import java.util.List;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pro.b2borganizer.services.templates.control.TemplateRepository;
import pro.b2borganizer.services.templates.entity.Template;

@RestController
@RequestMapping("/templates")
@RequiredArgsConstructor
public class TemplatesResource {

    private final TemplateRepository templateRepository;

    @GetMapping
    public List<Template> findAll() {
        return templateRepository.findAll();
    }
}
