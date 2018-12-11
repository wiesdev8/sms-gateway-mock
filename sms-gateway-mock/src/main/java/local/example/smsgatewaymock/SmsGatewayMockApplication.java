package local.example.smsgatewaymock;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import local.example.smsgatewaymock.configuration.BlueMediaReceiverProperties;
import local.example.smsgatewaymock.configuration.MessagesCacheProperties;
import local.example.smsgatewaymock.configuration.SmsApiMockProperties;

@SpringBootApplication
@EnableConfigurationProperties({
        BlueMediaReceiverProperties.class,
        SmsApiMockProperties.class,
        MessagesCacheProperties.class
})
public class SmsGatewayMockApplication {
    public static void main(String[] args) {
        SpringApplication.run(SmsGatewayMockApplication.class, args);
    }
}
