package io.github.sagapoctryone.configuration;

import com.hazelcast.collection.IQueue;
import com.hazelcast.config.MultiMapConfig;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.map.IMap;
import com.hazelcast.multimap.MultiMap;
import io.debezium.engine.ChangeEvent;
import io.debezium.engine.DebeziumEngine;
import io.debezium.engine.format.Json;
import io.debezium.engine.format.JsonByteArray;
import io.debezium.engine.format.KeyValueHeaderChangeEventFormat;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;
import java.util.concurrent.LinkedBlockingQueue;

import static lombok.AccessLevel.PRIVATE;

@Configuration
@RequiredArgsConstructor
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class DebeziumConfiguration {


    @Bean
    DebeziumEngine<ChangeEvent<String, byte[]>> debeziumEngine(IQueue<byte[]> queue) {
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

        DebeziumEngine<ChangeEvent<String, byte[]>> debeziumEngine =
                DebeziumEngine.create(KeyValueHeaderChangeEventFormat.of(Json.class, JsonByteArray.class, Json.class),
                                "io.debezium.embedded.async.ConvertingAsyncEngineBuilderFactory")
                        .using(properties)
                        //todo handle warning
                        .notifying(event -> System.out.println("~~~ " + event))
                        .notifying(event -> queue.offer(event.value()))
                        .build();

        return debeziumEngine;
    }

    @Bean
    public MultiMap<String, String> debeziumEventMap(HazelcastInstance hazelcastInstance) {
        MultiMapConfig mapConfig = new MultiMapConfig("debeziumEventMap");
        mapConfig.setValueCollectionType(MultiMapConfig.ValueCollectionType.LIST);
        return hazelcastInstance.getMultiMap("debeziumEventMap");
    }

    @Bean
    public IMap<String, String> auxiliaryEventMap(HazelcastInstance hazelcastInstance) {
        return hazelcastInstance.getMap("auxiliaryEventMap");
    }

    @Bean
    public IQueue<byte[]> cdcEventQueue(HazelcastInstance hazelcastInstance) {
        return hazelcastInstance.getQueue("cdcEventQueue");
    }
}
