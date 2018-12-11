package local.example.smsgatewaymock.rest;

import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import local.example.smsgatewaymock.dto.SmsReplyRequest;
import local.example.smsgatewaymock.service.SmsSender;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping(SmsRepliesController.PATH_TO_SMS_REPLIES)
@RequiredArgsConstructor
@Slf4j
public class SmsRepliesController {

    public static final String RESPONSE_BODY_OK = "OK";
    static final String PATH_TO_SMS_REPLIES = "/sms-replies";

    private final SmsSender smsSender;

    @PostMapping(consumes = APPLICATION_JSON_UTF8_VALUE)
    ResponseEntity createSmsReply(@RequestBody SmsReplyRequest request) {
        return smsSender.sendSmsReply(request);
    }

}
