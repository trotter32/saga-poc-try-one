package io.github.sagapoctryone.model;


import lombok.Data;
import org.springframework.data.annotation.Id;

@Data
public class Auxiliary {
    @Id
    String id;

    String choreographyId;
}
