package io.github.sagapoctryone.service.movement;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.hazelcast.function.FunctionEx;
import com.hazelcast.jet.Util;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class DebeziumMovementMapper implements FunctionEx<ObjectNode, Map.Entry<String, String>> {


    @Override
    public Map.Entry<String, String> applyEx(ObjectNode node) throws Exception {
        var transactionId = node.path("payload").path("transaction").path("id").asText();
        var payloadNode = (ObjectNode) node.path("payload");
        payloadNode.remove(List.of("transaction", "source", "updateDescription"));
        if (payloadNode.path("op").asText().equals("d")) {
            payloadNode.set("data", payloadNode.get("before"));
        } else {
            payloadNode.set("data", payloadNode.get("after"));
        }
        payloadNode.remove("after");
        payloadNode.remove("before");

        return Util.entry(transactionId, payloadNode.toString());
    }
}
