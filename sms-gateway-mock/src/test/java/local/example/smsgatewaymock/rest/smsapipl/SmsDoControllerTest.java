package local.example.smsgatewaymock.rest.smsapipl;

import static local.example.smsgatewaymock.data.SmsDoResponseGenerator.COUNT;
import static local.example.smsgatewaymock.data.SmsDoResponseGenerator.PARTS;
import static local.example.smsgatewaymock.data.SmsDoResponseGenerator.POINTS;
import static local.example.smsgatewaymock.dto.SmsApiProvider.SMSAPI_PL;
import static local.example.smsgatewaymock.dto.SmsDeliveryStatus.QUEUE;
import static local.example.smsgatewaymock.rest.smsapipl.SmsDoController.PATH_TO_SMS_DO;
import static local.example.smsgatewaymock.rest.smsapipl.SmsDoController.TO;
import static local.example.smsgatewaymock.rest.smsapipl.SmsDoController.USER_AGENT_HEADER_NAME;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.apache.commons.lang3.RandomStringUtils.randomNumeric;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.isEmptyOrNullString;
import static org.hamcrest.Matchers.not;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.log;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.net.URLEncoder;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import local.example.smsgatewaymock.configuration.SmsApiMockProperties;
import local.example.smsgatewaymock.data.SentMessagesCache;
import local.example.smsgatewaymock.dto.SmsDoResponse;
import com.fasterxml.jackson.databind.ObjectMapper;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class SmsDoControllerTest {

    private static final String PHONE_NUMBER_PREFIX = "+";
    private static final String SAMPLE_SMS_TEXT = "sample sms text";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private SentMessagesCache sentMessagesCache;

    @Autowired
    private SmsApiMockProperties properties;

    @Test
    public void should_receive_successfully_sms_message_sent_to_api_sms_do_using_method_get() throws Exception {
        MockHttpServletRequestBuilder requestWithMethodGet = get(PATH_TO_SMS_DO);
        should_receive_successfully_sms_message_sent_to_api_sms_do_using(requestWithMethodGet);
    }

    @Test
    public void should_receive_successfully_sms_message_sent_to_api_sms_do_using_method_post() throws Exception {
        MockHttpServletRequestBuilder requestWithMethodPost = post(PATH_TO_SMS_DO);
        should_receive_successfully_sms_message_sent_to_api_sms_do_using(requestWithMethodPost);
    }

    @Test
    public void should_respond_with_error_101_when_given_user_is_not_authorized() throws Exception {

        String recipientsCorrectNumbersStartsWith = properties.getRecipientsCorrectNumbersStartWith();
        String recipientPhoneNumber = PHONE_NUMBER_PREFIX + recipientsCorrectNumbersStartsWith
                + randomNumeric(11 - recipientsCorrectNumbersStartsWith.length());

        assertThat(sentMessagesCache.hasMessage(recipientPhoneNumber)).isFalse();

        String password = "ff54c7d56126324ef313f7e6bbc24df8";
        String username = "authorizationError";

        mockMvc.perform(
                get(PATH_TO_SMS_DO)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .header(USER_AGENT_HEADER_NAME, "smsapi-lib/java Windows 10")
                        .param(SmsDoController.PASSWORD, password)
                        .param(SmsDoController.FORMAT, "json")
                        .param(SmsDoController.DETAILS, "1")
                        .param(SmsDoController.FROM, "2way")
                        .param(TO, recipientPhoneNumber)
                        .param(SmsDoController.ENCODING, UTF_8.toString().toLowerCase())
                        .param(SmsDoController.MESSAGE, URLEncoder.encode(SAMPLE_SMS_TEXT, UTF_8.toString()))
                        .param(SmsDoController.USERNAME, username)
                       )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.error", equalTo(101)))
                .andExpect(jsonPath("$.message", not(isEmptyOrNullString())))
                .andDo(log())
                .andReturn().getResponse().getContentAsString();
        assertThat(sentMessagesCache.hasMessage(recipientPhoneNumber)).isFalse();
    }

    @Test
    public void should_respond_with_error_901_when_given_parameter_is_incorrect() throws Exception {

        String recipientsCorrectNumbersStartsWith = properties.getRecipientsCorrectNumbersStartWith();
        String recipientPhoneNumber = PHONE_NUMBER_PREFIX + recipientsCorrectNumbersStartsWith
                + randomNumeric(11 - recipientsCorrectNumbersStartsWith.length());

        assertThat(sentMessagesCache.hasMessage(recipientPhoneNumber)).isFalse();

        String password = "ff54c7d56126324ef313f7e6bbc24df8";
        String username = "incorrectParameter";
        properties.getAuthorizedUsersSmsapiPl().put(username, password);

        mockMvc.perform(
                get(PATH_TO_SMS_DO)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .header(USER_AGENT_HEADER_NAME, "smsapi-lib/java Windows 10")
                        .param(SmsDoController.PASSWORD, password)
                        .param(SmsDoController.FORMAT, "json")
                        .param(SmsDoController.DETAILS, "1")
                        .param(SmsDoController.FROM, "1way")
                        .param(TO, recipientPhoneNumber)
                        .param(SmsDoController.ENCODING, UTF_8.toString().toLowerCase())
                        .param(SmsDoController.MESSAGE, URLEncoder.encode(SAMPLE_SMS_TEXT, UTF_8.toString()))
                        .param(SmsDoController.USERNAME, username)
                       )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.error", equalTo(901)))
                .andExpect(jsonPath("$.message", not(isEmptyOrNullString())))
                .andDo(log())
                .andReturn().getResponse().getContentAsString();
        assertThat(sentMessagesCache.hasMessage(recipientPhoneNumber)).isFalse();
    }

    @Test
    public void should_respond_with_error_13_when_given_recipient_phone_number_is_incorrect() throws Exception {

        String invalidRecipientPhoneNumber = randomNumeric(8);

        assertThat(sentMessagesCache.hasMessage(invalidRecipientPhoneNumber)).isFalse();

        String password = "ff54c7d56126324ef313f7e6bbc24df8";
        String username = "incorrectPhoneNumber";
        properties.getAuthorizedUsersSmsapiPl().put(username, password);

        mockMvc.perform(
                get(PATH_TO_SMS_DO)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .header(USER_AGENT_HEADER_NAME, "smsapi-lib/java Windows 10")
                        .param(SmsDoController.PASSWORD, password)
                        .param(SmsDoController.FORMAT, "json")
                        .param(SmsDoController.DETAILS, "1")
                        .param(SmsDoController.FROM, "2way")
                        .param(TO, invalidRecipientPhoneNumber)
                        .param(SmsDoController.ENCODING, UTF_8.toString().toLowerCase())
                        .param(SmsDoController.MESSAGE, URLEncoder.encode(SAMPLE_SMS_TEXT, UTF_8.toString()))
                        .param(SmsDoController.USERNAME, username)
                       )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.error", equalTo(13)))
                .andExpect(jsonPath("$.message", not(isEmptyOrNullString())))
                .andExpect(jsonPath("$.invalid_numbers", hasSize(1)))
                .andExpect(jsonPath("$.invalid_numbers[0].number", equalTo(invalidRecipientPhoneNumber)))
                .andExpect(jsonPath("$.invalid_numbers[0].submitted_number", equalTo(properties.getSubmittedNumber())))
                .andExpect(jsonPath("$.invalid_numbers[0].message", not(isEmptyOrNullString())))
                .andDo(log())
                .andReturn().getResponse().getContentAsString();
        assertThat(sentMessagesCache.hasMessage(invalidRecipientPhoneNumber)).isFalse();
    }

    private void should_receive_successfully_sms_message_sent_to_api_sms_do_using(MockHttpServletRequestBuilder method) throws Exception {

        String recipientsCorrectNumbersStartsWith = properties.getRecipientsCorrectNumbersStartWith();
        String recipientPhoneNumber = PHONE_NUMBER_PREFIX + recipientsCorrectNumbersStartsWith
                + randomNumeric(11 - recipientsCorrectNumbersStartsWith.length());

        assertThat(sentMessagesCache.hasMessage(recipientPhoneNumber)).isFalse();

        String password = "ff54c7d56126324ef313f7e6bbc24df8";
        String username = "smsapimock";
        properties.getAuthorizedUsersSmsapiPl().put(username, password);

        String responseContent = mockMvc
                .perform(
                        method
                                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                                .header(USER_AGENT_HEADER_NAME, "smsapi-lib/java Windows 10")
                                .param(SmsDoController.PASSWORD, password)
                                .param(SmsDoController.FORMAT, "json")
                                .param(SmsDoController.DETAILS, "1")
                                .param(SmsDoController.FROM, "2way")
                                .param(TO, recipientPhoneNumber)
                                .param(SmsDoController.ENCODING, UTF_8.toString().toLowerCase())
                                .param(SmsDoController.MESSAGE, URLEncoder.encode(SAMPLE_SMS_TEXT, UTF_8.toString()))
                                .param(SmsDoController.USERNAME, username)
                        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.count", equalTo(COUNT)))
                .andExpect(jsonPath("$.parts", equalTo(PARTS)))
                .andExpect(jsonPath("list", hasSize(COUNT)))
                .andExpect(jsonPath("list[0].id", not(isEmptyOrNullString())))
                .andExpect(jsonPath("list[0].points", equalTo(POINTS)))
                .andExpect(jsonPath("list[0].number", equalTo(recipientPhoneNumber)))
                .andExpect(jsonPath("list[0].date_sent", not(isEmptyOrNullString())))
                .andExpect(jsonPath("list[0].submitted_number", equalTo(properties.getSubmittedNumber())))
                .andExpect(jsonPath("list[0].status", equalTo(QUEUE.name())))
                .andDo(log())
                .andReturn().getResponse().getContentAsString();
        assertThat(sentMessagesCache.hasMessage(recipientPhoneNumber)).isTrue();
        String messageId = objectMapper.readValue(responseContent, SmsDoResponse.class).getList().get(0).getId();
        assertThat(sentMessagesCache.getMessage(recipientPhoneNumber).getId()).isEqualTo(messageId);
        assertThat(sentMessagesCache.getMessage(recipientPhoneNumber).getSmsApiProvider()).isEqualTo(SMSAPI_PL);
    }


}