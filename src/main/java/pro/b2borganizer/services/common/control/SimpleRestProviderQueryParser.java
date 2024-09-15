package pro.b2borganizer.services.common.control;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import pro.b2borganizer.services.common.entity.CriteriaSupportable;

@Component
@RequiredArgsConstructor
@Slf4j
public class SimpleRestProviderQueryParser {

    private final ObjectMapper objectMapper;

    public <F extends CriteriaSupportable> SimpleRestProviderFilter<F> parse(InputParameters inputParameters, Class<F> filterClass) {
        try {
            log.info("Parsing query = {}.", inputParameters);
            F objectFilter = objectMapper.readValue(inputParameters.filter, filterClass);

            SimpleRestProviderFilter.Pagination pagination;
            if (inputParameters.hasRange()) {
                int[] ranges = objectMapper.readValue(inputParameters.range, int[].class);
                int pageSize = ranges[1] - ranges[0];
                int pageNumber = ranges[0] / pageSize;
                pagination = new SimpleRestProviderFilter.Pagination(pageSize, pageNumber, ranges[0], ranges[1]);
            } else {
               pagination = new SimpleRestProviderFilter.Pagination(-1, -1, -1, -1);
            }

            return new SimpleRestProviderFilter<>(inputParameters.name, objectFilter, pagination);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Failed to parse query.", e);
        }
    }

    public <D> D parse(String data, Class<D> dataClass) {
        try {
            return objectMapper.readValue(data, dataClass);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Failed to parse data.", e);
        }
    }

    public record InputParameters(String name, String sort, String range, String filter) {

        public boolean hasRange() {
            return range != null;
        }
    }
}
