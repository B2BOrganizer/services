package pro.b2borganizer.services.mails.control;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import pro.b2borganizer.services.mails.entity.MailMessageError;

@Repository
public interface MailMessageErrorRepository extends MongoRepository<MailMessageError, String> {
}
