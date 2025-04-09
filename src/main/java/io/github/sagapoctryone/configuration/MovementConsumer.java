package io.github.sagapoctryone.configuration;


import io.github.sagapoctryone.model.Choreography;
import io.github.sagapoctryone.model.MovementBuilder;
import jakarta.annotation.PostConstruct;
import lombok.experimental.FieldDefaults;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.KafkaMessageListenerContainer;
import org.springframework.kafka.listener.MessageListener;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static lombok.AccessLevel.PRIVATE;

@Configuration
@FieldDefaults(level = PRIVATE)
public abstract class MovementConsumer {

    @Autowired
    MovementBuilder movementBuilder;
    @Autowired
    ConfigurableListableBeanFactory beanFactory;


    public abstract void execute();

    public abstract String getSource();

    @PostConstruct
    public void run() {
        var containerProps = new ContainerProperties(movementBuilder.getMovement(
                getSource()));


        containerProps.setMessageListener((MessageListener<String, Choreography>) record -> {
            Choreography choreography = record.value();

            MDC.put("transactionId", choreography.getChoreographyId());

            System.out.println("Received message: " + record.value());

            execute();
        });


        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "group");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class);
        props.put(ErrorHandlingDeserializer.KEY_DESERIALIZER_CLASS, StringDeserializer.class);
        props.put(ErrorHandlingDeserializer.VALUE_DESERIALIZER_CLASS, JsonDeserializer.class);
        props.put(JsonDeserializer.VALUE_DEFAULT_TYPE, "io.github.sagapoctryone.model.Choreography");
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");


        DefaultKafkaConsumerFactory<String, Choreography> cf = new DefaultKafkaConsumerFactory<>(props);
        KafkaMessageListenerContainer<String, Choreography> container =
                new KafkaMessageListenerContainer<>(cf, containerProps);


        beanFactory.registerSingleton(UUID.randomUUID().toString(), container);
    }
}
