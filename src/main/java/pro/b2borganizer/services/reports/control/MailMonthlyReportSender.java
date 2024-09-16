package pro.b2borganizer.services.reports.control;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import pro.b2borganizer.services.documents.control.ManagedDocumentRepository;
import pro.b2borganizer.services.mails.control.MailGateway;
import pro.b2borganizer.services.mails.entity.MailToSend;
import pro.b2borganizer.services.reports.entity.MailMonthlyReportCreatedEvent;
import pro.b2borganizer.services.documents.entity.ManagedDocument;
import pro.b2borganizer.services.reports.entity.MailMonthlyReportStatus;

@Component
@Slf4j
@RequiredArgsConstructor
public class MailMonthlyReportSender {

    private final MailMonthlyReportRepository mailMonthlyReportRepository;

    private final MailGateway mailGateway;

    private final MailMonthlyReportContentGenerator mailMonthlyReportContentGenerator;

    private final ManagedDocumentRepository managedDocumentRepository;

    @Async
    @EventListener
    public void sendMailMonthlyReport(MailMonthlyReportCreatedEvent mailMonthlyReportCreatedEvent) {
        log.info("MailMonthlyReportCreatedEvent received: {}", mailMonthlyReportCreatedEvent);

        mailMonthlyReportRepository.findById(mailMonthlyReportCreatedEvent.getMailMonthlyReportId())
                .ifPresent(mailMonthlyReport -> {
                    List<ManagedDocument> managedDocuments = mailMonthlyReport.getManagedDocumentIds()
                            .stream()
                            .map(managedDocumentRepository::findOptionalById).filter(Optional::isPresent)
                            .map(Optional::get)
                            .toList();

                    String content = mailMonthlyReportContentGenerator.generate(mailMonthlyReport, managedDocuments);

                    MailToSend mailToSend = new MailToSend();
                    mailToSend.setTo(mailMonthlyReport.getSendTo());
                    mailToSend.setCc(mailMonthlyReport.getCopyTo());
                    mailToSend.setContent(content);
                    mailToSend.setSubject(mailMonthlyReport.getSubject());
                    mailToSend.setAttachments(managedDocuments.stream().map(ManagedDocument::getManagedFile).toList());

                    log.info("Sending mail: {}", mailToSend);

                    mailGateway.sendMail(mailToSend);

                    mailMonthlyReport.setSent(LocalDateTime.now());
                    mailMonthlyReport.setStatus(MailMonthlyReportStatus.SENT);
                    mailMonthlyReportRepository.save(mailMonthlyReport);
                });
    }


}
