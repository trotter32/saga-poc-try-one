package io.github.sagapoctryone.service.movement;

import com.hazelcast.jet.core.AbstractProcessor;
import com.hazelcast.map.IMap;
import jakarta.annotation.Nonnull;
import lombok.experimental.FieldDefaults;
import lombok.extern.log4j.Log4j2;

import java.util.Map;

import static lombok.AccessLevel.PRIVATE;


@Log4j2
@FieldDefaults(level = PRIVATE)
public class AuxiliaryMovementSinkProcessor extends AbstractProcessor {

    IMap<String, String> map;


    @Override
    protected boolean tryProcess(int ordinal, @Nonnull Object item) throws Exception {
        try {
            var entry = (Map.Entry<String, String>) item;
            map.put(entry.getKey(), entry.getValue());
            return true;
        } catch (RuntimeException e) {
            return false;
        }
    }

    @Override
    protected void init(@Nonnull Context context) throws Exception {
        var hazelcastInstance = context.hazelcastInstance();
        map = hazelcastInstance.getMap("auxiliaryMovementMap");
    }
}
