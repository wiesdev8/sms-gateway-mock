package local.example.smsgatewaymock.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Getter;
import lombok.Setter;

@ConfigurationProperties(prefix = "example.sms-api-mock.blue-media-receiver-client")
@Setter
@Getter
public class BlueMediaReceiverProperties {
    private String receiverPhoneNumber;
    private String operator;
}
