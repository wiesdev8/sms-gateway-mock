package local.example.smsgatewaymock.validator;

import java.util.Collections;

import org.springframework.stereotype.Component;

import local.example.smsgatewaymock.dto.SmsDoError;
import local.example.smsgatewaymock.exception.SmsDoException;

@Component
public class SmsDoRequestParametersVerifier {

    private static final String messageTemplate = "example.smsapi-mock -- parameter [%s] has expected value [%s] but actual has incorrect value [%s]";

    public void verifyThat(String parameter, String withValue, String isEqualTo) {
        if (!isEqualTo.equals(withValue)) {
            throw new SmsDoException() {
                @Override
                public SmsDoError getSmsDoError() {
                    return new SmsDoError(901,
                            String.format(messageTemplate, parameter, isEqualTo, withValue),
                            Collections.emptyList());
                }
            };
        }
    }

}
