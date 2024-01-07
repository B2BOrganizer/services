package pro.b2borganizer.services.mails.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
    @JsonIgnore
    private String serializedMimeMessage;

    private String subject;

    private String sender;

    private LocalDateTime received;

    private LocalDateTime sent;

    @JsonIgnore
    private String plainTextContent;

    @JsonIgnore
    private String htmlContent;

    private List<MailAttachment> contentRelatedMailAttachments = new ArrayList<>();

    private List<MailAttachment> mailAttachments = new ArrayList<>();

    private List<MailParseError> mailParseErrors = new ArrayList<>();

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

    public boolean hasErrors() {
        return mailParseErrors != null && !mailParseErrors.isEmpty();
    }

    public void appendPlainText(String plainText) {
        if (plainTextContent == null) {
            plainTextContent = "";
        }

        plainTextContent += plainText;
    }
}
