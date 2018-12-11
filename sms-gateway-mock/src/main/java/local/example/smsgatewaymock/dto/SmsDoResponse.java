package local.example.smsgatewaymock.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class SmsDoResponse {

    private int count;
    private int parts;
    List<SmsDelivery> list;
}
