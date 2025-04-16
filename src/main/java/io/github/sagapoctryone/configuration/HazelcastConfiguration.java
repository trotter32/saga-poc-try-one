package io.github.sagapoctryone.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.hazelcast.config.Config;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.function.FunctionEx;
import com.hazelcast.jet.Job;
import com.hazelcast.jet.core.ProcessorMetaSupplier;
import com.hazelcast.jet.pipeline.*;
import com.hazelcast.spring.context.SpringManagedContext;
import io.github.sagapoctryone.service.cdc.*;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.util.Map;

import static com.hazelcast.core.Hazelcast.newHazelcastInstance;
import static io.github.sagapoctryone.service.cdc.CdcFilterProvider.*;
import static lombok.AccessLevel.PRIVATE;


@Configuration(proxyBeanMethods = false)
@RequiredArgsConstructor
@FieldDefaults(level = PRIVATE)
public class HazelcastConfiguration {

    final DebeziumEventMapper debeziumEventMapper;
    final AuxiliaryEventMapper auxiliaryEventMapper;
    final CdcEventMapper cdcEventMapper;


    @Bean
    public Job hazelcastJob(HazelcastInstance hazelcastInstance) {
        var pipeline = Pipeline.create();

        var streamSourceStage = pipeline
                .readFrom(streamSource());


/*        var streamStage = pipeline
                .readFrom(streamSource())

                .withIngestionTimestamps()
                .map(cdcEventMapper)
                .filter(cdcEventPreFilter());*/

/*        streamSourceStage
                .withIngestionTimestamps()
                .map(cdcEventMapper)
                .filter(cdcEventPreFilter())
                .filter(auxiliaryEventFilter())
                .map(auxiliaryEventMapper)
                .writeTo(getAuxiliaryEventSink());*/

        streamSourceStage
                .withIngestionTimestamps()
                .map(cdcEventMapper)
                .filter(cdcEventPreFilter())
                .filter(debeziumEventFilter())
                .map(debeziumEventMapper)
                .writeTo(getDebeziumEventSink());

        return hazelcastInstance.getJet().newJob(pipeline);
    }

    private StreamSource<byte[]> streamSource() {
        return SourceBuilder.stream("queueStream", context ->
                        context.hazelcastInstance().<byte[]>getQueue("cdcEventQueue"))
                .<byte[]>fillBufferFn((queue, buffer) -> {
                    byte[] item;
                    while ((item = queue.poll()) != null) {
                        buffer.add(item);
                    }
                }).build();
    }

    @Bean
    @Primary
    public HazelcastInstance hazelcastInstance(SpringManagedContext springManagedContext) {
        Config config = new Config();
        config.setManagedContext(springManagedContext);
        config.getJetConfig().setEnabled(true);

        return newHazelcastInstance(config);
    }

    @Bean
    public SpringManagedContext springManagedContext(ApplicationContext applicationContext) {
        return new SpringManagedContext(applicationContext);
    }

    private Sink<Map.Entry<String, String>> getDebeziumEventSink() {
        return Sinks.fromProcessor("debeziumEventSink",
                ProcessorMetaSupplier.of(DebeziumEventSinkProcessor::new));
    }

    private Sink<Map.Entry<String, String>> getAuxiliaryEventSink() {
        return Sinks.fromProcessor("auxiliaryEventEventSink",
                ProcessorMetaSupplier.of(AuxiliaryEventSinkProcessor::new));
    }
}
