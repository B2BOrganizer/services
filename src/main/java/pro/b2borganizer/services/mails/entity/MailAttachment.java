package pro.b2borganizer.services.mails.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import pro.b2borganizer.services.files.entity.ManagedFile;

@Getter
@Setter
@ToString
public class MailAttachment {

    private ManagedFile managedFile;

    private String disposition;
}
