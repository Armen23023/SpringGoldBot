package am.relex.service.impl;

import am.relex.dao.AppUserDAO;
import am.relex.dto.MailParams;
import am.relex.entity.AppUser;
import am.relex.service.AppUserService;
import lombok.extern.log4j.Log4j;
import org.hashids.Hashids;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

import static am.relex.entity.enums.UserState.BASIC_STATE;
import static am.relex.entity.enums.UserState.WAIT_FOR_EMAIL_STATE;
@Log4j
@Service
public class AppUserServiceImpl implements AppUserService {
    private final AppUserDAO appUserDAO;
    private final Hashids hashids;

    @Value("${service.mail.uri}")
    private String mailServiceUri;

    public AppUserServiceImpl(AppUserDAO appUserDAO, Hashids cryptoTool) {
        this.appUserDAO = appUserDAO;
        this.hashids = cryptoTool;
    }

    @Override
    public String registerUser(AppUser appUser) {
        if (appUser.getIsActive()){
            return  "Դուք արդեն գրանցվել եք";
        }else if (appUser.getEmail()!= null){
            return "Ձեր էլ․հասցեին ուղղարկվել է հաստատման նամակ։" +
                    " \n Անցեք այդ հղումով որպեսզի հաստատեք գրանցումը շ";
        }
        appUser.setState(WAIT_FOR_EMAIL_STATE);
        appUserDAO.save(appUser);
        return "Խնդրում եմ մուտքագրեք ձեր էլ հասցեն:";
    }

    @Override
    public String setEmail(AppUser appUser, String email) {
        try {
            InternetAddress emailAddress = new InternetAddress(email);
            emailAddress.validate();
        }catch (AddressException e){
            return "Մուտքագրեք ճիշտ էլ․հասցե  /cancel"  ;
        }
        var optional =  appUserDAO.findByEmail(email);
        if (optional.isEmpty()){
            appUser.setEmail(email);
            appUser.setState(BASIC_STATE);
            appUser = appUserDAO.save(appUser);

            var cryptoUserId = hashids.encode(appUser.getId());
            var response = sendRequestToMailService(cryptoUserId,email);
            if (response.getStatusCode() != HttpStatus.OK){
                var msg =  String.format("Հաղորդագրությունը "+ email + " էլ․ հասցեին ուղարկվեց");
                log.error(msg);
                appUser.setEmail(null);
                appUserDAO.save(appUser);
                return msg;
            }
            return "Ձեր էլ․ հասցեին ուղարկվել է հաղորթագրություն " +
                    " Անցեք հղումով որպեսզի հաստատեք գրանցումը";
        }else {
            return "Հետևյալ էլ․ հասցեն արդեն օգտագործվում է, մուտքագրեք մեկ այլ էլ հասցե։" +
                    "\n հրահանգը չեղարկելու համար մուտքագրեք /cancel";
        }
    }

    private ResponseEntity<String> sendRequestToMailService(String cryptoUserId, String email) {
        var restTemplate = new RestTemplate();
        var headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        var mailParams = MailParams.builder()
                .id(cryptoUserId)
                .emailTo(email)
                .build();

        var request =  new HttpEntity<MailParams>(mailParams,headers);
        return restTemplate.exchange(mailServiceUri,
                HttpMethod.POST,
                request,
                String.class);

    }
}
