package am.relex.Controller;

import am.relex.configuration.botConfig.BotConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.bots.TelegramWebhookBot;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updates.SetWebhook;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import javax.annotation.PostConstruct;


@Component
@RequiredArgsConstructor
@Log4j
public class TelegramBot extends TelegramWebhookBot {

    final BotConfig botConfig;

    private final UpdateProcessor updateProcessor;




    @PostConstruct
    public void init() {
        updateProcessor.registerBot(this);
        try{
            var setWebHook = SetWebhook.builder()
                    .url(getBotUri())
                    .build();
            this.setWebhook(setWebHook);
        } catch (TelegramApiException e) {
            log.error("setWebHook in init method",e);
        }
    }


    public String getBotUsername() {
        return botConfig.getBotName();
    }

    public String getBotToken() {
        return botConfig.getToken();
    }

    public String getBotUri(){return botConfig.getBotUri();}




    public void sendAnswerMessage(SendMessage sendMessage) {
        if (sendMessage != null) {
            try {
                execute(sendMessage);
            } catch (TelegramApiException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public BotApiMethod<?> onWebhookUpdateReceived(Update update) {
        return null;
    }

    @Override
    public String getBotPath() {
        return "/update";
    }
}
