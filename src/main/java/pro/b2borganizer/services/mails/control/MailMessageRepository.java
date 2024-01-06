package pro.b2borganizer.services.mails.control;

import java.util.List;

import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import pro.b2borganizer.services.mails.entity.MailMessage;

@Repository
public interface MailMessageRepository extends MongoRepository<MailMessage, String> {
}
