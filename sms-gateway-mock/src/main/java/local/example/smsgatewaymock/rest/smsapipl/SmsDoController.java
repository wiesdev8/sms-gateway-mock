package local.example.smsgatewaymock.rest.smsapipl;

import static local.example.smsgatewaymock.data.MessageCreators.createMessageForSmsApiPl;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import local.example.smsgatewaymock.validator.AuthorizationVerifier;
import local.example.smsgatewaymock.validator.RecipientPhoneNumberVerifier;
import local.example.smsgatewaymock.data.SentMessagesCache;
import local.example.smsgatewaymock.exception.SmsDoException;
import local.example.smsgatewaymock.validator.SmsDoRequestParametersVerifier;
import local.example.smsgatewaymock.data.SmsDoResponseGenerator;
import local.example.smsgatewaymock.dto.SmsDoError;
import local.example.smsgatewaymock.dto.SmsDoResponse;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping(SmsDoController.PATH_TO_SMS_DO)
@AllArgsConstructor
@Slf4j
public class SmsDoController {

    static final String PASSWORD = "password";
    static final String FORMAT = "format";
    static final String DETAILS = "details";
    static final String FROM = "from";
    static final String TO = "to";
    static final String ENCODING = "encoding";
    static final String MESSAGE = "message";
    static final String USERNAME = "username";

    static final String PATH_TO_SMS_DO = "/sms.do";

    static final String USER_AGENT_HEADER_NAME = "User-Agent";
    private static final String CONTENT_TYPE_HEADER_NAME = "Content-Type";

    private final SentMessagesCache sentMessagesCache;
    private final SmsDoResponseGenerator responseGenerator;
    private final AuthorizationVerifier authorizationVerifier;
    private final SmsDoRequestParametersVerifier smsDoRequestParametersVerifier;
    private final RecipientPhoneNumberVerifier recipientPhoneNumberVerifier;

    @RequestMapping(
            method = {RequestMethod.GET, RequestMethod.POST},
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public ResponseEntity<SmsDoResponse> doSms(@RequestParam(PASSWORD) String password,
                                               @RequestParam(FORMAT) String format,
                                               @RequestParam(DETAILS) String details,
                                               @RequestParam(FROM) String from,
                                               @RequestParam(TO) String recipientPhoneNumber,
                                               @RequestParam(ENCODING) String encoding,
                                               @RequestParam(MESSAGE) String message,
                                               @RequestParam(USERNAME) String username,
                                               @RequestHeader(USER_AGENT_HEADER_NAME) String userAgent,
                                               @RequestHeader(CONTENT_TYPE_HEADER_NAME) String contentType
                                              ) throws UnsupportedEncodingException {
        String decodedUsername = decode(username);
        String decodedMessage = decode(message);
        log.debug("SMS API PL provider mock - received 'sms do' request with " +
                        "recipientPhoneNumber='{}', text='{}'",
                recipientPhoneNumber, decodedMessage);

        authorizationVerifier.verifyAuthorizationToSmsapiPl(decodedUsername, password);
        smsDoRequestParametersVerifier.verifyThat("format", format, "json");
        smsDoRequestParametersVerifier.verifyThat("details", details, "1");
        smsDoRequestParametersVerifier.verifyThat("from", from, "2way");
        smsDoRequestParametersVerifier.verifyThat("encoding", encoding, "utf-8");

        recipientPhoneNumberVerifier.verify(recipientPhoneNumber);

        SmsDoResponse response = responseGenerator.generateUsing(recipientPhoneNumber);

        sentMessagesCache.addMessage(response.getList().get(0).getNumber(),
                createMessageForSmsApiPl(response.getList().get(0).getId(),
                        decodedMessage,
                        decodedUsername));

        return ResponseEntity.ok(response);
    }

    @ExceptionHandler(value = {
            SmsDoException.class
    })
    public <T extends SmsDoException> ResponseEntity<SmsDoError> handelException(T exception) {
        return ResponseEntity.status(HttpStatus.OK).contentType(APPLICATION_JSON_UTF8).body(exception.getSmsDoError());
    }

    private String decode(String s) throws UnsupportedEncodingException {
        return URLDecoder.decode(s, UTF_8.toString());
    }
}
