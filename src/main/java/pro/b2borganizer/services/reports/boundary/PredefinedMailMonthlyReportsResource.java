package pro.b2borganizer.services.reports.boundary;

import java.text.MessageFormat;
import java.util.List;
import java.util.Optional;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import pro.b2borganizer.services.common.control.SimpleRestProviderFilter;
import pro.b2borganizer.services.common.control.SimpleRestProviderQueryParser;
import pro.b2borganizer.services.common.control.SimpleRestProviderRepository;
import pro.b2borganizer.services.common.control.SimpleRestProviderResponseBuilder;
import pro.b2borganizer.services.common.entity.SimpleFilter;
import pro.b2borganizer.services.reports.control.PredefinedMailMonthlyReportMapper;
import pro.b2borganizer.services.reports.control.PredefinedMailMonthlyReportRepository;
import pro.b2borganizer.services.reports.entity.NewPredefinedMailMonthlyReport;
import pro.b2borganizer.services.reports.entity.PredefinedMailMonthlyReport;
import pro.b2borganizer.services.reports.entity.UpdatedPredefinedMailMonthlyReport;

@RestController
@RequestMapping("/predefined-mail-monthly-reports")
@Slf4j
@RequiredArgsConstructor
public class PredefinedMailMonthlyReportsResource {

    private final SimpleRestProviderQueryParser simpleRestProviderQueryParser;

    private final SimpleRestProviderRepository simpleRestProviderRepository;

    private final SimpleRestProviderResponseBuilder simpleRestProviderResponseBuilder;

    private final PredefinedMailMonthlyReportMapper predefinedMailMonthlyReportMapper;

    private final PredefinedMailMonthlyReportRepository predefinedMailMonthlyReportRepository;

    @PostMapping
    public ResponseEntity<PredefinedMailMonthlyReport> create(@Valid @RequestBody NewPredefinedMailMonthlyReport newPredefinedMailMonthlyReport) {
        return createInternal(newPredefinedMailMonthlyReport);
    }

    private ResponseEntity<PredefinedMailMonthlyReport> createInternal(NewPredefinedMailMonthlyReport newPredefinedMailMonthlyReport) {
        PredefinedMailMonthlyReport predefinedMailMonthlyReport = predefinedMailMonthlyReportMapper.map(newPredefinedMailMonthlyReport);

        PredefinedMailMonthlyReport saved = predefinedMailMonthlyReportRepository.save(predefinedMailMonthlyReport);

        log.info("Created new predefined mail monthly report = {}.", saved);

        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @PostMapping(consumes = "application/json+simpleRestProvider")
    public ResponseEntity<PredefinedMailMonthlyReport> create(@Valid @RequestBody String data) {
        log.info("Creating new email monthly report = {}.", data);

        NewPredefinedMailMonthlyReport newPredefinedMailMonthlyReport = simpleRestProviderQueryParser.parse(data, NewPredefinedMailMonthlyReport.class);

        return createInternal(newPredefinedMailMonthlyReport);
    }

    @GetMapping(consumes = "application/json+simpleRestProvider")
    public ResponseEntity<List<PredefinedMailMonthlyReport>> findAll(@RequestParam(required = false) String sort,
                                                                     @RequestParam(required = false) String range,
                                                                     @RequestParam(required = false) String filter) {

        SimpleRestProviderQueryParser.InputParameters inputParameters = new SimpleRestProviderQueryParser.InputParameters("predefined-mail-monthly-reports", sort, range, filter);

        SimpleRestProviderFilter simpleRestProviderFilter = simpleRestProviderQueryParser.parse(inputParameters);

        SimpleRestProviderRepository.SimpleRestProviderQueryListResult<PredefinedMailMonthlyReport> result = simpleRestProviderRepository.findByQuery(simpleRestProviderFilter, PredefinedMailMonthlyReport.class);

        return simpleRestProviderResponseBuilder.buildListResponse(simpleRestProviderFilter, result);
    }

    @GetMapping(consumes = "application/json+simpleRestProvider", value = "/{id}")
    public ResponseEntity<PredefinedMailMonthlyReport> getOne(@PathVariable String id) {
        Optional<PredefinedMailMonthlyReport> found = simpleRestProviderRepository.getOne(id, PredefinedMailMonthlyReport.class);

        return found.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping(value = "/{id}", consumes = "application/json+simpleRestProvider")
    public PredefinedMailMonthlyReport update(@PathVariable(value = "id") String id, @RequestBody String updated) {
        UpdatedPredefinedMailMonthlyReport updatedPredefinedMailMonthlyReport = simpleRestProviderQueryParser.parse(updated, UpdatedPredefinedMailMonthlyReport.class);

        log.info("Updating = {} {}.", id, updatedPredefinedMailMonthlyReport);

        PredefinedMailMonthlyReport predefinedMailMonthlyReport = predefinedMailMonthlyReportRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, MessageFormat.format("Entity = {0} with id = {1} has not been found!", PredefinedMailMonthlyReport.class, id)));

        PredefinedMailMonthlyReport toUpdate = predefinedMailMonthlyReportMapper.map(updatedPredefinedMailMonthlyReport, predefinedMailMonthlyReport);

        return predefinedMailMonthlyReportRepository.save(toUpdate);
    }

}
