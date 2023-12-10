package pro.b2borganizer.services.documents.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class NewManagedDocumentsMovement {

    private LocalDate receivedFrom;

    private LocalDate receivedTo;

    private LocalDateTime newReceived;
}
