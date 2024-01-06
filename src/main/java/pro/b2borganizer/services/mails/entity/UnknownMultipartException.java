package pro.b2borganizer.services.mails.entity;

import lombok.Getter;

@Getter
public class UnknownMultipartException extends  Exception {
    private final MailMessage mailMessage;
    private final String contentType;

    public UnknownMultipartException(MailMessage mailMessage, String contentType) {
        this.mailMessage = mailMessage;
        this.contentType = contentType;
    }

}
