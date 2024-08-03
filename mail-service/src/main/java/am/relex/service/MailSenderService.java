package am.relex.service;

import am.relex.dto.MailParams;

public interface MailSenderService {
     void send(MailParams mailParams);
}
