package io.github.sagapoctryone.configuration;


import com.hazelcast.collection.IQueue;
import com.hazelcast.config.MultiMapConfig;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.jet.core.ProcessorMetaSupplier;
import com.hazelcast.jet.pipeline.Sink;
import com.hazelcast.jet.pipeline.Sinks;
import com.hazelcast.map.IMap;
import com.hazelcast.multimap.MultiMap;
import io.github.sagapoctryone.service.movement.AuxiliaryMovementSinkProcessor;
import io.github.sagapoctryone.service.movement.DebeziumMovementSinkProcessor;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

import static lombok.AccessLevel.PRIVATE;

@Configuration(proxyBeanMethods = false)
@RequiredArgsConstructor
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class MovementConfiguration {

    @Bean
    public Sink<Map.Entry<String, String>> debeziumMovementDestination() {
        return Sinks.fromProcessor("debeziumMovementDestination",
                ProcessorMetaSupplier.of(DebeziumMovementSinkProcessor::new));
    }

    @Bean
    public Sink<Map.Entry<String, String>> auxiliaryMovementDestination() {
        return Sinks.fromProcessor("auxiliaryMovementDestination",
                ProcessorMetaSupplier.of(AuxiliaryMovementSinkProcessor::new));
    }

    @Bean
    public MultiMap<String, String> debeziumMovementMap(HazelcastInstance hazelcastInstance) {
        MultiMapConfig mapConfig = new MultiMapConfig("debeziumMovementMap");
        mapConfig.setValueCollectionType(MultiMapConfig.ValueCollectionType.LIST);
        return hazelcastInstance.getMultiMap("debeziumMovementMap");
    }

    @Bean
    public IMap<String, String> auxiliaryMovementMap(HazelcastInstance hazelcastInstance) {
        return hazelcastInstance.getMap("auxiliaryMovementMap");
    }

    @Bean
    public MultiMap<String, Map<String, String>> AuxiliaryMovementStepsMap(HazelcastInstance hazelcastInstance) {
        MultiMapConfig mapConfig = new MultiMapConfig("auxiliaryMovementStepsMap");
        mapConfig.setValueCollectionType(MultiMapConfig.ValueCollectionType.LIST);
        return hazelcastInstance.getMultiMap("auxiliaryMovementStepsMap");
    }

    @Bean
    public IQueue<byte[]> cdcMovementQueue(HazelcastInstance hazelcastInstance) {
        return hazelcastInstance.getQueue("cdcMovementQueue");
    }
}
