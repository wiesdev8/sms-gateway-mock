package local.example.smsgatewaymock.rest;

import static local.example.smsgatewaymock.RandomPhoneNumberGenerator.generateRandomPhoneNumber;
import static local.example.smsgatewaymock.data.MessageCreators.createMessageForSmsApiPl;
import static local.example.smsgatewaymock.rest.SentMessagesController.PATH_TO_SENT_MESSAGE;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.log;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import local.example.smsgatewaymock.data.MessageIdGenerator;
import local.example.smsgatewaymock.data.SentMessagesCache;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class SentMessagesControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private SentMessagesCache sentMessagesCache;

    @Autowired
    private MessageIdGenerator messageIdGenerator;

    @Test
    public void should_send_successfully_sms_reply_to_messages_sms_callback_rest_service() throws Exception {

        String sentTo = generateRandomPhoneNumber();
        String text_message = "text message";
        String id = messageIdGenerator.generate();
        String username = "username";
        sentMessagesCache.addMessage(sentTo,
                createMessageForSmsApiPl(id, text_message, username));

        mockMvc
                .perform(
                        get(PATH_TO_SENT_MESSAGE, sentTo))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.smsApiProvider", equalTo("SMSAPI_PL")))
                .andExpect(jsonPath("$.id", equalTo(id)))
                .andExpect(jsonPath("$.text", equalTo(text_message)))
                .andExpect(jsonPath("$.username", equalTo(username)))
                .andExpect(jsonPath("$.*", hasSize(4)))
                .andDo(log())
                .andReturn().getResponse().getContentAsString();
    }

    @Test
    public void should_respond_with_status_not_found_when_no_message_in_cache_for_given_phone_number() throws Exception {

        String sentTo = generateRandomPhoneNumber();

        mockMvc
                .perform(
                        get(PATH_TO_SENT_MESSAGE, sentTo))
                .andExpect(status().isNotFound())
                .andDo(log())
                .andReturn().getResponse().getContentAsString();
    }
}