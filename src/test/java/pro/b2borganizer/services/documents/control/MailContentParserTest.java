package pro.b2borganizer.services.documents.control;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Month;
import java.time.Year;
import java.util.Optional;

import org.junit.jupiter.api.Test;

class MailContentParserTest {
    private final MailContentParser mailContentParser = new MailContentParser();

    @Test
    void parseMailContent1() {
        //GIVEN

        //WHEN
        Optional<MailContentParser.AssignedYearMonth> assignedYearMonth = mailContentParser.parseMailContent("To jest jakaś treść [%X1001%]");

        //THEN
        assertThat(assignedYearMonth).isNotEmpty();
    }

    @Test
    void parseMailContent2() {
        //GIVEN

        //WHEN
        Optional<MailContentParser.AssignedYearMonth> assignedYearMonth = mailContentParser.parseMailContent("To jest jakaś treść [%X1001-2024-06%]");

        //THEN
        assertThat(assignedYearMonth)
                .isPresent()
                .get()
                .isEqualTo(new MailContentParser.AssignedYearMonth(Year.of(2024), Month.of(6)));
    }

    @Test
    void parseMailContent3() {
        //GIVEN

        //WHEN
        Optional<MailContentParser.AssignedYearMonth> assignedYearMonth = mailContentParser.parseMailContent("To jest jakaś treść [%X1001-2024-15%]");

        //THEN
        assertThat(assignedYearMonth)
                .isEmpty();
    }

    @Test
    void parseMailContent4() {
        //GIVEN

        //WHEN
        Optional<MailContentParser.AssignedYearMonth> assignedYearMonth = mailContentParser.parseMailContent("To jest jakaś treść [%X1001-22024-10%]");

        //THEN
        assertThat(assignedYearMonth)
                .isEmpty();
    }
}
