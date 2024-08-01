package pro.b2borganizer.services.documents.control;

import java.time.DateTimeException;
import java.time.Month;
import java.time.Year;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.stereotype.Component;

@Component
public class MailContentParser {

    private static final String ASSIGNED_YEAR_MONTH_REGEX = "\\[%X1001-(\\d{4})-(\\d{2})%\\]";

    private static final Pattern ASSIGNED_YEAR_MONTH_PATTERN = Pattern.compile(ASSIGNED_YEAR_MONTH_REGEX);

    public Optional<AssignedYearMonth> parseMailContent(String content) {
        Matcher matcher = ASSIGNED_YEAR_MONTH_PATTERN.matcher(content);

        if (matcher.find()) {
            int year = Integer.parseInt(matcher.group(1));
            int month = Integer.parseInt(matcher.group(2));

            try {
                return Optional.of(new AssignedYearMonth(Year.of(year), Month.of(month)));
            } catch (DateTimeException e) {
                return Optional.empty();
            }
        }

        return Optional.empty();
    }

    public record AssignedYearMonth(Year year, Month month) {

    }
}
