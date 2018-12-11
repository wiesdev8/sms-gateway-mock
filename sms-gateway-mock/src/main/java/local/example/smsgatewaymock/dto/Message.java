package local.example.smsgatewaymock.dto;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
@JsonInclude(NON_NULL)
public class Message {
    private final SmsApiProvider smsApiProvider;
    private final String id;
    private final String text;
    private final String username;
}
