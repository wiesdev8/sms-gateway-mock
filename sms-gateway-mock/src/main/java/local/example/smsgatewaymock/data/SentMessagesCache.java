package local.example.smsgatewaymock.data;

import java.util.Optional;

import org.springframework.stereotype.Component;

import local.example.smsgatewaymock.configuration.MessagesCacheProperties;
import local.example.smsgatewaymock.dto.Message;
import local.example.smsgatewaymock.exception.MessageNotFoundException;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

@Component
public class SentMessagesCache {

    private final Cache<String, Message> sentMessages;

    SentMessagesCache(MessagesCacheProperties messagesCacheProperties) {
        sentMessages = CacheBuilder.newBuilder()
                .maximumSize(messagesCacheProperties.getMaximumSize())
                .expireAfterWrite(messagesCacheProperties.getExpireDurationAfterWrite(),
                        messagesCacheProperties.getTimeUnit())
                .build();
    }

    public void addMessage(String sentTo, Message message) {
        sentMessages.put(sentTo, message);
    }

    public Message getMessage(String sentTo) {
        return Optional.ofNullable(sentMessages.getIfPresent(sentTo))
                .orElseThrow(() -> new MessageNotFoundException(sentTo));
    }

    public boolean hasMessage(String sentTo) {
        return sentMessages.getIfPresent(sentTo) != null;
    }

}
