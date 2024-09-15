package pro.b2borganizer.services.timesheets.entity;

import java.time.Duration;
import java.time.LocalDate;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("workLogs")
@Getter
@Setter
@ToString
public class WorkLog {
    @Id
    private String id;

    private LocalDate day;

    private Duration workingTime;

    private String client;

    private String comment;
}
