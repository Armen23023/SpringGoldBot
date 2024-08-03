package am.relex.service.impl;

import am.relex.dto.MailParams;
import am.relex.service.MailSenderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Log4j
@RequiredArgsConstructor
@Service
public class MailSenderServiceImpl implements MailSenderService {

    private final JavaMailSender javaMailSender;
    @Value("${spring.mail.username}")
    private String emailFrom;
    @Value("${service.activation.uri}")

    private String activationServiceUri;
    @Override
    public void send(MailParams mailParams) {
        var subject = "Activation ";
        var messageBody = getActivationMailBody(mailParams.getId());
        var emailTo = mailParams.getEmailTo();
        log.debug(String.format("Sending email for mail=[%s]", emailTo));


        var mailMessage = new SimpleMailMessage();
        mailMessage.setFrom(emailFrom);
        mailMessage.setTo(emailTo);
        mailMessage.setSubject(subject);
        assert messageBody != null;
        mailMessage.setText(messageBody);

        javaMailSender.send(mailMessage);
    }

    private String getActivationMailBody(String id) {
        var msg  = String.format("Բոտում գրանցումը ավարտելու համար անցեք հղումով: \n%s",
                activationServiceUri);
        return msg.replace("{id}" , id);
    }
}
