package io.github.sagapoctryone.service.choreography;

import io.github.sagapoctryone.model.Choreography;
import io.github.sagapoctryone.model.ChoreographyBuilder;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.listener.MessageListener;

import java.util.List;


public abstract class BaseChoreographyReceiver implements MessageListener<String, Choreography> {

    @Autowired
    ChoreographyBuilder choreographyBuilder;

    public abstract void onMessage(ConsumerRecord<String, Choreography> data);

    public abstract List<String> getSteps();

    public abstract String getType();
}
