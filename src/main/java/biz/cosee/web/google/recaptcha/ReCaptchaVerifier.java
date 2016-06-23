package biz.cosee.web.google.recaptcha;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import static java.util.Arrays.asList;

@Service
public class ReCaptchaVerifier {

    private static final Logger logger = LoggerFactory.getLogger(ReCaptchaVerifier.class);

    private final String secret;

    private final RestTemplate restTemplate;

    @Autowired
    public ReCaptchaVerifier(
            @Value("${recaptcha.secret}") String secret,
            RestTemplate restTemplate) {

        this.secret = secret;
        this.restTemplate = restTemplate;
    }

    public boolean isHumanUser(String gCaptchaValue) {

        logger.debug("Checking ReCAPTCHA '{}' using secret '{}'.", gCaptchaValue, secret);

        ReCaptchaResponse response = invokeReCaptchaService(gCaptchaValue);
        if (response.hasErrors()) {
            throw new ReCaptchaVerificationException(response.getErrorCodes());
        }

        return response.getSuccess();
    }

    private ReCaptchaResponse invokeReCaptchaService(String gCaptchaValue) {
        MultiValueMap<String, String> parameters = new LinkedMultiValueMap<>();
        parameters.put("secret", asList(secret));
        parameters.put("response", asList(gCaptchaValue));

        return restTemplate.postForObject("https://www.google.com/recaptcha/api/siteverify",
                parameters,
                ReCaptchaResponse.class);
    }
}
