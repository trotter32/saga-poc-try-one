package io.github.sagapoctryone.service;

import com.hazelcast.core.HazelcastInstance;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

import static lombok.AccessLevel.PRIVATE;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class FooBar {

    FooRepository fooRepository;
    JdbcTemplate jdbcTemplate;

    @GetMapping("/foo")
    public void foo() {
        var foo = new Foo();
        foo.setId((int)(Math.random() * 101));
        foo.setBar("hello there");
        fooRepository.save(foo);

        //var set = hazelcastInstance.getSet("deduplication");
    }
}
