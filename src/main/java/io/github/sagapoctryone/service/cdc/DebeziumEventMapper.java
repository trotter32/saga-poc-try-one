package io.github.sagapoctryone.service.cdc;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.hazelcast.function.FunctionEx;
import com.hazelcast.jet.Util;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class DebeziumEventMapper implements FunctionEx<ObjectNode, Map.Entry<String, String>>, ApplicationContextAware {

    private transient ObjectMapper objectMapper;


    @Override
    public Map.Entry<String, String> applyEx(ObjectNode node) throws Exception {
        var transactionId = node.path("payload").path("transaction").path("id").asText();
        node.remove(List.of("transaction", "source"));
        var payloadNode = (ObjectNode) node.path("payload");

        if (node.path("op").asText().equals("d")) {
            payloadNode.set("data", payloadNode.get("before"));
        } else {
            payloadNode.set("data", payloadNode.get("before"));
        }
        payloadNode.remove("after");
        payloadNode.remove("before");

        return Util.entry(transactionId, payloadNode.asText());
    }


    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        objectMapper = applicationContext.getBean(ObjectMapper.class);
    }
}
