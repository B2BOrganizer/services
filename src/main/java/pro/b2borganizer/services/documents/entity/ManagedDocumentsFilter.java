package pro.b2borganizer.services.documents.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ManagedDocumentsFilter {

    private Integer assignedToMonth;
    
    private Integer assignedToYear;
}
