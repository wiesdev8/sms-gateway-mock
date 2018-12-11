package local.example.smsgatewaymock.validator;

import java.util.Collections;
import java.util.Map;

import org.springframework.stereotype.Component;

import local.example.smsgatewaymock.configuration.SmsApiMockProperties;
import local.example.smsgatewaymock.dto.SmsDoError;
import local.example.smsgatewaymock.exception.SmsDoException;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class AuthorizationVerifier {

    private final SmsApiMockProperties smsApiMockProperties;

    public void verifyAuthorizationToSmsapiPl(String ofUsername, String withPassword) {
        Map<String, String> authorizedUsers = smsApiMockProperties.getAuthorizedUsersSmsapiPl();
        if (!authorizedUsers.containsKey(ofUsername) || !authorizedUsers.get(ofUsername).equals(withPassword)) {
            throw new SmsDoException() {
                @Override
                public SmsDoError getSmsDoError() {
                    return new SmsDoError(101, "Authorization failed", Collections.emptyList());
                }
            };
        }

    }

    public void verifyAuthorizationToBlueMediaSmsSender(String ofUsername, String withPassword) {
        Map<String, String> authorizedUsers = smsApiMockProperties.getAuthorizedUsersBlueMedia();
        if (!authorizedUsers.containsKey(ofUsername) || !authorizedUsers.get(ofUsername).equals(withPassword)) {
            throw new BlueMediaSmsSenderAuthorizationException(ofUsername);
        }

    }

    private class BlueMediaSmsSenderAuthorizationException extends RuntimeException {

        private BlueMediaSmsSenderAuthorizationException(String userName) {
            super("Blue Media Sms Sender Mock - authorization failed for user: " + userName);
        }
    }

}
