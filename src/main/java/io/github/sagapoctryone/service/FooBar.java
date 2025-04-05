package io.github.sagapoctryone.service;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import static lombok.AccessLevel.PRIVATE;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class FooBar {

    FooRepository fooRepository;

    @GetMapping("/foo")
    @Transactional
    public void foo() {
        var foo = new Foo();
        foo.setBar("hello there");
        fooRepository.save(foo);

        //var set = hazelcastInstance.getSet("deduplication");
    }
}
