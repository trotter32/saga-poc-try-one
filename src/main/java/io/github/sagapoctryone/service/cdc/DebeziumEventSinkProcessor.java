package io.github.sagapoctryone.service.cdc;

import com.hazelcast.jet.core.AbstractProcessor;
import com.hazelcast.multimap.MultiMap;
import jakarta.annotation.Nonnull;
import lombok.experimental.FieldDefaults;

import java.util.Map;

import static lombok.AccessLevel.PRIVATE;


@FieldDefaults(level = PRIVATE)
public class DebeziumEventSinkProcessor extends AbstractProcessor {

    MultiMap<String, String> multiMap;


    @Override
    protected boolean tryProcess(int ordinal, @Nonnull Object item) throws Exception {
        try {
            var entry = (Map.Entry<String, String>) item;
            multiMap.put(entry.getKey(), entry.getValue());
            return true;
        } catch (RuntimeException e) {
            return false;
        }
    }

    @Override
    protected void init(@Nonnull Context context) throws Exception {
        var hazelcastInstance = context.hazelcastInstance();
        multiMap = hazelcastInstance.getMultiMap("debeziumEventMap");
    }
}
