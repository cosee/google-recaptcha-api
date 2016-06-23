package biz.cosee.web.google.recaptcha;

import org.junit.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import java.text.SimpleDateFormat;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

public class ReCaptchaVerifierTest {

    public static final String CORRECT_SECRET = "s1";

    private RestTemplate restTemplate = new RestTemplate();

    @Test
    public void successfullyVerifyReCaptchaForRealUser() {
        MockRestServiceServer server = mockRestServer(CORRECT_SECRET, "aaa", true);
        assertThat(new ReCaptchaVerifier(CORRECT_SECRET, restTemplate).isHumanUser("aaa")).isTrue();
        server.verify();
    }

    @Test
    public void successfullyVerifyReCaptchaForComputer() {
        MockRestServiceServer server = mockRestServer(CORRECT_SECRET, "ccc", false);
        assertThat(new ReCaptchaVerifier(CORRECT_SECRET, restTemplate).isHumanUser("ccc")).isFalse();
        server.verify();
    }

    private MockRestServiceServer mockRestServer(String secret, String captchaValue, boolean success) {
        MockRestServiceServer server = MockRestServiceServer.createServer(restTemplate);
        server
                .expect(requestTo("https://www.google.com/recaptcha/api/siteverify"))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withSuccess("{\n" +
                        "  \"success\": " + success + ",\n" +
                        "  \"challenge_ts\": \"" + new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZZ").format(new Date()) + "\",\n" +
                        "  \"hostname\": \"localhost\"" +
                        "}", MediaType.APPLICATION_JSON));
        return server;
    }

    @Test(expected = ReCaptchaVerificationException.class)
    public void failVerifyingReCaptcha() {
        mockRestServerWithError("missing-input-secret");
        new ReCaptchaVerifier(null, restTemplate).isHumanUser("bbb");
    }

    private MockRestServiceServer mockRestServerWithError(String errors) {
        MockRestServiceServer server = MockRestServiceServer.createServer(restTemplate);
        server
                .expect(requestTo("https://www.google.com/recaptcha/api/siteverify"))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withSuccess("{\n" +
                        "  \"challenge_ts\": \"" + new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZZ").format(new Date()) + "\",\n" +
                        "  \"hostname\": \"localhost\"," +
                        "  \"error-codes\": [\"" + errors + "\"]" +
                        "}", MediaType.APPLICATION_JSON));
        return server;
    }
}
