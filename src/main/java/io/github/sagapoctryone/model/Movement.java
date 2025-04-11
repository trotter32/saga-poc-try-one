package io.github.sagapoctryone.model;

import lombok.Data;

import java.util.List;

@Data
public class Movement {

    String choreographyId;

    List<String> finishedSteps;

    // zis is stupid
    String body;

    //LocalDateTime timestamp;

    //String schemaVersion = "1.0";
}
