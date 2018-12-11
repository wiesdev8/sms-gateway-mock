package local.example.smsgatewaymock.service;

import static org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import local.example.smsgatewaymock.configuration.SmsApiMockProperties;
import local.example.smsgatewaymock.data.DateSentProvider;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class SmsApiPlCallbackClient {

    static final String PATH_TO_SMSAPI_CALLBACK = "/smsapi/callback";
    static final String SMS_FROM = "sms_from";
    static final String SMS_TO = "sms_to";
    static final String SMS_DATE = "sms_date";
    static final String SMS_TEXT = "sms_text";
    static final String USERNAME = "username";
    static final String MSG_ID = "MsgId";

    private final RestTemplate restTemplate;
    private final HttpEntity<String> requestEntity;
    private final DateSentProvider dateSentProvider;
    private final SmsApiMockProperties smsApiMockProperties;

    SmsApiPlCallbackClient(RestTemplateBuilder restTemplateBuilder,
                                  DateSentProvider dateSentProvider,
                                  SmsApiMockProperties smsApiMockProperties) {
        this.restTemplate = restTemplateBuilder.build();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(APPLICATION_FORM_URLENCODED);
        this.requestEntity = new HttpEntity<>("", headers);
        this.dateSentProvider = dateSentProvider;
        this.smsApiMockProperties = smsApiMockProperties;
    }

    public ResponseEntity<String> sendSmsReply(String fromPhoneNumber,
                                               String text, String messageId,
                                               String username) {

        UriComponents uri = UriComponentsBuilder
                .fromUriString(smsApiMockProperties.getMessagesServiceUrl())
                .path(PATH_TO_SMSAPI_CALLBACK)
                .queryParam(SMS_FROM, fromPhoneNumber)
                .queryParam(SMS_TO, smsApiMockProperties.getSubmittedNumber())
                .queryParam(SMS_DATE, dateSentProvider.provide())
                .queryParam(SMS_TEXT, text)
                .queryParam(USERNAME, username)
                .queryParam(MSG_ID, messageId)
                .build();
        return restTemplate
                .postForEntity(uri.toUri(), requestEntity, String.class);
    }
}
