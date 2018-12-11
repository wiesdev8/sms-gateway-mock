package local.example.smsgatewaymock.data;

import java.time.Instant;

import org.springframework.stereotype.Component;

@Component
public class DateSentProvider {

    public long provide() {
        return Instant.now().getEpochSecond();
    }
}
