package local.example.smsgatewaymock.data;

import static local.example.smsgatewaymock.dto.SmsDeliveryStatus.QUEUE;

import java.util.Collections;

import org.springframework.stereotype.Component;

import local.example.smsgatewaymock.configuration.SmsApiMockProperties;
import local.example.smsgatewaymock.dto.SmsDoResponse;
import local.example.smsgatewaymock.dto.SmsDelivery;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class SmsDoResponseGenerator {

    public static final int COUNT = 1;
    public static final int PARTS = 1;
    public static final double POINTS = 0.16;

    private final MessageIdGenerator messageIdGenerator;
    private final DateSentProvider dateSentProvider;
    private final SmsApiMockProperties properties;

    public SmsDoResponse generateUsing(String recipientPhoneNumber) {

        return new SmsDoResponse(
                COUNT,
                PARTS,
                Collections.singletonList(
                        new SmsDelivery(messageIdGenerator.generate(),
                                POINTS,
                                recipientPhoneNumber,
                                dateSentProvider.provide(),
                                properties.getSubmittedNumber(),
                                QUEUE)));
    }

}
