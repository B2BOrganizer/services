package pro.b2borganizer.services.mails.entity;

import java.util.List;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import pro.b2borganizer.services.files.entity.ManagedFile;

@Getter
@Setter
@ToString
public class MailToSend {

    private String subject;

    private String content;

    private String to;

    private List<ManagedFile> attachments;
}
