package pro.b2borganizer.services.mails.boundary;

import java.time.LocalDate;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pro.b2borganizer.services.mails.control.MailMessageRepository;
import pro.b2borganizer.services.mails.entity.MailMessage;
import pro.b2borganizer.services.mails.entity.MailMessagesFilter;

@RestController
@RequestMapping("/mail-messages")
@RequiredArgsConstructor
@Slf4j
public class MailMessageResource {

    private final MailMessageRepository mailMessageRepository;

    private final MongoTemplate mongoTemplate;

    private final ObjectMapper objectMapper;

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
    public List<MailMessage> findAllWithSimpleRestProvider(@RequestParam(required = false) String filter) {
        try {
            MailMessagesFilter mailMessagesFilter = objectMapper.readValue(filter, MailMessagesFilter.class);

            log.info("Find all mail messages with filter = {}.", filter);

            Query query = new Query();
            query.addCriteria(Criteria.where("id").in(mailMessagesFilter.getId()));

            return mongoTemplate.find(query, MailMessage.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
