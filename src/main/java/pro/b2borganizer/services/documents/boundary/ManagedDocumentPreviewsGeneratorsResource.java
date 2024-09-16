package pro.b2borganizer.services.documents.boundary;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pro.b2borganizer.services.documents.entity.ManagedDocumentPreviewsGenerationEvent;

@RestController
@RequestMapping("/managed-document-previews-generators")
@RequiredArgsConstructor
@Slf4j
public class ManagedDocumentPreviewsGeneratorsResource {

    private final ApplicationEventPublisher applicationEventPublisher;

    @PostMapping
    public void generate() {
        applicationEventPublisher.publishEvent(ManagedDocumentPreviewsGenerationEvent.builder().type(ManagedDocumentPreviewsGenerationEvent.Type.ALL).build());
    }
}
