package local.example.smsgatewaymock.rest;

import static local.example.smsgatewaymock.rest.SmsRepliesController.PATH_TO_SMS_REPLIES;
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.log;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import local.example.smsgatewaymock.data.DateSentProvider;
import local.example.smsgatewaymock.dto.SmsReplyRequest;
import com.fasterxml.jackson.databind.ObjectMapper;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class MessageNotFoundTest {

    private static final String REPLY_TEXT = "reply text";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void should_respond_with_status_not_found_when_no_message_in_cache_for_given_phone_number() throws Exception {

        mockMvc.perform(post(PATH_TO_SMS_REPLIES)
                .content(objectMapper.writeValueAsString(new SmsReplyRequest("48100100100", REPLY_TEXT)))
                .contentType(APPLICATION_JSON_UTF8))
                .andExpect(status().isNotFound())
                .andDo(log());
    }

    @TestConfiguration
    @ComponentScan(basePackageClasses = {DateSentProvider.class})
    static class Config {
    }

}