package local.example.smsgatewaymock.exception;

import static org.springframework.http.HttpStatus.NOT_FOUND;

import java.util.NoSuchElementException;

import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = NOT_FOUND)
public class MessageNotFoundException extends NoSuchElementException {

    public MessageNotFoundException(String sentTo) {
        super("Not found any message sent to: " + sentTo);
    }
}
