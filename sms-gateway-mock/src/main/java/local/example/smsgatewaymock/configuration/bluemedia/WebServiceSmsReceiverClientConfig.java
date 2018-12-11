package local.example.smsgatewaymock.configuration.bluemedia;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;

import local.example.smsgatewaymock.ws.bluemedia.ReceiveSmsRequest;

@Configuration
class WebServiceSmsReceiverClientConfig {

    @Bean(name = "blueMediaSmsReceiverMarshaller")
    Jaxb2Marshaller blueMediaSmsReceiverMarshaller() {
        Jaxb2Marshaller jaxb2Marshaller = new Jaxb2Marshaller();
        jaxb2Marshaller.setPackagesToScan(ReceiveSmsRequest.class.getPackage().getName());
        return jaxb2Marshaller;
    }


}