package io.github.sagapoctryone.service.cdc;

import com.fasterxml.jackson.core.JsonProcessingException;
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
public class AuxiliaryEventMapper implements FunctionEx<ObjectNode, Map.Entry<String, String>>, ApplicationContextAware {

    private transient ObjectMapper objectMapper;


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


    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        objectMapper = applicationContext.getBean(ObjectMapper.class);
    }
}
