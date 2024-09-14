package am.relex.service.impl;

import am.relex.dao.AppUserDAO;
import am.relex.dao.GoldDataDAO;
import am.relex.dao.RawDataDAO;
import am.relex.entity.AppUser;
import am.relex.entity.RawData;
import am.relex.service.AppUserService;
import am.relex.service.MainService;
import am.relex.service.ProducerService;
import am.relex.service.enums.ServiceCommand;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;

import static am.relex.entity.enums.UserState.BASIC_STATE;
import static am.relex.entity.enums.UserState.WAIT_FOR_EMAIL_STATE;
import static am.relex.service.enums.ServiceCommand.*;

@Service
@Log4j
@RequiredArgsConstructor
public class MainServiceImpl implements MainService {

    private final RawDataDAO rawDataDAO;
    private final ProducerService producerService;
    private final AppUserDAO appUserDAO;
    private final GoldDataDAO goldDataDAO;
    private final AppUserService appUserService;


    @Override
    public void processTextMessage(Update update) {
        saveRawData(update);

        var appUser = findOrSaveAppUser(update);
        var userState = appUser.getState();
        var text = update.getMessage().getText();
        var output = "";


        var serviceCommand = ServiceCommand.fromValue(text);
        if (CANCEL.equals(serviceCommand)) {
            output = cancelProcess(appUser);
        } else if (BASIC_STATE.equals(userState)) {
            output = processServiceCommand(appUser, text);
        } else if (WAIT_FOR_EMAIL_STATE.equals(userState)) {

            output = appUserService.
                    setEmail(appUser, text);


        } else {
            log.error("Unknown user state : " + userState);
            output = "Անհասկանալի խնդիր սեղմեք /cancel և փորձեք կրկին ! ";
        }

        var chatId = update.getMessage().getChatId().toString();
        sendAnswer(output, chatId);
    }

    @Override
    public void processDocMessage(Update update) {
        saveRawData(update);
        var appUser = findOrSaveAppUser(update);
        var chatId = update.getMessage().getChatId().toString();
        if (isNotAllowToSendContent(chatId, appUser)) {
            return;
        }

        //TODO save document

        var answer = "Փաստաթուղթը հաջողությամբ բեռնվել է։ Հղումը՝ " +
                " http://test.ru/get-doc/777";
        sendAnswer(answer, chatId);
    }

    @Override
    public void processPhotoMessage(Update update) {
        saveRawData(update);
        var appUser = findOrSaveAppUser(update);
        var chatId = update.getMessage().getChatId().toString();
        if (isNotAllowToSendContent(chatId, appUser)) {
            return;
        }

        //TODO save photo

        var answer = "Լուսանկար հաջողությամբ բեռնվել է։ Հղումը՝ " +
                " http://test.ru/get-photo/777";
        sendAnswer(answer, chatId);
    }


    private boolean isNotAllowToSendContent(String chatId, AppUser appUser) {
        var userState = appUser.getState();
        if (!appUser.getIsActive()) {
            var error = appUser.getFirstName() + " ջան գրանցվեք կամ ակտիվացրեք ձեր հաշիվը" +
                    " բոտից օգտվելու համար , Շնորհակալություն";
            sendAnswer(error, chatId);
            return true;
        } else if (!BASIC_STATE.equals(userState)) {
            var error = "Չեղարկեք հետևյալ հրամանգը սեղմելով /cancel";
            sendAnswer(error, chatId);
            return true;
        }
        return false;
    }


    private void sendAnswer(String output, String chatId) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(output);
        producerService.produceAnswer(sendMessage);
    }

    private String processServiceCommand(AppUser appUser, String cmd) {
        if (REGISTRATION.equals(cmd)) {
            return appUserService.registerUser(appUser);
        } else if (HELP.equals(cmd)) {
            return help();
        } else if (START.equals(cmd)) {
            return "Ողջույն " + appUser.getFirstName() + " ջան։ " +
                    "Որպեսզի ծանոթանաս bot-ի հրահանգների հետ խորհուրդ ենք տալիս " +
                    "մուտքագրել /help ";
        } else if (PUT.equals(cmd)) {
            return put(appUser);
        } else if (GET.equals(cmd)) {
            return get(appUser);
        } else {
            return "Անհասկանալի հրահանգ մուտքագրեք /help";
        }

    }

    private String get(AppUser appUser) {
        return null;
    }

    private String put(AppUser appUser) {
        return null;
    }

    private String help() {
        return "Հասանելի հրահանգների ցանկը ։\n" +
                "/cancel - չեղարկել ներկայիս հրահանգի իրականացումը \n" +
                "/registration - գրանցվել \n";

        //TODO: add another commands
    }

    private String cancelProcess(AppUser appUser) {
        appUser.setState(BASIC_STATE);
        appUserDAO.save(appUser);
        return "Command is canceled";
    }


    private AppUser findOrSaveAppUser(Update update) {
        User telegramUser = update.getMessage().getFrom();

        var optional = appUserDAO.findByTelegramUserId(telegramUser.getId());
        if (optional.isEmpty()) {
            AppUser transientAppUser = AppUser.builder()
                    .telegramUserId(telegramUser.getId())
                    .username(telegramUser.getUserName())
                    .firstName(telegramUser.getFirstName())
                    .lastName(telegramUser.getLastName())
                    .isActive(false)
                    .state(BASIC_STATE)
                    .build();
            return appUserDAO.save(transientAppUser);
        }
        return optional.get();

    }


    private void saveRawData(Update update) {
        RawData rawData = RawData.builder()
                .event(update)
                .build();
        rawDataDAO.save(rawData);
    }
}
