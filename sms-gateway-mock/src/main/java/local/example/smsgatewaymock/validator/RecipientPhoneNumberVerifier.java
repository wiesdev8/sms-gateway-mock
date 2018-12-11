package local.example.smsgatewaymock.validator;

import java.util.Collections;

import org.springframework.stereotype.Component;

import local.example.smsgatewaymock.configuration.SmsApiMockProperties;
import local.example.smsgatewaymock.dto.InvalidNumber;
import local.example.smsgatewaymock.dto.SmsDoError;
import local.example.smsgatewaymock.exception.SmsDoException;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class RecipientPhoneNumberVerifier {

    private final SmsApiMockProperties smsApiMockProperties;

    public void verify(String recipientPhoneNumber) {
        if (recipientPhoneNumber == null
                || !recipientPhoneNumber.matches("^\\+?\\d{9,11}$")
                || !recipientPhoneNumber.startsWith(smsApiMockProperties.getRecipientsCorrectNumbersStartWith())) {
            throw new SmsDoException() {
                @Override
                public SmsDoError getSmsDoError() {
                    return new SmsDoError(13, "No correct phone numbers",
                            Collections.singletonList(
                                    new InvalidNumber(recipientPhoneNumber,
                                            smsApiMockProperties.getSubmittedNumber(),
                                            "Invalid phone number")));
                }
            };
        }
    }
}
