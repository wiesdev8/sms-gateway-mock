package local.example.smsgatewaymock.rest;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import local.example.smsgatewaymock.data.SentMessagesCache;
import local.example.smsgatewaymock.dto.Message;

import lombok.AllArgsConstructor;

@RestController
@AllArgsConstructor
public class SentMessagesController {

    static final String PATH_TO_SENT_MESSAGE = "/sent-messages/{sentTo}";

    private SentMessagesCache sentMessagesCache;

    @GetMapping(path = PATH_TO_SENT_MESSAGE)
    public ResponseEntity<Message> readMessage(
            @PathVariable("sentTo") String sentTo) {

        return ResponseEntity.ok(sentMessagesCache.getMessage(sentTo));
    }

}
