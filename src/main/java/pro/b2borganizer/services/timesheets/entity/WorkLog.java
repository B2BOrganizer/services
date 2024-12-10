package pro.b2borganizer.services.timesheets.entity;

import java.time.Duration;
import java.time.LocalDate;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.checkerframework.common.aliasing.qual.Unique;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("workLogs")
@Getter
@Setter
@ToString
public class WorkLog {
    @Id
    private String id;

    @Indexed(unique = true)
    private LocalDate day;

    @NotNull
    private Duration workingTime;

    @NotNull
    private String client;

    private String comment;
}
