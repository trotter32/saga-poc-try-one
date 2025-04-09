package io.github.sagapoctryone.model;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;


@Component
public class MovementBuilder {

    private static final String MOVEMENT_PREFIX = "movement_";
    private static final String COMPENSATION_PREFIX = "movement_compensation_";

    @Value("${spring.application.name}")
    private String applicationName;


    public String getMovement(String nextStep) {
        return MOVEMENT_PREFIX + applicationName + "_" + nextStep;
    }

    public String getCompensation(String nextStep) {
        return COMPENSATION_PREFIX + nextStep + "_" + applicationName;
    }
}
