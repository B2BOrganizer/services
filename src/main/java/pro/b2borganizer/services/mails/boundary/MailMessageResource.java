package pro.b2borganizer.services.mails.boundary;

import java.text.MessageFormat;
import java.time.LocalDate;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import pro.b2borganizer.services.common.control.SimpleRestProviderFilter;
import pro.b2borganizer.services.common.control.SimpleRestProviderQueryParser;
import pro.b2borganizer.services.common.control.SimpleRestProviderRepository;
import pro.b2borganizer.services.common.control.SimpleRestProviderResponseBuilder;
import pro.b2borganizer.services.common.entity.SimpleFilter;
import pro.b2borganizer.services.documents.entity.ManagedDocument;
import pro.b2borganizer.services.mails.control.MailMessageRepository;
import pro.b2borganizer.services.mails.entity.MailMessage;

@RestController
@RequestMapping("/mail-messages")
@RequiredArgsConstructor
@Slf4j
public class MailMessageResource {

    private final MailMessageRepository mailMessageRepository;

    private final MongoTemplate mongoTemplate;

    private final ObjectMapper objectMapper;

    private final SimpleRestProviderQueryParser simpleRestProviderQueryParser;

    private final SimpleRestProviderRepository simpleRestProviderRepository;

    private final SimpleRestProviderResponseBuilder simpleRestProviderResponseBuilder;

    @GetMapping
    public List<MailMessage> findAll(@RequestParam(required = false) LocalDate from,
                                     @RequestParam(required = false) LocalDate to) {

        log.info("Find all mail messages from {} to {}", from, to);

        Query query = new Query();

        if (from != null && to != null) {
            query.addCriteria(Criteria.where("received").gte(from).lt(to));
        } else if (from != null) {
            query.addCriteria(Criteria.where("received").gte(from));
        } else if (to != null) {
            query.addCriteria(Criteria.where("received").lt(to));
        }

        return mongoTemplate.find(query, MailMessage.class);
    }

    @GetMapping(consumes = "application/json+simpleRestProvider")
    public ResponseEntity<List<MailMessage>> findAllWithSimpleRestProvider(@RequestParam(required = false) String sort,
                                                                           @RequestParam(required = false) String range,
                                                                           @RequestParam(required = false) String filter) {

        SimpleRestProviderQueryParser.InputParameters inputParameters = new SimpleRestProviderQueryParser.InputParameters("mail-messages", sort, range, filter);

        SimpleRestProviderFilter simpleRestProviderFilter = simpleRestProviderQueryParser.parse(inputParameters);

        SimpleRestProviderRepository.SimpleRestProviderQueryListResult<MailMessage> result = simpleRestProviderRepository.findByQuery(simpleRestProviderFilter, MailMessage.class);

        return simpleRestProviderResponseBuilder.buildListResponse(simpleRestProviderFilter, result);
    }

    @GetMapping("/{id}")
    public MailMessage get(@PathVariable(value = "id") String id) {
        log.info("Get mail message = {}", id);

        return mailMessageRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, MessageFormat.format("Mail message with id = {0} not found.", id)));
    }
}
