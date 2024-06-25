package am.relex.Controller;

import am.relex.configuration.botConfig.BotConfig;
import lombok.extern.log4j.Log4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import javax.annotation.PostConstruct;


@Component
@Log4j
public class TelegramBot extends TelegramLongPollingBot {


    final BotConfig botConfig;

    private UpdateController updateController;

    public TelegramBot(BotConfig botConfig, UpdateController updateController) {
        this.botConfig = botConfig;
        this.updateController = updateController;
    }


    @PostConstruct
    public void init() {
        updateController.registerBot(this);
    }


    @Override
    public String getBotUsername() {
        return botConfig.getBotName();
    }

    @Override
    public String getBotToken() {
        return botConfig.getToken();
    }

    @Override
    public void onUpdateReceived(Update update) {
        updateController.processUpdate(update);
    }


    public void sendAnswerMessage(SendMessage sendMessage) {
        if (sendMessage != null) {
            try {
                execute(sendMessage);
            } catch (TelegramApiException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
