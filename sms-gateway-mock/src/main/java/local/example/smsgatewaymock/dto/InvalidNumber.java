package local.example.smsgatewaymock.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class InvalidNumber {

    private String number;

    @JsonProperty("submitted_number")
    private String submittedNumber;

    private String message;
}
