package local.example.smsgatewaymock.ws;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.springframework.ws.soap.SoapMessage;

import lombok.experimental.UtilityClass;


@UtilityClass
public final class SoapMessageAsString {

    public static String asString(SoapMessage soapMessage) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        soapMessage.writeTo(out);
        return new String(out.toByteArray());
    }
}
