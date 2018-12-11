package local.example.smsgatewaymock.service;

import static local.example.smsgatewaymock.data.MessageCreators.createMessageForSmsApiPl;
import static local.example.smsgatewaymock.dto.SmsApiProvider.SMSAPI_PL;
import static java.util.Collections.emptyMap;
import static org.assertj.core.api.Assertions.assertThat;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.function.BiFunction;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import local.example.smsgatewaymock.data.SentMessagesCache;
import local.example.smsgatewaymock.dto.Message;
import local.example.smsgatewaymock.dto.SmsApiProvider;
import local.example.smsgatewaymock.dto.SmsReplyRequest;

import lombok.extern.slf4j.Slf4j;

@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class SmsSenderTest {

    @Autowired
    private SmsSender smsSender;

    @Autowired
    private SentMessagesCache sentMessagesCache;

    @Test
    public void should_not_process_request_when_send_sms_method_is_not_defined_for_sms_api_provider() throws
            NoSuchFieldException, IllegalAccessException {
        //given
        Field sendSmsMethodsField = SmsSender.class.getDeclaredField("sendSmsMethods");
        sendSmsMethodsField.setAccessible(true);

        sendSmsMethodsField.set(smsSender, emptyMap());

        Message message = createMessageForSmsApiPl("id", "text", "user");
        sentMessagesCache.addMessage("phoneNumber", message);

        //when
        ResponseEntity responseEntity = smsSender.sendSmsReply(new SmsReplyRequest("phoneNumber", "reply text"));

        //then
        assertThat(responseEntity.getStatusCode())
                .isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
        assertThat(responseEntity.getBody())
                .isEqualTo("No send sms reply method for SMS API Provider: " + SMSAPI_PL);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void should_have_send_sms_method_for_each_sms_api_provider() throws NoSuchFieldException,
            IllegalAccessException {
        //given
        Field sendSmsMethodsField = SmsSender.class.getDeclaredField("sendSmsMethods");
        sendSmsMethodsField.setAccessible(true);

        //when
        Map<SmsApiProvider, BiFunction<SmsReplyRequest, Message, ResponseEntity>> sendSmsMethods =
                (Map<SmsApiProvider, BiFunction<SmsReplyRequest, Message, ResponseEntity>>) sendSmsMethodsField
                        .get(smsSender);

        //then
        assertThat(sendSmsMethods.keySet())
                .containsExactlyInAnyOrder(SmsApiProvider.values());

    }


}