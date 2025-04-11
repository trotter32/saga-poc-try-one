package io.github.sagapoctryone.model;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;


@Component
public class MovementBuilder {

    private static final String MOVEMENT_PREFIX = "movement_";
    private static final String COMPENSATION_PREFIX = "movement_compensation_";


    @Value("${spring.application.name}")
    private String applicationName;


    public String getNextMovement(String step, String movementType) {
        if (movementType.equals("compensation")) {
            return getCompensation(step);
        } else {
            return getMovement(step);
        }
    }

    private String getMovement(String step) {
        return MOVEMENT_PREFIX + applicationName + "_" + step;
    }

    private String getCompensation(String step) {
        return COMPENSATION_PREFIX + step + "_" + applicationName;
    }
}
