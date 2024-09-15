package pro.b2borganizer.services.common.control;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import pro.b2borganizer.services.common.entity.CriteriaSupportable;

@Component
public class SimpleRestProviderResponseBuilder {

    public <F extends CriteriaSupportable, R> ResponseEntity<List<R>> buildListResponse(SimpleRestProviderFilter<F> filter, SimpleRestProviderRepository.SimpleRestProviderQueryListResult<R> result) {
        ResponseEntity.BodyBuilder bodyBuilder = ResponseEntity.status(HttpStatus.OK);

        if (filter.isListQuery()) {
            bodyBuilder = bodyBuilder.header("Content-Range", String.format("%s %s-%s/%s", filter.name(), filter.pagination().rangeStart(), filter.pagination().rangeEnd(), result.totalElements()));
        }

        return bodyBuilder.body(result.results());
    }
}
