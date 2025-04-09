package io.github.sagapoctryone.model;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class Choreography {

    String choreographyId;

    List<String> finishedSteps;

    // zis is stupid
    String body;

    //LocalDateTime timestamp;

    //String schemaVersion = "1.0";
}
