package io.github.sagapoctryone.testing;

import com.fasterxml.jackson.databind.JsonNode;
import com.hazelcast.core.HazelcastInstance;
import io.github.sagapoctryone.configuration.MovementConsumerConfiguration;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Base64;
import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class FooBar {

    FooRepository fooRepository;
    HazelcastInstance hazelcastInstance;
    List<MovementConsumerConfiguration> movementConsumerConfigurations;

    @GetMapping("/foo")
    @Transactional
    public void foo() {
        var foo = new Foo();
        //foo.setId("aqfwqfqw");
        foo.setBar("hello there");
        fooRepository.save(foo);

        //var set = hazelcastInstance.getSet("deduplication");
    }

    @GetMapping("/bar")
    @Transactional
    public void bar() {
        System.out.println("Velicina multimape " + hazelcastInstance.getMultiMap("debeziumEventMap").size());

        hazelcastInstance.getMultiMap("debeziumEventMap").entrySet().stream()
                .forEach(entry -> System.out.println(Base64.getDecoder().decode(((JsonNode) entry.getValue()).path("transaction").path("id").asText())));
        //var set = hazelcastInstance.getSet("deduplication");
    }
}
