package io.github.sagapoctryone.testing;


import lombok.Data;
import org.springframework.data.annotation.Id;

@Data
public class Foo {

    @Id
    String id;

    String bar;
}
