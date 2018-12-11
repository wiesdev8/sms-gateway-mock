package local.example.smsgatewaymock.ws.bluemedia;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.ws.test.client.RequestMatchers.payload;
import static org.springframework.ws.test.client.ResponseCreators.withPayload;

import javax.xml.transform.Source;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.ws.test.client.MockWebServiceServer;
import org.springframework.xml.transform.StringSource;

import local.example.ws.bluemedia.sms.receiver.Sms;

@RunWith(SpringRunner.class)
@SpringBootTest
public class SmsReceiverClientTest {

    @Autowired
    private SmsReceiverClient smsReceiverClient;

    private MockWebServiceServer mockServer;

    @Before
    public void createSmsReceiverServer() {
        mockServer = MockWebServiceServer.createServer(smsReceiverClient);
    }

    @Test
    public void should_be_able_to_send_sms_request_to_receiver_service() {
        //given
        ReceiveSmsRequest request = createRequest();
        Source responsePayload = new StringSource(createResponsePayload());
        Source expectedRequestPayload = new StringSource(createExpectedRequestPayload());

        mockServer.expect(payload(expectedRequestPayload))
                .andRespond(withPayload(responsePayload));

        //when

        ReceiveSmsResponse response = smsReceiverClient.send(request);

        //then
        assertThat(response.getResult())
                .isEqualTo(1);
    }

    private String createExpectedRequestPayload() {
        return "<ns3:ReceiveSms " +
                "xmlns:ns3='http://com.bluemedia.fbs.webservice.receiver/webservice'>" +
                "<Sms_1>" +
                "<id>101</id>" +
                "<from>303</from>" +
                "<operator></operator>" +
                "<to>404</to>" +
                "<text>test blue media sms receiver service</text>" +
                "<numberOfParts>1</numberOfParts>" +
                "</Sms_1>" +
                "</ns3:ReceiveSms>";
    }

    private String createResponsePayload() {
        return "<ns1:ReceiveSmsResponse " +
                "xmlns:ns1='http://com.bluemedia.fbs.webservice.receiver/webservice'>" +
                "<result>1</result>" +
                "</ns1:ReceiveSmsResponse>";
    }

    private ReceiveSmsRequest createRequest() {
        Sms sms = new Sms();
        sms.setId(101L);
        sms.setFrom("303");
        sms.setOperator("");
        sms.setTo("404");
        sms.setText("test blue media sms receiver service");
        sms.setNumberOfParts(1);
        ReceiveSmsRequest request = new ReceiveSmsRequest();
        request.setSms(sms);
        return request;
    }
}
