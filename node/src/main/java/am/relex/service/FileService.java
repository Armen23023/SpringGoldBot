package am.relex.service;

import am.relex.entity.GoldData;
import am.relex.service.enums.LinkType;
import org.telegram.telegrambots.meta.api.objects.Message;

public interface FileService {
    GoldData processPhoto(Message telegramMessage);
    String generateLink(Long docId, LinkType linkType);
}
