package local.example.smsgatewaymock.data;

import static local.example.smsgatewaymock.dto.SmsApiProvider.BLUE_MEDIA_PREMIUM_SMS;
import static local.example.smsgatewaymock.dto.SmsApiProvider.SMSAPI_PL;

import local.example.smsgatewaymock.dto.Message;

import lombok.experimental.UtilityClass;

@UtilityClass
public class MessageCreators {

    public static Message createMessageForSmsApiPl(String havingId, String messageText, String username) {
        return new Message(SMSAPI_PL, havingId, messageText, username);
    }

    public static Message createMessageForBlueMediaPremiumSms(String havingId, String messageText, String username) {
        return new Message(BLUE_MEDIA_PREMIUM_SMS, havingId, messageText, username);
    }
}
