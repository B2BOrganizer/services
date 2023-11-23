package pro.b2borganizer.services.files.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@ToString
public class ManagedFile {

    private String fileName;

    private String mimeType;

    @ToString.Exclude
    @JsonIgnore
    private String contentInBase64;
}
