package pro.b2borganizer.services.timesheets.boundary;

import java.text.MessageFormat;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import pro.b2borganizer.services.common.control.SimpleRestProviderFilter;
import pro.b2borganizer.services.common.control.SimpleRestProviderRepository;
import pro.b2borganizer.services.common.entity.SimpleFilter;
import pro.b2borganizer.services.common.entity.SimpleSort;
import pro.b2borganizer.services.timesheets.control.WorkLogsRepository;
import pro.b2borganizer.services.timesheets.entity.WorkLog;

@RestController
@RequestMapping("/work-logs")
@Slf4j
@RequiredArgsConstructor
public class WorkLogsResource {

    private final WorkLogsRepository workLogsRepository;

    private final SimpleRestProviderRepository simpleRestProviderRepository;

    @GetMapping
    public List<WorkLog> list(@RequestParam(required = false) LocalDate from,
                              @RequestParam(required = false) LocalDate to) {

        log.info("Test 3daaa test");

        Map<SimpleFilter.SimpleFilterKey, Object> filters = new HashMap<>();
        if (from != null) {
            filters.put(new SimpleFilter.SimpleFilterKey("day", SimpleFilter.KeyType.GREATER_THAN), from);
        }
        if (to != null) {
            filters.put(new SimpleFilter.SimpleFilterKey("day", SimpleFilter.KeyType.LESS_THAN_EQUALS), to);
        }

        SimpleFilter simpleFilter = new SimpleFilter(filters);
        SimpleSort simpleSort = new SimpleSort("day", SimpleSort.SortDirection.ASC);
        SimpleRestProviderFilter simpleRestProviderFilter = new SimpleRestProviderFilter("work-logs", simpleFilter, SimpleRestProviderFilter.Pagination.EMPTY, simpleSort);

        SimpleRestProviderRepository.SimpleRestProviderQueryListResult<WorkLog> result = simpleRestProviderRepository.findByQuery(simpleRestProviderFilter, WorkLog.class);

        return result.results();
    }

    @GetMapping("/{day}")
    public WorkLog get(@PathVariable(value = "day") LocalDate day) {
        log.info("Getting WorkDay by day (id) = {}", day);

        return workLogsRepository.findOptionalByDay(day)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, MessageFormat.format("Day = {0} not found.", day)));
    }

    @PostMapping
    public ResponseEntity<WorkLog> create(@RequestBody WorkLog workLog) {
        log.info("Create work log = {}", workLog);

        WorkLog saved = workLogsRepository.save(workLog);

        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }
}
