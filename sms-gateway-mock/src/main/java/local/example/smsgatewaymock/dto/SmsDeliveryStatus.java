package local.example.smsgatewaymock.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum SmsDeliveryStatus {

    NOT_FOUND(401),
    EXPIRED(402),
    SENT(403),
    DELIVERED(404),
    UNDELIVERED(405),
    FAILED(406),
    REJECTED(407),
    UNKNOWN(408),
    QUEUE(409);

    private final int code;

}
