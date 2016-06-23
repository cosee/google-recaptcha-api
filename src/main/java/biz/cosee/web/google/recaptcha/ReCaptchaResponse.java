package biz.cosee.web.google.recaptcha;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ReCaptchaResponse {

    private Boolean success;

    private String challenge_ts;

    private String hostname;

    private String[] errorCodes;

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public String getChallenge_ts() {
        return challenge_ts;
    }

    public void setChallenge_ts(String challenge_ts) {
        this.challenge_ts = challenge_ts;
    }

    public String getHostname() {
        return hostname;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    public String[] getErrorCodes() {
        return errorCodes;
    }

    @JsonProperty("error-codes")
    public void setErrorCodes(String[] errorCodes) {
        this.errorCodes = errorCodes;
    }

    public boolean hasErrors() {
        return (errorCodes != null) && (errorCodes.length > 0);
    }
}
