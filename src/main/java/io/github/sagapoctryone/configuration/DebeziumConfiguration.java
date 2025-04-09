package io.github.sagapoctryone.configuration;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.debezium.engine.ChangeEvent;
import io.debezium.engine.DebeziumEngine;
import io.debezium.engine.format.JsonByteArray;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.debezium.dsl.Debezium;
import org.springframework.integration.dsl.IntegrationFlow;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import static lombok.AccessLevel.PRIVATE;

@Configuration
@RequiredArgsConstructor
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class DebeziumConfiguration {

    private static final TypeReference<Map<String, Object>> typeRef = new TypeReference<Map<String, Object>>() {
    };

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
        properties.setProperty("snapshot.mode", "when_needed");
        properties.setProperty("topic.prefix", "aux_");

        DebeziumEngine.Builder<ChangeEvent<byte[], byte[]>> builder = DebeziumEngine.create(JsonByteArray.class)
                .using(properties);

        return IntegrationFlow.from(Debezium.inboundChannelAdapter(builder))
                .transform(objectMapper::valueToTree)
                .<JsonNode>filter(node -> {
                    var schemaNameNode = node.path("schema").path("name");
                    return !schemaNameNode.isMissingNode()
                            && schemaNameNode.asText().equals("io.debezium.connector.common.TransactionMetadataValue");
                })
                .handle(message -> {
                    var node = (JsonNode) message.getPayload();

                    // todo  send to hazelcast
                    // todo  group by transactionId
                    // something something dark side
                    // something something credits
                    // todo extract data, parse and store
                    // todo istrazi hazelcast Jet cluster kako funkcionise

                    var transactionId = node.path("transactionId").path("id").asText();

                    System.out.println("~~~ Incoming message: " + message);
                    System.out.println("+++ Payload: " + new String((byte[]) message.getPayload()));
                }).get();

/*
        HazelcastInstance hz = Hazelcast.newHazelcastInstance();
        hz.getJet().getConfig().setEnabled(true);
        JetService jet = hz.getJet();

        JobConfig jobConfig = new JobConfig();
        jobConfig.setName("myProductionJob");
        jobConfig.setSnapshotIntervalMillis(15000);
        jobConfig.setProcessingGuarantee(ProcessingGuarantee.AT_LEAST_ONCE);

        jet.newJob(pipeline, jobConfig);
*/
    }

/*
    @Bean
    StreamSource<ChangeRecord> debeziumSources() {
        return DebeziumCdcSources.debezium("cdc", MongoDbConnector.class)
                .setProperty("mongodb.connection.string", "mongodb://127.0.0.1:27017/?replicaSet=rs0")
                .setProperty("mongodb.members.auto.discover", "true")
                .build();
    }
*/
}
