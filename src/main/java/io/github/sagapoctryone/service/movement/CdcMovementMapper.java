package io.github.sagapoctryone.service.movement;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.hazelcast.function.FunctionEx;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.io.Serializable;

@Component
public class CdcMovementMapper implements FunctionEx<byte[], ObjectNode> {

    private final ObjectMapper objectMapper = new ObjectMapper();


    @Override
    public ObjectNode applyEx(byte[] bytes) throws Exception {
       var result = (ObjectNode) objectMapper.readTree(bytes);
        System.out.println("~~~ " + result.toPrettyString());
        return result;
    }
}
