package io.github.sagapoctryone.service.movement;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.hazelcast.function.FunctionEx;
import com.hazelcast.jet.Util;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class AuxiliaryMovementMapper implements FunctionEx<ObjectNode, Map.Entry<String, String>> {

    private final ObjectMapper objectMapper = new ObjectMapper();


    @Override
    public Map.Entry<String, String> applyEx(ObjectNode node) throws Exception {
        var transactionId = node.path("payload").path("transaction").path("id").asText();
        var payloadNode = (ObjectNode) node.path("payload");
        try {
            var choreographyId = objectMapper.readTree(payloadNode.path("after").asText())
                    .path("choreographyId");
            return Util.entry(choreographyId.asText(), transactionId);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
