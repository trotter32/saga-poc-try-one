package io.github.sagapoctryone.testing;

import com.fasterxml.jackson.databind.JsonNode;
import com.hazelcast.core.HazelcastInstance;
import io.github.sagapoctryone.configuration.ChoreographyConsumerConfiguration;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.transaction.ReactiveTransactionManager;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.reactive.TransactionalOperator;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.concurrent.Executors;

import static lombok.AccessLevel.PRIVATE;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class FooBar {

    FooRepository fooRepository;
    HazelcastInstance hazelcastInstance;
    List<ChoreographyConsumerConfiguration> choreographyConsumerConfigurations;

    Scheduler scheduler = Schedulers.fromExecutor(Executors.newVirtualThreadPerTaskExecutor());



    @GetMapping("/foo")
    @Transactional
    public void foo() {
        try {
            fooRepository.save(new Foo());
        } catch (Throwable e){
            System.out.printf("");
        }


        //var set = hazelcastInstance.getSet("deduplication");
    }


    @GetMapping("/foo/async")
    public void fooAsync() {
        var foo = new Foo();
        //foo.setId("aqfwqfqw");
        foo.setBar("hello there");

        Flux.range(1, 5000).flatMap(i ->
                Mono.fromCallable(() -> {
                    try (var client = HttpClient.newHttpClient()) {
                        var request = HttpRequest.newBuilder().uri(URI.create("http://localhost:8080/foo")).build();
                        return client.send(request, HttpResponse.BodyHandlers.ofString());
                    }
                }).subscribeOn(scheduler), 5000).blockLast();

        //var set = hazelcastInstance.getSet("deduplication");
    }


    @GetMapping("/bar")
    @Transactional
    public void bar() {
        System.out.println("Velicina multimape " + hazelcastInstance.getMultiMap("debeziumMovementMap").size());

        hazelcastInstance.getMultiMap("debeziumMovementMap").entrySet().stream()
                .forEach(entry -> System.out.println(Base64.getDecoder().decode(((JsonNode) entry.getValue()).path("transaction").path("id").asText())));
        //var set = hazelcastInstance.getSet("deduplication");
    }
}
