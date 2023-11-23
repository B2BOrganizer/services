package pro.b2borganizer.services.mails.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@ToString
@Document("mailMessages")
public class MailMessage {

    @Id
    private String id;

    @ToString.Exclude
    private String serializedMimeMessage;

    private String subject;

    private String sender;

    private LocalDateTime received;

    private LocalDateTime sent;

    private String plainTextContent;

    private String htmlContent;

    private List<MailAttachment> contentRelatedMailAttachments;

    private List<MailAttachment> mailAttachments;

    private List<MailParseError> mailParseErrors;

    public void addContentRelatedMailAttachment(MailAttachment mailAttachment) {
        if (contentRelatedMailAttachments == null) {
            contentRelatedMailAttachments = new ArrayList<>();
        }

        contentRelatedMailAttachments.add(mailAttachment);
    }

    public void addMailAttachment(MailAttachment mailAttachment) {
        if (mailAttachments == null) {
            mailAttachments = new ArrayList<>();
        }

        mailAttachments.add(mailAttachment);
    }

    public void addMailParseError(MailParseError mailParseError) {
        if (mailParseErrors == null) {
            mailParseErrors = new ArrayList<>();
        }

        mailParseErrors.add(mailParseError);
    }
}
