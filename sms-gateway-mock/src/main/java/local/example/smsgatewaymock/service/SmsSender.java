package local.example.smsgatewaymock.service;

import static local.example.smsgatewaymock.dto.SmsApiProvider.BLUE_MEDIA_PREMIUM_SMS;
import static local.example.smsgatewaymock.dto.SmsApiProvider.SMSAPI_PL;
import static local.example.smsgatewaymock.rest.SmsRepliesController.RESPONSE_BODY_OK;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.ResponseEntity.unprocessableEntity;

import java.util.Map;
import java.util.function.BiFunction;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import local.example.smsgatewaymock.data.SentMessagesCache;
import local.example.smsgatewaymock.dto.Message;
import local.example.smsgatewaymock.dto.SmsApiProvider;
import local.example.smsgatewaymock.dto.SmsReplyRequest;
import local.example.smsgatewaymock.ws.bluemedia.SmsReceiverClient;
import local.example.smsgatewaymock.ws.bluemedia.ReceiveSmsRequest;
import local.example.smsgatewaymock.ws.bluemedia.ReceiveSmsResponse;
import com.google.common.collect.ImmutableMap;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class SmsSender {

    private final SentMessagesCache sentMessagesCache;
    private final SmsReceiverClient smsReceiverClient;
    private final SmsApiPlCallbackClient smsApiPlCallbackClient;
    private final BlueMediaReceiveSmsRequestCreator receiveSmsRequestCreator;

    private final Map<SmsApiProvider, BiFunction<SmsReplyRequest, Message, ResponseEntity>> sendSmsMethods =
            ImmutableMap.of(SMSAPI_PL, this::sendSmsReplyUsingSmsApiPlProvider,
                    BLUE_MEDIA_PREMIUM_SMS, this::sendSmsReplyUsingBlueMediaReceiverService);

    public ResponseEntity sendSmsReply(SmsReplyRequest request) {
        String fromPhoneNumber = request.getFrom();
        Message messageFromCache = sentMessagesCache.getMessage(fromPhoneNumber);
        final SmsApiProvider smsApiProvider = messageFromCache.getSmsApiProvider();
        return sendSmsMethods
                .getOrDefault(smsApiProvider,
                        respondWithErrorAboutUnknownSendSmsReplyMethodFor(smsApiProvider))
                .apply(request, messageFromCache);
    }

    private ResponseEntity sendSmsReplyUsingBlueMediaReceiverService(SmsReplyRequest request,
                                                                         Message messageFromCache) {
        log.debug("Sending SMS reply using Blue Media Receiver for remote id " + messageFromCache.getId());
        ReceiveSmsRequest receiveSmsRequest = receiveSmsRequestCreator
                .createReceiveSmsRequest(request.getFrom(), request.getText());
        ReceiveSmsResponse receiveSmsResponse = smsReceiverClient.send(receiveSmsRequest);
        Integer result = receiveSmsResponse.getResult();
        if (Integer.valueOf(1).equals(result)) {
            return ResponseEntity.status(CREATED).body("Blue Media Receiver has processed SMS successfully");
        } else {
            return ResponseEntity.badRequest().body("Blue Media Receiver can not process SMS");
        }
    }

    private ResponseEntity<String> sendSmsReplyUsingSmsApiPlProvider(SmsReplyRequest request,
                                               Message messageFromCache) {
        String fromPhoneNumber = request.getFrom();
        String messageId = messageFromCache.getId();
        String username = messageFromCache.getUsername();
        String text = request.getText();
        ResponseEntity<String> responseFromMessagesService = smsApiPlCallbackClient
                .sendSmsReply(fromPhoneNumber, text, messageId, username);

        HttpStatus statusOfResponseFromMessagesService = responseFromMessagesService.getStatusCode();

        if (!OK.equals(statusOfResponseFromMessagesService)) {
            return responseFromMessagesService;
        } else {
            String bodyOfResponseFromMessagesService = responseFromMessagesService.getBody();
            if (RESPONSE_BODY_OK.equals(bodyOfResponseFromMessagesService)) {
                return ResponseEntity.status(CREATED).body(RESPONSE_BODY_OK);
            } else {
                return ResponseEntity.badRequest().body(bodyOfResponseFromMessagesService);
            }
        }

    }

    private BiFunction<SmsReplyRequest, Message, ResponseEntity>
    respondWithErrorAboutUnknownSendSmsReplyMethodFor(SmsApiProvider smsApiProvider) {
        return (smsReplyRequest, message) -> {
            String s = "No send sms reply method for SMS API Provider: " + smsApiProvider;
            log.warn(s);
            return unprocessableEntity().body(s);
        };
    }


}
