package local.example.smsgatewaymock.service;

import static local.example.smsgatewaymock.RandomPhoneNumberGenerator.generateRandomPhoneNumber;
import static local.example.smsgatewaymock.dto.SmsApiProvider.SMSAPI_PL;
import static local.example.smsgatewaymock.service.SmsApiPlCallbackClient.MSG_ID;
import static local.example.smsgatewaymock.service.SmsApiPlCallbackClient.PATH_TO_SMSAPI_CALLBACK;
import static local.example.smsgatewaymock.service.SmsApiPlCallbackClient.SMS_DATE;
import static local.example.smsgatewaymock.service.SmsApiPlCallbackClient.SMS_FROM;
import static local.example.smsgatewaymock.service.SmsApiPlCallbackClient.SMS_TEXT;
import static local.example.smsgatewaymock.service.SmsApiPlCallbackClient.SMS_TO;
import static local.example.smsgatewaymock.service.SmsApiPlCallbackClient.USERNAME;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED;
import static org.springframework.http.MediaType.TEXT_PLAIN;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.queryParam;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.client.MockRestServiceServer;

import local.example.smsgatewaymock.data.DateSentProvider;
import local.example.smsgatewaymock.data.SentMessagesCache;
import local.example.smsgatewaymock.dto.Message;
import local.example.smsgatewaymock.dto.SmsReplyRequest;

import lombok.extern.slf4j.Slf4j;

@RunWith(SpringRunner.class)
@RestClientTest(components = {SmsApiPlCallbackClient.class})
@Slf4j
public class SmsApiPlCallbackClientTest {

    private static final String TEXT_IN_REPLY = "text in reply";
    private static final String OK = "OK";

    @Autowired
    private MockRestServiceServer server;

    @Autowired
    private SmsApiPlCallbackClient smsApiPlCallbackClient;

    @MockBean
    private SentMessagesCache sentMessagesCache;

    private SmsReplyRequest request;
    private Message message;

    @Before
    public void mockMessagesSmsCallbackRestService() {
        message = new Message(SMSAPI_PL, "1001", "text in message", "username");
        request = new SmsReplyRequest(
                generateRandomPhoneNumber(),
                TEXT_IN_REPLY);

        when(sentMessagesCache.getMessage(request.getFrom()))
                .thenReturn(message);

        server.expect(requestTo(containsString(PATH_TO_SMSAPI_CALLBACK)))
                .andExpect(method(POST))
                .andExpect(content().contentType(APPLICATION_FORM_URLENCODED))
                .andExpect(queryParam(SMS_FROM, notNullValue()))
                .andExpect(queryParam(SMS_TO, notNullValue()))
                .andExpect(queryParam(SMS_DATE, notNullValue()))
                .andExpect(queryParam(SMS_TEXT, notNullValue()))
                .andExpect(queryParam(USERNAME, notNullValue()))
                .andExpect(queryParam(MSG_ID, equalTo(message.getId())))
                .andRespond(withSuccess(OK, TEXT_PLAIN));
    }

    @Test
    public void should_send_sms_reply_to_messages_sms_callback_rest_service() {
        String fromPhoneNumber = request.getFrom();
        String text = request.getText();
        String id = message.getId();
        String username = message.getUsername();
        ResponseEntity<String> response = smsApiPlCallbackClient
                .sendSmsReply(fromPhoneNumber, text, id, username);
        log.info(response.toString());
        assertThat(response.getStatusCode()).isEqualTo((HttpStatus.OK));
        assertThat(response.getBody()).isEqualTo(OK);
    }

    @TestConfiguration
    @ComponentScan(basePackageClasses = {DateSentProvider.class})
    static class Config {
    }


}