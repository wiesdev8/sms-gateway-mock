package local.example.smsgatewaymock.configuration;

import java.util.concurrent.TimeUnit;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Getter;
import lombok.Setter;

@ConfigurationProperties(prefix = "example.sms-api-mock.messages-cache")
@Setter
@Getter
public class MessagesCacheProperties {
    private long maximumSize;
    private long expireDurationAfterWrite;
    private TimeUnit timeUnit;
}
