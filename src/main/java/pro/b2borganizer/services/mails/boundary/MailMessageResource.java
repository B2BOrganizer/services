package pro.b2borganizer.services.mails.boundary;

import java.time.LocalDate;
import java.util.List;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cglib.core.Local;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
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

    @GetMapping
    public List<MailMessage> findAll(@RequestParam(required = false) LocalDate from,
                                     @RequestParam(required = false) LocalDate to) {

        log.info("Find all mail messages from {} to {}", from, to);

        Query query = new Query();

        if (from != null && to != null) {
            query.addCriteria(Criteria.where("received").gte(from).lte(to));
        } else if (from != null) {
            query.addCriteria(Criteria.where("received").gte(from));
        } else if (to != null) {
            query.addCriteria(Criteria.where("received").lte(to));
        }

        return mongoTemplate.find(query, MailMessage.class);
    }
}
