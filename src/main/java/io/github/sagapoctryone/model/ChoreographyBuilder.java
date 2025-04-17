package io.github.sagapoctryone.model;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;


@Component
public class ChoreographyBuilder {

    private static final String CHOREOGRAPHY_PREFIX = "choreography_";
    private static final String COMPENSATION_PREFIX = "choreography_compensation_";


    @Value("${spring.application.name}")
    private String applicationName;


    public String getNextChoreography(String step, String choreographyType) {
        if (choreographyType.equals("compensation")) {
            return getCompensation(step);
        } else {
            return getChoreography(step);
        }
    }

    private String getChoreography(String step) {
        return CHOREOGRAPHY_PREFIX + applicationName + "_" + step;
    }

    private String getCompensation(String step) {
        return COMPENSATION_PREFIX + step + "_" + applicationName;
    }
}
