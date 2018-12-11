package local.example.smsgatewaymock.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class SmsDelivery {

    private String id;

    private double points;

    private String number;

    @JsonProperty("date_sent")
    private long dataSent;

    @JsonProperty("submitted_number")
    private String submittedNumber;

    private SmsDeliveryStatus status;
}
