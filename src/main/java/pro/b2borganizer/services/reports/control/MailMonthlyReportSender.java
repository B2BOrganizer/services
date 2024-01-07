package pro.b2borganizer.services.reports.control;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import pro.b2borganizer.services.mails.control.MailGateway;
import pro.b2borganizer.services.mails.entity.MailToSend;
import pro.b2borganizer.services.reports.entity.MailMonthlyReportCreatedEvent;
import pro.b2borganizer.services.documents.entity.ManagedDocument;

@Component
@Slf4j
@RequiredArgsConstructor
public class MailMonthlyReportSender {

    private final MailMonthlyReportRepository mailMonthlyReportRepository;

    private final MailGateway mailGateway;

    private final MailMonthlyReportContentGenerator mailMonthlyReportContentGenerator;

    @Async
    @EventListener
    public void sendMailMonthlyReport(MailMonthlyReportCreatedEvent mailMonthlyReportCreatedEvent) {
        log.info("MailMonthlyReportCreatedEvent received: {}", mailMonthlyReportCreatedEvent);

        mailMonthlyReportRepository.findById(mailMonthlyReportCreatedEvent.getMailMonthlyReportId())
                .ifPresent(mailMonthlyReport -> {
                    String content = mailMonthlyReportContentGenerator.generate(mailMonthlyReport);

                    MailToSend mailToSend = new MailToSend();
                    mailToSend.setTo(mailMonthlyReport.getSendTo());
                    mailToSend.setCc(mailMonthlyReport.getCopyTo());
                    mailToSend.setContent(content);
                    mailToSend.setSubject(mailMonthlyReport.getSubject());
                    mailToSend.setAttachments(mailMonthlyReport.getManagedDocuments().stream().map(ManagedDocument::getManagedFile).toList());

                    log.info("Sending mail: {}", mailToSend);

                    mailGateway.sendMail(mailToSend);
                });
    }


}
