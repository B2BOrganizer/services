package pro.b2borganizer.services.common.control;

import java.text.MessageFormat;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import pro.b2borganizer.services.common.entity.CriteriaSupportable;
import pro.b2borganizer.services.common.entity.SimpleFilter;
import pro.b2borganizer.services.common.entity.SimpleSort;

@Component
@RequiredArgsConstructor
@Slf4j
public class SimpleRestProviderQueryParser {

    private final ObjectMapper objectMapper;

    public SimpleRestProviderFilter parse(InputParameters inputParameters) {
        try {
            log.info("Parsing query = {}.", inputParameters);
            Map<String, Object> filters = objectMapper.readValue(inputParameters.filter, new TypeReference<>() {});

            SimpleRestProviderFilter.Pagination pagination;
            if (inputParameters.hasRange()) {
                int[] ranges = objectMapper.readValue(inputParameters.range, int[].class);
                int pageSize = ranges[1] - ranges[0];
                int pageNumber = ranges[0] / pageSize;
                pagination = new SimpleRestProviderFilter.Pagination(pageSize, pageNumber, ranges[0], ranges[1]);
            } else {
               pagination = new SimpleRestProviderFilter.Pagination(-1, -1, -1, -1);
            }

            SimpleSort simpleSort;
            if (inputParameters.hasSort()) {
                List<String> sortData = objectMapper.readValue(inputParameters.sort, new TypeReference<>() {});

                if (sortData.size() != 2) {
                    throw new IllegalArgumentException(MessageFormat.format("Invalid JSON format for Sort = {0}.", inputParameters.sort));
                }

                String field = sortData.get(0);
                SimpleSort.SortDirection direction;
                try {
                    direction = SimpleSort.SortDirection.valueOf(sortData.get(1));
                } catch (IllegalArgumentException e) {
                    throw new IllegalArgumentException("Invalid sort direction: " + sortData.get(1));
                }

                simpleSort = new SimpleSort(field, direction);
            } else {
                simpleSort = new SimpleSort("id", SimpleSort.SortDirection.ASC);
            }

            return new SimpleRestProviderFilter(inputParameters.name, new SimpleFilter(filters), pagination, simpleSort);
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

        public boolean hasSort() {
            return sort != null;
        }
    }
}
