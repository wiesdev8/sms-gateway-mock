package local.example.smsgatewaymock.ws.bluemedia;

import static local.example.smsgatewaymock.dto.SmsApiProvider.BLUE_MEDIA_PREMIUM_SMS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.ws.test.server.RequestCreators.withPayload;
import static org.springframework.ws.test.server.ResponseMatchers.noFault;
import static org.springframework.ws.test.server.ResponseMatchers.serverOrReceiverFault;
import static org.springframework.ws.test.server.ResponseMatchers.xpath;

import javax.xml.transform.Source;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.ws.test.server.MockWebServiceClient;
import org.springframework.ws.test.server.ResponseXPathExpectations;
import org.springframework.xml.transform.StringSource;

import local.example.smsgatewaymock.configuration.SmsApiMockProperties;
import local.example.smsgatewaymock.data.SentMessagesCache;

@RunWith(SpringRunner.class)
@SpringBootTest
public class SmsSenderEndpointTest {

    private static final String XPATH_EXPRESSION_FORMAT = "/*[name()='SendSmsRequestResponse']/result/%s";

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private SentMessagesCache sentMessagesCache;

    @Autowired
    private SmsApiMockProperties smsApiMockProperties;

    private MockWebServiceClient mockWebServiceClient;

    @Before
    public void init() {
        mockWebServiceClient = MockWebServiceClient
                .createClient(applicationContext);
        smsApiMockProperties.getAuthorizedUsersBlueMedia().put("test-login", "test-password");
    }


    @Test
    public void should_be_able_to_send_sms_request_to_blue_media_sender_mock() {
        //given
        Source requestPayload = new StringSource(
                "<SendSmsRequest " +
                        "xmlns='http://com.bluemedia.fbs.webservice.premiumsms.sender/webservice'>" +
                        "<String_1 xmlns=''>test-login</String_1>" +
                        "<String_2 xmlns=''>test-password</String_2>" +
                        "<SendRequest_3 xmlns=''>" +
                        "<phone>+48603880632</phone>" +
                        "<operator></operator>" +
                        "<gateway></gateway>" +
                        "<remoteId>19695540</remoteId>" +
                        "<text>Test blue media sms mock</text>" +
                        "</SendRequest_3>" +
                        "</SendSmsRequest>");
        assertThat(sentMessagesCache.hasMessage("+48603880632")).isFalse();
        //when & then
        mockWebServiceClient
                .sendRequest(withPayload(requestPayload))
                .andExpect(noFault())
                .andExpect(xpathTo("appCode").evaluatesTo(1))
                .andExpect(xpathTo("phone").evaluatesTo("+48603880632"))
                .andExpect(xpathTo("remoteId").evaluatesTo("19695540"))
                .andExpect(xpathTo("status").evaluatesTo("1"))
                .andExpect(xpathTo("timestamp").exists());

        assertThat(sentMessagesCache.hasMessage("+48603880632"))
                .isTrue();
        assertThat(sentMessagesCache.getMessage("+48603880632").getId())
                .isEqualTo("19695540");
        assertThat(sentMessagesCache.getMessage("+48603880632").getSmsApiProvider())
                .isEqualTo(BLUE_MEDIA_PREMIUM_SMS);
    }

    @Test
    public void should_receive_failure_when_sending_incorrect_sms_request_to_blue_media_sender_mock() {
        //given
        Source requestPayload = new StringSource(
                "<SendSmsRequest " +
                        "xmlns='http://com.bluemedia.fbs.webservice.premiumsms.sender/webservice'>" +
                        "<String_1 xmlns=''>test-login</String_1>" +
                        "<String_2 xmlns=''>test-password</String_2>" +
                        "<SendRequest_3 xmlns=''>" +
                        "<phone>+48603880632</phone>" +
                        "<operator></operator>" +
                        "<gateway></gateway>" +

                        "<text>Test blue media sms mock</text>" +
                        "</SendRequest_3>" +
                        "</SendSmsRequest>");
        //when & then
        mockWebServiceClient
                .sendRequest(withPayload(requestPayload))
                .andExpect(serverOrReceiverFault());

    }

    @Test
    public void should_receive_failure_when_password_is_incorrect() {
        //given
        Source requestPayload = new StringSource(
                "<SendSmsRequest " +
                        "xmlns='http://com.bluemedia.fbs.webservice.premiumsms.sender/webservice'>" +
                        "<String_1 xmlns=''>test-login</String_1>" +
                        "<String_2 xmlns=''>test-password-incorrect</String_2>" +
                        "<SendRequest_3 xmlns=''>" +
                        "<phone>+48603880632</phone>" +
                        "<operator></operator>" +
                        "<gateway></gateway>" +
                        "<remoteId>19695540</remoteId>" +
                        "<text>Test blue media sms mock</text>" +
                        "</SendRequest_3>" +
                        "</SendSmsRequest>");
        assertThat(sentMessagesCache.hasMessage("+48603880632")).isFalse();
        //when & then
        mockWebServiceClient
                .sendRequest(withPayload(requestPayload))
                .andExpect(serverOrReceiverFault());

        assertThat(sentMessagesCache.hasMessage("+48603880632"))
                .isFalse();
    }


    private ResponseXPathExpectations xpathTo(String nameOfFieldInResult) {
        return xpath(String.format(XPATH_EXPRESSION_FORMAT, nameOfFieldInResult));
    }

}