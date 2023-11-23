package pro.b2borganizer.services.mails.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@ToString
@Document("mailMessageErrors")
public class MailMessageError {

    @Id
    private String id;

    @ToString.Exclude
    private String serializedMimeMessage;

    private String messageId;

    private String stackTrace;
}
