package io.github.sagapoctryone.service.movement;

import io.github.sagapoctryone.model.Movement;
import io.github.sagapoctryone.model.MovementBuilder;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.listener.MessageListener;

import java.util.List;


public abstract class MovementReceiver implements MessageListener<String, Movement> {

    @Autowired
    MovementBuilder movementBuilder;

    public abstract void onMessage(ConsumerRecord<String, Movement> data);

    public abstract List<String> getSteps();

    public abstract String getType();
}
