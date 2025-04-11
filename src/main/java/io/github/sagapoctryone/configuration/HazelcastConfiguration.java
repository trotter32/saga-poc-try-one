package io.github.sagapoctryone.configuration;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.jet.Job;
import com.hazelcast.jet.pipeline.*;
import com.hazelcast.multimap.MultiMap;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.rest.webmvc.support.BackendId;

import static lombok.AccessLevel.PRIVATE;


@Configuration
@RequiredArgsConstructor
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class HazelcastConfiguration {

    HazelcastInstance hazelcastInstance;

    @Bean
    public Job hazelcastJob() {
        Pipeline pipeline = Pipeline.create();
        pipeline.readFrom(streamSource())
                .writeTo(Sinks.logger());

        return hazelcastInstance.getJet().newJob(pipeline);
    }

    @Bean
    public BatchSource<String> streamSource() {
        return SourceBuilder.<MultiMap<String, String>>batch("queueStream",
                        context -> context.hazelcastInstance().getMultiMap("debeziumEventMap"))
                .<String>fillBufferFn((map, buf) -> {
                    map.keySet().forEach(buf::add);
                    buf.close();
                    System.out.println("buffer executed");
                }).build();
    }
}
