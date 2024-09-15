package pro.b2borganizer.services.reports.control;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import pro.b2borganizer.services.reports.entity.NewPredefinedMailMonthlyReport;
import pro.b2borganizer.services.reports.entity.PredefinedMailMonthlyReport;
import pro.b2borganizer.services.reports.entity.UpdatedPredefinedMailMonthlyReport;

@Mapper(componentModel = "spring")
public interface PredefinedMailMonthlyReportMapper {

    @Mapping(target = "id", ignore = true)
    PredefinedMailMonthlyReport map(NewPredefinedMailMonthlyReport newPredefinedMailMonthlyReport);

    @Mapping(target = "id", ignore = true)
    PredefinedMailMonthlyReport map(UpdatedPredefinedMailMonthlyReport updatedPredefinedMailMonthlyReport, @MappingTarget PredefinedMailMonthlyReport mappingTarget);
}
