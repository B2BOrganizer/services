package pro.b2borganizer.services.templates.boundary;

import java.util.List;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pro.b2borganizer.services.common.control.SimpleRestProviderFilter;
import pro.b2borganizer.services.common.control.SimpleRestProviderQueryParser;
import pro.b2borganizer.services.common.control.SimpleRestProviderRepository;
import pro.b2borganizer.services.common.control.SimpleRestProviderResponseBuilder;
import pro.b2borganizer.services.common.entity.SimpleFilter;
import pro.b2borganizer.services.templates.control.TemplateRepository;
import pro.b2borganizer.services.templates.entity.Template;

@RestController
@RequestMapping("/templates")
@RequiredArgsConstructor
public class TemplatesResource {

    private final TemplateRepository templateRepository;

    private final SimpleRestProviderQueryParser simpleRestProviderQueryParser;

    private final SimpleRestProviderRepository simpleRestProviderRepository;

    private final SimpleRestProviderResponseBuilder simpleRestProviderResponseBuilder;

    @GetMapping
    public List<Template> findAll() {
        return templateRepository.findAll();
    }

    @GetMapping(consumes = "application/json+simpleRestProvider")
    public ResponseEntity<List<Template>> findAll(@RequestParam(required = false) String sort,
                                                  @RequestParam(required = false) String range,
                                                  @RequestParam(required = false) String filter) {

        SimpleRestProviderQueryParser.InputParameters inputParameters = new SimpleRestProviderQueryParser.InputParameters("templates", sort, range, filter);

        SimpleRestProviderFilter<SimpleFilter> simpleRestProviderFilter = simpleRestProviderQueryParser.parse(inputParameters, SimpleFilter.class);

        SimpleRestProviderRepository.SimpleRestProviderQueryListResult<Template> result = simpleRestProviderRepository.findByQuery(simpleRestProviderFilter, Template.class);

        return simpleRestProviderResponseBuilder.buildListResponse(simpleRestProviderFilter, result);
    }
}
