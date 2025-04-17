package io.github.sagapoctryone.model;

import lombok.Data;

import java.util.List;

@Data
public class Choreography {

    String choreographyId;

    List<String> previousPartners;

    // zis is stupid
    String body;

    //LocalDateTime timestamp;

    //String schemaVersion = "1.0";
}
