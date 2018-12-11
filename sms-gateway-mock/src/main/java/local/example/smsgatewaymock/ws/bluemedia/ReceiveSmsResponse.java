package local.example.smsgatewaymock.ws.bluemedia;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
        name = "ReceiveSmsResponse",
        propOrder = {"result"}
)
@XmlRootElement(
        name = "ReceiveSmsResponse",
        namespace = "http://com.bluemedia.fbs.webservice.receiver/webservice"
)
@NoArgsConstructor
@Setter
@Getter
@EqualsAndHashCode
public class ReceiveSmsResponse {

    @XmlElement(
            required = true
    )
    protected Integer result;

}
