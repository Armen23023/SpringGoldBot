package am.relex.Controller;

import am.relex.service.UpdateProducer;
import am.relex.utils.MessageUtils;
import lombok.extern.log4j.Log4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import static am.relex.model.RabbitQueue.*;

@Component
@Log4j
public class UpdateController {

    private TelegramBot telegramBot;

    private final MessageUtils messageUtils;

    private final UpdateProducer updateProducer;

    public UpdateController(MessageUtils messageUtils, UpdateProducer updateProducer){
        this.messageUtils= messageUtils;
        this.updateProducer = updateProducer;
    }

    public void registerBot(TelegramBot telegramBot){
        this.telegramBot = telegramBot;
    }


    public void processUpdate(Update update){
        if (update==null){
            log.error("Received update is null");
            return;
        }


        if (update.hasMessage()){
            distributeMessageByType(update);
        }else {
            log.error("Unsupported message type is received:   " + update);
        }
    }

    private void distributeMessageByType(Update update) {
        var message = update.getMessage();

        if (message.hasText()) {
            processTextMessage(update);
        }else if(message.hasDocument()){
            processDocMessage(update);
        } else if (message.hasPhoto()) {
            processPhotoMessage(update);
        }else {
            setUnsupportedMessageTypeView(update);
        }
    }

    private void setUnsupportedMessageTypeView(Update update) {
        var sendMessage = messageUtils.generateSendMessageWithText(update,"Unsupported message type!");
        setView(sendMessage);
    }

    private void setFileIsReceivedView(Update update) {
        var sendMessage = messageUtils.generateSendMessageWithText(update,"Ֆայլը բեռնվել է , խնդրում ենք սպասել ․․․");
            setView(sendMessage);
    }

    public void setView(SendMessage sendMessage){
        telegramBot.sendAnswerMessage(sendMessage);
    }

    private void processPhotoMessage(Update update) {
        updateProducer.producer(PHOTO_MESSAGE_UPDATE ,update);

    }



    private void processDocMessage(Update update) {
         updateProducer.producer(DOC_MESSAGE_UPDATE,update);
         setFileIsReceivedView(update);
    }

    private void processTextMessage(Update update) {
        updateProducer.producer(TEXT_MESSAGE_UPDATE,update);
    }
}
