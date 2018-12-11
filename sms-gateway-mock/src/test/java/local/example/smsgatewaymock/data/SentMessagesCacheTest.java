package local.example.smsgatewaymock.data;

import static local.example.smsgatewaymock.data.MessageCreators.createMessageForSmsApiPl;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
@TestPropertySource(properties = {
        "example.sms-api-mock.messages-cache.maximum-size=2"
})
public class SentMessagesCacheTest {

    @Autowired
    private SentMessagesCache sentMessagesCache;

    @Test
    public void should_have_messages_in_cache_no_more_than_maximum_size() {
        String username = "username";
        String firstMessage = "1";
        assertThat(sentMessagesCache.hasMessage(firstMessage)).isFalse();

        sentMessagesCache.addMessage(firstMessage,
                createMessageForSmsApiPl(firstMessage, "text 1", username));
        assertThat(sentMessagesCache.hasMessage(firstMessage)).isTrue();

        String secondMessage = "2";
        sentMessagesCache.addMessage(secondMessage,
                createMessageForSmsApiPl(secondMessage, "text 2", username));
        assertThat(sentMessagesCache.hasMessage(secondMessage)).isTrue();

        String thirdMessage = "3";
        sentMessagesCache.addMessage(thirdMessage,
                createMessageForSmsApiPl(thirdMessage, "text 3", username));
        assertThat(sentMessagesCache.hasMessage(firstMessage)).isFalse();
        assertThat(sentMessagesCache.hasMessage(thirdMessage)).isTrue();

    }

    @TestConfiguration()
    @ComponentScan(basePackageClasses = SentMessagesCache.class)
    static class Config {
    }
}
