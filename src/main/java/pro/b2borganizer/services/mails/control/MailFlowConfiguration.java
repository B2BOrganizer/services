package pro.b2borganizer.services.mails.control;

import java.time.Duration;
import java.util.Properties;

import jakarta.mail.AuthenticationFailedException;
import jakarta.mail.URLName;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.framework.AopProxyUtils;
import org.springframework.aot.hint.RuntimeHints;
import org.springframework.aot.hint.RuntimeHintsRegistrar;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.MessageChannels;
import org.springframework.integration.mail.ImapMailReceiver;
import org.springframework.integration.mail.MailReceiver;
import org.springframework.integration.mail.dsl.ImapMailInboundChannelAdapterSpec;
import org.springframework.integration.mail.dsl.Mail;
import org.springframework.integration.scheduling.PollerMetadata;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.support.PeriodicTrigger;

@Configuration
@Slf4j
@RequiredArgsConstructor
public class MailFlowConfiguration {

    private final MailReceiverProperties mailReceiverProperties;

    @Bean
    public IntegrationFlow inboundMailFlow() {
        Properties javaMailProperties = new Properties();
        javaMailProperties.put("mail.debug", "false");
        javaMailProperties.put("mail.mime.address.strict", "false");

        URLName urlName = new URLName(mailReceiverProperties.protocol(), mailReceiverProperties.host(), mailReceiverProperties.port(), mailReceiverProperties.folder(), mailReceiverProperties.username(), mailReceiverProperties.password());

        ImapMailInboundChannelAdapterSpec imapMailInboundChannelAdapterSpec = Mail.imapInboundAdapter(urlName.toString())
                .shouldMarkMessagesAsRead(false)
                .shouldDeleteMessages(false)
                .simpleContent(true)
                .autoCloseFolder(true)
                .javaMailProperties(javaMailProperties);

        return IntegrationFlow
                .from(imapMailInboundChannelAdapterSpec,
                        e -> e.autoStartup(true)
                                .poller(p -> p.fixedDelay(5000)))
                .channel(MessageChannels.queue("imapChannel"))
                .get();
    }

    @Bean
    public IntegrationFlow outboundMailFlow() {
        return IntegrationFlow.from("mailChannel")
                .channel(MessageChannels.queue("mailOutputChannel"))
                .get();
    }
}