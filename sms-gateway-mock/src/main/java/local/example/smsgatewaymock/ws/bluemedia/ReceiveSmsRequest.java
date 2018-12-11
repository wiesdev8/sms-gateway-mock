package local.example.smsgatewaymock.ws.bluemedia;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import local.example.ws.bluemedia.sms.receiver.Sms;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
        name = "ReceiveSmsRequest",
        propOrder = {"sms"}
)
@XmlRootElement(
        name = "ReceiveSms",
        namespace = "http://com.bluemedia.fbs.webservice.receiver/webservice"
)
@NoArgsConstructor
@Setter
@Getter
@EqualsAndHashCode
public class ReceiveSmsRequest {

    @XmlElement(
            required = true,
            name = "Sms_1"
    )
    protected Sms sms;

}
