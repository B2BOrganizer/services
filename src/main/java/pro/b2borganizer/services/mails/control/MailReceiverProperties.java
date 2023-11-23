package pro.b2borganizer.services.mails.control;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "pro.b2organizer.mail.receiver")
public record MailReceiverProperties(String protocol, String host, int port, String folder, String username, String password) {
}
