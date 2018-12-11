package local.example.smsgatewaymock.rest;

import static local.example.smsgatewaymock.RandomPhoneNumberGenerator.generateRandomPhoneNumber;
import static local.example.smsgatewaymock.data.MessageCreators.createMessageForBlueMediaPremiumSms;
import static local.example.smsgatewaymock.data.MessageCreators.createMessageForSmsApiPl;
import static local.example.smsgatewaymock.rest.SmsRepliesController.PATH_TO_SMS_REPLIES;
import static local.example.smsgatewaymock.rest.SmsRepliesController.RESPONSE_BODY_OK;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.log;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.function.Supplier;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.ResultMatcher;

import local.example.smsgatewaymock.configuration.BlueMediaReceiverProperties;
import local.example.smsgatewaymock.data.SentMessagesCache;
import local.example.smsgatewaymock.dto.Message;
import local.example.smsgatewaymock.dto.SmsReplyRequest;
import local.example.smsgatewaymock.service.SmsApiPlCallbackClient;
import local.example.smsgatewaymock.ws.bluemedia.SmsReceiverClient;
import local.example.smsgatewaymock.ws.bluemedia.ReceiveSmsRequest;
import local.example.smsgatewaymock.ws.bluemedia.ReceiveSmsResponse;
import local.example.ws.bluemedia.sms.receiver.Sms;
import com.fasterxml.jackson.databind.ObjectMapper;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class SmsRepliesControllerTest {

    private static final String REPLY_TEXT = "reply text";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private SentMessagesCache sentMessagesCache;

    @Autowired
    private BlueMediaReceiverProperties receiverProperties;

    @MockBean
    private SmsApiPlCallbackClient smsApiPlCallbackClient;

    @MockBean
    private SmsReceiverClient smsReceiverClient;

    private SmsReplyRequest request;
    private Message message;

    @Before
    public void init() {
        request = new SmsReplyRequest(
                generateRandomPhoneNumber(),
                REPLY_TEXT);
    }

    @Test
    public void should_send_sms_reply_using_smsapi_pl_sms_provider() throws Exception {
        //given
        message = createMessageForSmsApiPl("id", "text", "user");
        sentMessagesCache.addMessage(request.getFrom(), message);

        when(smsApiPlCallbackClient.sendSmsReply(request.getFrom(), REPLY_TEXT, message.getId(), message.getUsername()))
                .thenReturn(ResponseEntity.status(HttpStatus.OK).contentType(APPLICATION_JSON_UTF8)
                        .body(RESPONSE_BODY_OK));
        //when
        ResultActions response = send_sms_reply();

        //then
        verify_response_from_send_sms_reply(response, () -> status().isCreated(), RESPONSE_BODY_OK);
    }

    @Test
    public void should_reply_with_status_bad_request_when_something_went_wrong_in_messages() throws Exception {
        //given
        message = createMessageForSmsApiPl("id", "text", "user");
        sentMessagesCache.addMessage(request.getFrom(), message);

        when(smsApiPlCallbackClient.sendSmsReply(request.getFrom(), REPLY_TEXT, message.getId(), message.getUsername()))
                .thenReturn(ResponseEntity.status(HttpStatus.OK).contentType(APPLICATION_JSON_UTF8)
                        .body("something went wrong"));
        //when
        ResultActions response = send_sms_reply();

        //then
        verify_response_from_send_sms_reply(response, () -> status().isBadRequest(), "something went wrong");

    }

    @Test
    public void should_send_sms_reply_using_blue_media_sms_provider() throws Exception {
        //given
        message = createMessageForBlueMediaPremiumSms("id", "text", "user");
        sentMessagesCache.addMessage(request.getFrom(), message);

        when(smsReceiverClient.send(mock_expected_ReceiveSmsRequest(request.getFrom())))
                .thenReturn(create_ReceiveSmsResponse(1));

        //when
        ResultActions response = send_sms_reply();

        //then
        verify_response_from_send_sms_reply(response, () -> status().isCreated(),
                "Blue Media Receiver has processed SMS successfully");
    }

    @Test
    public void should_not_send_sms_for_bad_request_using_blue_media_sms_provider() throws Exception {
        //given
        message = createMessageForBlueMediaPremiumSms("id", "text", "user");
        sentMessagesCache.addMessage(request.getFrom(), message);

        when(smsReceiverClient.send(mock_expected_ReceiveSmsRequest(request.getFrom())))
                .thenReturn(create_ReceiveSmsResponse(0));

        //when
        ResultActions response = send_sms_reply();

        //then
        verify_response_from_send_sms_reply(response, () -> status().isBadRequest(),
                "Blue Media Receiver can not process SMS");
    }


    @Test
    public void should_fail_with_status_not_found_when_no_message_in_cache() throws Exception {
        //when
        ResultActions response = send_sms_reply();
        //then
        response.andExpect(status().isNotFound());
    }

    private ResultActions send_sms_reply() throws Exception {
        return mockMvc.perform(post(PATH_TO_SMS_REPLIES)
                .content(objectMapper.writeValueAsString(request))
                .contentType(APPLICATION_JSON_UTF8));
    }

    private void verify_response_from_send_sms_reply(ResultActions response,
                                                     Supplier<ResultMatcher> expectedStatus,
                                                     String expectedBody) throws Exception {
        response
                .andExpect(expectedStatus.get())
                .andExpect(content().string(expectedBody))
                .andDo(log());
    }

    private ReceiveSmsRequest mock_expected_ReceiveSmsRequest(String from) {
        Sms sms = new Sms();
        sms.setId(anyLong());
        sms.setNumberOfParts(1);
        sms.setText(REPLY_TEXT);
        sms.setTo(receiverProperties.getReceiverPhoneNumber());
        sms.setOperator(receiverProperties.getOperator());
        sms.setFrom(from);
        ReceiveSmsRequest receiveSmsRequest = new ReceiveSmsRequest();
        receiveSmsRequest.setSms(sms);
        return receiveSmsRequest;
    }

    private ReceiveSmsResponse create_ReceiveSmsResponse(int withResult) {
        ReceiveSmsResponse smsResponse = new ReceiveSmsResponse();
        smsResponse.setResult(withResult);
        return smsResponse;
    }

}