package pro.b2borganizer.services.timesheets.boundary;

import java.text.MessageFormat;
import java.time.LocalDate;
import java.util.List;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import pro.b2borganizer.services.timesheets.control.WorkLogsRepository;
import pro.b2borganizer.services.timesheets.entity.WorkLog;

@RestController
@RequestMapping("/work-logs")
@Slf4j
@RequiredArgsConstructor
public class WorkLogsResource {

    private final WorkLogsRepository workLogsRepository;

    @GetMapping
    public List<WorkLog> list() {
        return workLogsRepository.findAll();
    }

    @GetMapping("/{day}")
    public WorkLog get(@PathVariable(value = "day") LocalDate day) {
        log.info("Getting WorkDay by day (id) = {}", day);

        return workLogsRepository.findOptionalByDay(day)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, MessageFormat.format("Day = {0} not found.", day)));
    }

    @PostMapping
    public void create(@RequestBody WorkLog workLog) {
        log.info("Create work log = {}", workLog);

        workLogsRepository.save(workLog);
    }
}
