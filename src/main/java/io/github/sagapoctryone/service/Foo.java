package io.github.sagapoctryone.service;


import lombok.Data;
import org.springframework.data.annotation.Id;

@Data
public class Foo {

    @Id
    String id;

    String bar;
}
