package io.github.sagapoctryone.configuration;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.hazelcast.multimap.MultiMap;
import io.github.sagapoctryone.vendor.DebeziumTransactionUtil;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.MessagingException;
import org.springframework.stereotype.Component;

import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class DebeziumEventHandler implements MessageHandler {

    MultiMap<String, String> debeziumEventMap;


    @Override
    public void handleMessage(Message<?> message) throws MessagingException {
        var node = (ObjectNode) message.getPayload();
        System.out.println(node.toPrettyString());
        var transactionId = node.path("payload").path("transaction").path("id").asText();

        node.remove(List.of("schema", "transaction"));
        var payloadNode = (ObjectNode) node.path("payload");
        payloadNode.remove(List.of("before", "source"));

        debeziumEventMap.put(transactionId,
                node.path("payload").path("after").asText());
    }
}
