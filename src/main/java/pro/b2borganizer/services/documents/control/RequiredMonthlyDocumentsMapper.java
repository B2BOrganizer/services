package pro.b2borganizer.services.documents.control;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import pro.b2borganizer.services.documents.entity.NewRequiredDocument;
import pro.b2borganizer.services.documents.entity.RequiredDocument;
import pro.b2borganizer.services.documents.entity.UpdatedRequiredDocument;

@Mapper(componentModel = "spring")
public interface RequiredMonthlyDocumentsMapper {

    @Mapping(target = "id", ignore = true)
    RequiredDocument map(NewRequiredDocument newRequiredDocument);

    @Mapping(target = "id", ignore = true)
    RequiredDocument map(UpdatedRequiredDocument updatedRequiredDocument, @MappingTarget RequiredDocument mappingTarget);
}
