package io.github.sagapoctryone.service.cdc;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.hazelcast.function.FunctionEx;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;

import java.io.Serializable;

@Component
public class CdcEventMapper implements FunctionEx<byte[], ObjectNode>, Serializable, ApplicationContextAware {

    private static  ObjectMapper objectMapper;


    @Override
    public ObjectNode applyEx(byte[] bytes) throws Exception {
        return (ObjectNode) objectMapper.readTree(bytes);
    }


    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        objectMapper = applicationContext.getBean(ObjectMapper.class);
        SpringBeanAutowiringSupport.processInjectionBasedOnCurrentContext(this);
    }
}
