package io.github.sagapoctryone.configuration;

import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.jet.Jet;
import com.hazelcast.jet.JetInstance;
import com.hazelcast.jet.JetService;
import com.hazelcast.jet.cdc.ChangeRecord;
import com.hazelcast.jet.cdc.DebeziumCdcSources;
import com.hazelcast.jet.cdc.mysql.MySqlCdcSources;
import com.hazelcast.jet.config.JobConfig;
import com.hazelcast.jet.config.ProcessingGuarantee;
import com.hazelcast.jet.pipeline.Pipeline;
import com.hazelcast.jet.pipeline.Sinks;
import com.hazelcast.jet.pipeline.StreamSource;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.boot.sql.init.dependency.DependsOnDatabaseInitialization;
import org.springframework.context.annotation.Bean;

import static lombok.AccessLevel.PRIVATE;

@org.springframework.context.annotation.Configuration
@RequiredArgsConstructor
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class DebeziumConfiguration {

    @PostConstruct
    Pipeline pipeline() {
        StreamSource<ChangeRecord> source = MySqlCdcSources.mysql("source")
                .setCustomProperty("database.server.name", "source")
                .setCustomProperty("database.allowPublicKeyRetrieval", "true")
                .setDatabaseAddress("127.0.0.1")
                .setDatabasePort(3306)
                .setDatabaseUser("root")
                .setDatabasePassword("")
                //.setClusterName("dbserver1")
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
