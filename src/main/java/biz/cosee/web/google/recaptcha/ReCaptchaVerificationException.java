package biz.cosee.web.google.recaptcha;

import java.util.Arrays;

public class ReCaptchaVerificationException extends RuntimeException {

    private final String[] errorCodes;

    public ReCaptchaVerificationException(String[] errorCodes) {
        super("ReCapta verification failed: " + Arrays.toString(errorCodes));
        this.errorCodes = errorCodes;
    }
}
