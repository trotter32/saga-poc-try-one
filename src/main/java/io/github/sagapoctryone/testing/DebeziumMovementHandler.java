package io.github.sagapoctryone.testing;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.hazelcast.map.IMap;
import com.hazelcast.multimap.MultiMap;
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
public class DebeziumMovementHandler implements MessageHandler {

    MultiMap<String, String> debeziumMovementMap;
    IMap<String, String> auxiliaryMovementMap;
    ObjectMapper objectMapper;

    @Override
    public void handleMessage(Message<?> message) throws MessagingException {
        var node = (ObjectNode) message.getPayload();
        System.out.println(node.toPrettyString());
        var transactionId = node.path("payload").path("transaction").path("id").asText();

        node.remove(List.of("transaction", "source"));

        var schemaName = node.path("schema").path("name").asText();
        String[] tokens = schemaName.split("\\.");
        var payloadNode = (ObjectNode) node.path("payload");

        if (!tokens[2].equals("auxiliaryMovementSteps")) {
            if (tokens[2].equals("auxiliary")) {
                try {
                    var choreographyId = objectMapper.readTree(payloadNode.path("after").asText())
                            .path("choreographyId");
                    auxiliaryMovementMap.put(choreographyId.asText(), transactionId);
                } catch (JsonProcessingException e) {
                    throw new RuntimeException(e);
                }
            } else {
                if (node.path("op").asText().equals("d")) {
                    payloadNode.remove("after");
                } else {
                    payloadNode.remove("before");
                }
                debeziumMovementMap.put(transactionId, payloadNode.toString());
            }

        }
    }
}
