package pro.b2borganizer.services.mails.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Builder
@ToString
public class MailReceivedEvent {

    private String mailMessageId;
}
