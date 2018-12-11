package local.example.smsgatewaymock;

import static org.apache.commons.lang3.RandomStringUtils.randomNumeric;

import lombok.experimental.UtilityClass;


@UtilityClass
public class RandomPhoneNumberGenerator {

    public static String generateRandomPhoneNumber() {
        return randomNumeric(11);
    }
}
