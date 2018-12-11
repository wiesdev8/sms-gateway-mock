package local.example.smsgatewaymock.dto;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_EMPTY;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
@JsonInclude(NON_EMPTY)
public class SmsDoError {

    private int error;
    private String message;

    @JsonProperty("invalid_numbers")
    List<InvalidNumber> invalidNumbers;
}
