package io.github.sagapoctryone.configuration;


import com.hazelcast.config.MultiMapConfig;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.map.IMap;
import com.hazelcast.multimap.MultiMap;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import static lombok.AccessLevel.PRIVATE;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class DebeziumEventConfiguration {

    @Bean
    public MultiMap<String, String> debeziumEventMap(HazelcastInstance hazelcastInstance) {
        MultiMapConfig mapConfig = new MultiMapConfig("debeziumEventMap");
        mapConfig.setValueCollectionType(MultiMapConfig.ValueCollectionType.LIST);
        return hazelcastInstance.getMultiMap("debeziumEventMap");
    }

    @Bean
    public IMap<String, String> choreographyIdTransactionIdMap(HazelcastInstance hazelcastInstance) {
        return hazelcastInstance.getMap("choreographyIdTransactionIdMap");
    }
}
