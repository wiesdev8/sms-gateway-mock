package local.example.smsgatewaymock.ws.bluemedia;

import static local.example.smsgatewaymock.ws.SoapMessageAsString.asString;
import static java.util.Optional.ofNullable;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.stereotype.Component;
import org.springframework.ws.client.core.support.WebServiceGatewaySupport;
import org.springframework.ws.soap.SoapMessage;
import org.springframework.ws.soap.client.SoapFaultClientException;

import local.example.smsgatewaymock.configuration.SmsApiMockProperties;
import local.example.ws.bluemedia.sms.receiver.Sms;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class SmsReceiverClient extends WebServiceGatewaySupport {

    private static final String PATH_TO_RECEIVE_SMS_WEB_SERVICE = "/ws";

    private final SmsApiMockProperties smsApiMockProperties;
    private final Jaxb2Marshaller blueMediaSmsReceiverMarshaller;

    public SmsReceiverClient(SmsApiMockProperties smsApiMockProperties,
                             @Qualifier("blueMediaSmsReceiverMarshaller")
                                    Jaxb2Marshaller blueMediaSmsReceiverMarshaller) {
        this.smsApiMockProperties = smsApiMockProperties;
        this.blueMediaSmsReceiverMarshaller = blueMediaSmsReceiverMarshaller;
    }

    @PostConstruct
    private void init() {
        setDefaultUri(smsApiMockProperties.getMessagesServiceUrl() +
                PATH_TO_RECEIVE_SMS_WEB_SERVICE);
        setMarshaller(blueMediaSmsReceiverMarshaller);
        setUnmarshaller(blueMediaSmsReceiverMarshaller);
        getWebServiceTemplate().setFaultMessageResolver(message -> {
            SoapMessage soapMessage = (SoapMessage) message;
            log.warn("Soap fault: {}", asString(soapMessage));
            throw new SoapFaultClientException(soapMessage);
        });
    }

    public ReceiveSmsResponse send(ReceiveSmsRequest smsRequest) {
        Sms sms = ofNullable(smsRequest).map(ReceiveSmsRequest::getSms).orElse(new Sms());
        log.debug("Blue Media sms receiver client - sending sms request with " +
                        "id='{}', from='{}', to='{}', " +
                        "operator='{}', text='{}'",
                sms.getId(), sms.getFrom(), sms.getTo(), sms.getOperator(), sms.getText());

        ReceiveSmsResponse smsResponse = (ReceiveSmsResponse) getWebServiceTemplate().marshalSendAndReceive(smsRequest);

        String statusInResponse = ofNullable(smsResponse)
                .map(ReceiveSmsResponse::getResult).map(Object::toString).orElse(null);
        log.debug("Blue Media sms receiver client - received response with status='{}' for sms id='{}'",
                statusInResponse, sms.getId());
        return smsResponse;
    }


}
