package local.example.smsgatewaymock.ws.bluemedia;

import static local.example.smsgatewaymock.data.MessageCreators.createMessageForBlueMediaPremiumSms;
import static java.time.LocalDateTime.now;
import static java.time.format.DateTimeFormatter.ofPattern;
import static javax.xml.parsers.DocumentBuilderFactory.newInstance;

import javax.xml.parsers.ParserConfigurationException;

import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.Namespace;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;
import org.springframework.ws.server.endpoint.annotation.XPathParam;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import local.example.smsgatewaymock.data.SentMessagesCache;
import local.example.smsgatewaymock.validator.AuthorizationVerifier;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Endpoint
@Slf4j
@RequiredArgsConstructor
public class SmsSenderEndpoint {

    private final static String NAMESPACE_URI = "http://com.bluemedia.fbs.webservice.premiumsms.sender/webservice";
    private static final String TIMESTAMP_FORMAT = "YYMMddHHmmss";

    private final AuthorizationVerifier authorizationVerifier;
    private final SentMessagesCache sentMessagesCache;

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "SendSmsRequest")
    @Namespace(prefix = "rns", uri = NAMESPACE_URI)
    @ResponsePayload
    public Element sendSms(@XPathParam("/rns:SendSmsRequest/String_1") String login,
                           @XPathParam("/rns:SendSmsRequest/String_2") String password,
                           @XPathParam("/rns:SendSmsRequest/SendRequest_3/phone") String phone,
                           @XPathParam("/rns:SendSmsRequest/SendRequest_3/operator") String operator,
                           @XPathParam("/rns:SendSmsRequest/SendRequest_3/gateway") String gateway,
                           @XPathParam("/rns:SendSmsRequest/SendRequest_3/remoteId") Long remoteId,
                           @XPathParam("/rns:SendSmsRequest/SendRequest_3/text") String text)
            throws ParserConfigurationException {

        log.debug("Blue Media sms sender - received request with " +
                        "login='{}', phone='{}', operator='{}', " +
                        "gateway='{}', remoteId='{}' text='{}'",
                login, phone, operator, gateway, remoteId, text);

        authorizationVerifier.verifyAuthorizationToBlueMediaSmsSender(login, password);

        sentMessagesCache.addMessage(phone, createMessageForBlueMediaPremiumSms(remoteId.toString(), text, login));

        Document document = newInstance().newDocumentBuilder().newDocument();

        Element responseElement = createRootElementInResponse(document);

        Element result = createResultElementInResponse(document, responseElement);

        appendChild("appCode", "1", result, document);
        appendChild("phone", phone, result, document);
        appendChild("remoteId", remoteId.toString(), result, document);
        appendChild("status", "1", result, document);
        appendChild("timestamp", provideTimestamp(), result, document);
        return responseElement;

    }

    private Element createResultElementInResponse(Document document, Element responseElement) {
        Element result = document.createElementNS("", "result");
        responseElement.appendChild(result);
        return result;
    }

    private Element createRootElementInResponse(Document document) {
        Element sendSmsRequestResponse = document.createElementNS(NAMESPACE_URI, "SendSmsRequestResponse");
        document.appendChild(sendSmsRequestResponse);
        return sendSmsRequestResponse;
    }

    private void appendChild(String withTagName, String andTextContent, Element intoElement, Document usingDocument) {
        Element element = usingDocument.createElementNS("", withTagName);
        element.setTextContent(andTextContent);
        intoElement.appendChild(element);
    }

    private String provideTimestamp() {
        return now().format(ofPattern(TIMESTAMP_FORMAT));
    }


}
