package local.example.smsgatewaymock.data;

import static org.apache.commons.lang3.RandomStringUtils.randomNumeric;

import java.time.Instant;

import org.springframework.stereotype.Component;

@Component
public class MessageIdGenerator {

    public String generate() {
        return Instant.now().getEpochSecond() + randomNumeric(6);
    }
}
