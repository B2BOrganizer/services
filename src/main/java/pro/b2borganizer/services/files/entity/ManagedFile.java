package pro.b2borganizer.services.files.entity;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.util.MimeType;
import org.springframework.util.MimeTypeUtils;

@Getter
@Setter
@ToString
public class ManagedFile {

    private String fileName;

    private String mimeType;

    @ToString.Exclude
    @JsonIgnore
    private String contentInBase64;

    public boolean isPdf() {
        MimeType pdfMimeType = MimeType.valueOf("application/pdf");
        MimeType parseMimeType = MimeTypeUtils.parseMimeType(mimeType);

        return pdfMimeType.equalsTypeAndSubtype(parseMimeType);
    }
}
