package pro.b2borganizer.services.reports.control;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import pro.b2borganizer.services.reports.entity.ManagedDocumentsReport;
import pro.b2borganizer.services.reports.entity.NewManagedDocumentsReport;

@Mapper(componentModel = "spring")
public interface ManagedDocumentsReportMapper {

    /**
     * @param newManagedDocumentsReport
     * @return
     */
    @Mapping(target = "id", ignore = true)
    ManagedDocumentsReport map(NewManagedDocumentsReport newManagedDocumentsReport); }
