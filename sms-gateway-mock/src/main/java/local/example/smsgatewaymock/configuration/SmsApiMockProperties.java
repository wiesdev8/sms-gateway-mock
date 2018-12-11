package local.example.smsgatewaymock.configuration;

import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Getter;
import lombok.Setter;

@ConfigurationProperties(prefix = "example.sms-api-mock")
@Setter
@Getter
public class SmsApiMockProperties {

    private String messagesServiceUrl;
    private String submittedNumber;
    private Map<String, String> authorizedUsersSmsapiPl;
    private Map<String, String> authorizedUsersBlueMedia;
    private String recipientsCorrectNumbersStartWith;

}
