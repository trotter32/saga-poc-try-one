package io.github.sagapoctryone.configuration;

import com.hazelcast.config.Config;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.jet.Job;
import com.hazelcast.jet.pipeline.Pipeline;
import com.hazelcast.jet.pipeline.Sink;
import com.hazelcast.jet.pipeline.SourceBuilder;
import com.hazelcast.jet.pipeline.StreamSource;
import com.hazelcast.spring.context.SpringManagedContext;
import io.github.sagapoctryone.service.movement.AuxiliaryMovementMapper;
import io.github.sagapoctryone.service.movement.CdcMovementMapper;
import io.github.sagapoctryone.service.movement.DebeziumMovementMapper;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.log4j.Log4j2;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.util.Map;

import static com.hazelcast.core.Hazelcast.newHazelcastInstance;
import static io.github.sagapoctryone.service.movement.MovementFilterProvider.auxiliaryMovemeventFilter;
import static io.github.sagapoctryone.service.movement.MovementFilterProvider.cdcMovementPreFilter;
import static lombok.AccessLevel.PRIVATE;


@Configuration(proxyBeanMethods = false)
@RequiredArgsConstructor
@FieldDefaults(level = PRIVATE, makeFinal = true)
@Log4j2
public class HazelcastConfiguration {

    DebeziumMovementMapper debeziumMovementMapper;
    AuxiliaryMovementMapper auxiliaryMovementMapper;
    CdcMovementMapper cdcMovementMapper;
    Sink<Map.Entry<String, String>> debeziumMovementDestination;
    Sink<Map.Entry<String, String>> auxiliaryMovementDestination;


    @Bean
    public Job hazelcastJob(HazelcastInstance hazelcastInstance) {
        var pipeline = Pipeline.create();

        var streamSource = pipeline.readFrom(streamSource())
                .withoutTimestamps()
                .map(cdcMovementMapper)
                .filter(cdcMovementPreFilter());

        streamSource
                .filter(auxiliaryMovemeventFilter().negate())
                .map(debeziumMovementMapper)
                .writeTo(debeziumMovementDestination);

        streamSource
                .filter(cdcMovementPreFilter())
                .filter(auxiliaryMovemeventFilter())
                .map(auxiliaryMovementMapper)
                .writeTo(auxiliaryMovementDestination);

        return hazelcastInstance.getJet().newJob(pipeline);
    }

    private StreamSource<byte[]> streamSource() {
        return SourceBuilder.stream("queueStream", context ->
                        context.hazelcastInstance().<byte[]>getQueue("cdcMovementQueue"))
                .<byte[]>fillBufferFn((queue, buffer) -> {
                    byte[] item;
                    if ((item = queue.poll()) != null) {
                        buffer.add(item);
                    }
                }).build();
    }

    @Bean
    @Primary
    public HazelcastInstance hazelcastInstance() {
        Config config = new Config();
        config.getJetConfig().setEnabled(true);

        return newHazelcastInstance(config);
    }
}
