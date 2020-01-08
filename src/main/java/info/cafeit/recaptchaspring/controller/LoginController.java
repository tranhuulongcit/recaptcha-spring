package info.cafeit.recaptchaspring.controller;

import com.google.gson.Gson;
import info.cafeit.recaptchaspring.dto.Captcha;
import info.cafeit.recaptchaspring.dto.LoginDto;
import info.cafeit.recaptchaspring.utils.CafeitNetwoking;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.io.IOException;

@Controller
public class LoginController {

    @Value("${recaptcha.endpoint}")
    private String endpoint;

    @Value("${recaptcha.site-key}")
    private String siteKey;

    @Value("${recaptcha.secret-key}")
    private String secretKey;

    @RequestMapping(value = "/login")
    public String getLogin(Model model) {
        model.addAttribute("loginDTO", new LoginDto());
        model.addAttribute("siteKey", siteKey);
        return "login";
    }


    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public ResponseEntity<?> postLogin(@ModelAttribute LoginDto loginDto) {
        String result = null;
        try {
            Response response = CafeitNetwoking.Post.create()
                    .setUrl(endpoint)
                    .setHeaders("User-Agent", "Mozilla/5.0")
                    .setHeaders("Accept-Language", "en-US,en;q=0.5")
                    .setParams("secret", secretKey)
                    .setParams("response", loginDto.getToken())
                    .build().execute();

            if (response.isSuccessful()) {
                Captcha captcha = new Gson().fromJson(response.body().string(), Captcha.class);
                if (captcha.getSuccess() && captcha.isHuman()) {
                    result = "Hello " + loginDto.getUserName();
                } else {
                    result = "You are a Robot!!";
                }
            } else {
                result = " Request error!!";
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        return new ResponseEntity<String>(result, HttpStatus.OK);
    }

}
