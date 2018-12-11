package local.example.smsgatewaymock.service;

import static java.lang.Long.valueOf;

import org.springframework.stereotype.Component;

import local.example.smsgatewaymock.configuration.BlueMediaReceiverProperties;
import local.example.smsgatewaymock.data.MessageIdGenerator;
import local.example.smsgatewaymock.ws.bluemedia.ReceiveSmsRequest;
import local.example.ws.bluemedia.sms.receiver.Sms;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
class BlueMediaReceiveSmsRequestCreator {

    private final MessageIdGenerator messageIdGenerator;
    private final BlueMediaReceiverProperties receiverProperties;

    ReceiveSmsRequest createReceiveSmsRequest(String from, String text) {
        Sms sms = new Sms();
        sms.setId(valueOf(messageIdGenerator.generate()));
        sms.setNumberOfParts(1);
        sms.setText(text);
        sms.setTo(receiverProperties.getReceiverPhoneNumber());
        sms.setOperator(receiverProperties.getOperator());
        sms.setFrom(from);
        ReceiveSmsRequest receiveSmsRequest = new ReceiveSmsRequest();
        receiveSmsRequest.setSms(sms);
        return receiveSmsRequest;
    }

}
