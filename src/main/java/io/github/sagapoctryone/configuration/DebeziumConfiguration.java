package io.github.sagapoctryone.configuration;

import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.jet.JetService;
import com.hazelcast.jet.cdc.ChangeRecord;
import com.hazelcast.jet.cdc.DebeziumCdcSources;
import com.hazelcast.jet.config.JobConfig;
import com.hazelcast.jet.config.ProcessingGuarantee;
import com.hazelcast.jet.pipeline.Pipeline;
import com.hazelcast.jet.pipeline.Sinks;
import com.hazelcast.jet.pipeline.StreamSource;
import io.debezium.connector.mongodb.MongoDbConnector;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static lombok.AccessLevel.PRIVATE;

@Configuration
@RequiredArgsConstructor
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class DebeziumConfiguration {

    @Bean
    Pipeline pipeline() {
        StreamSource<ChangeRecord> source =
                DebeziumCdcSources.debezium("source", MongoDbConnector.class)
                .setProperty("mongodb.name", "source")
                .setProperty("mongodb.hosts", "localhost:27017")
                .setProperty("mongodb.user", "")
                .setProperty("mongodb.password", "")
                .setProperty("mongodb.authsource", "local")
                .build();

        Pipeline pipeline = Pipeline.create();
        pipeline.readFrom(source)
                .withoutTimestamps()
                .peek()
                .writeTo(Sinks.logger());


        HazelcastInstance hz = Hazelcast.newHazelcastInstance();
        hz.getJet().getConfig().setEnabled(true);
        JetService jet = hz.getJet();

        JobConfig jobConfig = new JobConfig();
        jobConfig.setName("myProductionJob");
        jobConfig.setSnapshotIntervalMillis(15000);
        jobConfig.setProcessingGuarantee(ProcessingGuarantee.AT_LEAST_ONCE);

        jet.newJob(pipeline, jobConfig);

        return pipeline;
    }

/*    @Bean
    StreamSource<ChangeRecord> debeziumSources() {
        return DebeziumCdcSources.debezium("cdc", MongoDbConnector.class)
                .setProperty("mongodb.connection.string", "mongodb://127.0.0.1:27017/?replicaSet=rs0")
                .setProperty("mongodb.members.auto.discover", "true")
                .build();
    }*/
}
