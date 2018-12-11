package local.example.smsgatewaymock.exception;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

import org.springframework.web.bind.annotation.ResponseStatus;

import local.example.smsgatewaymock.dto.SmsDoError;

@ResponseStatus(BAD_REQUEST)
public abstract class SmsDoException extends RuntimeException {

    public abstract SmsDoError getSmsDoError();
}
