package io.github.sagapoctryone.configuration;


import io.github.sagapoctryone.service.choreography.BaseChoreographyReceiver;
import io.github.sagapoctryone.model.ChoreographyBuilder;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static lombok.AccessLevel.PRIVATE;

@Configuration
@RequiredArgsConstructor
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class ChoreographyConsumerConfiguration {

    ChoreographyBuilder choreographyBuilder;
    ConfigurableListableBeanFactory beanFactory;
    List<BaseChoreographyReceiver> compensationBaseChoreographyReceivers;


    @PostConstruct
    public void run() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "group");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class);
        props.put(ErrorHandlingDeserializer.KEY_DESERIALIZER_CLASS, StringDeserializer.class);
        props.put(ErrorHandlingDeserializer.VALUE_DESERIALIZER_CLASS, JsonDeserializer.class);
        props.put(JsonDeserializer.VALUE_DEFAULT_TYPE, "io.github.sagapoctryone.model.Movement");
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

/*
        compensationMovementReceivers.forEach(
                compensationMovementReceiver -> compensationMovementReceiver.getSteps().stream()
                        .map(step -> movementBuilder.getNextChoreography(step, compensationMovementReceiver.getType()))
                        .forEach(movement -> {
                            var containerProps = new ContainerProperties(movement);
                            containerProps.setMessageListener(compensationMovementReceiver);

                            DefaultKafkaConsumerFactory<String, Movement> consumerFactory = new DefaultKafkaConsumerFactory<>(props);
                            KafkaMessageListenerContainer<String, Movement> container =
                                    new KafkaMessageListenerContainer<>(consumerFactory, containerProps);

                            beanFactory.registerSingleton(UUID.randomUUID().toString(), container);
                        }));
*/
    }
}
