package io.github.sagapoctryone.configuration;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.debezium.engine.ChangeEvent;
import io.debezium.engine.DebeziumEngine;
import io.debezium.engine.format.JsonByteArray;
import io.github.sagapoctryone.util.DebeziumUtil;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.debezium.dsl.Debezium;
import org.springframework.integration.dsl.IntegrationFlow;

import java.util.*;

import static java.util.function.Predicate.not;
import static lombok.AccessLevel.PRIVATE;

@Configuration
@RequiredArgsConstructor
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class DebeziumConfiguration {

    private static final TypeReference<Map<String, Object>> typeRef = new TypeReference<Map<String, Object>>() {
    };

    DebeziumEventHandler debeziumEventHandler;
    ObjectMapper objectMapper;


    @Bean
    IntegrationFlow pipeline() {
        Properties properties = new Properties();
        properties.setProperty("name", "source");
        properties.setProperty("connector.class", "io.debezium.connector.mongodb.MongoDbConnector");
        //properties.setProperty("mongodb.name", "source");
        properties.setProperty("mongodb.connection.string", "mongodb://mongo1:27017/?replicaSet=rs0");
        properties.setProperty("mongodb.user", "debezium");
        properties.setProperty("mongodb.password", "debeziumpassword");
        properties.setProperty("mongodb.authsource", "saga");
        properties.setProperty("offset.storage", "org.apache.kafka.connect.storage.MemoryOffsetBackingStore");
        properties.setProperty("provide.transaction.metadata", "true");
        properties.setProperty("snapshot.mode", "never");
        properties.setProperty("topic.prefix", "aux_");

        DebeziumEngine.Builder<ChangeEvent<byte[], byte[]>> builder = DebeziumEngine.create(JsonByteArray.class)
                .using(properties);

        return IntegrationFlow.from(Debezium.inboundChannelAdapter(builder))
                .transform(bytes -> {
                    try {
                        return objectMapper.readTree(new String((byte[]) bytes));
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException(e);
                    }
                })
                .filter(DebeziumUtil::filterOutTransaction)
                .handle(debeziumEventHandler)
                .get();
    }
}
