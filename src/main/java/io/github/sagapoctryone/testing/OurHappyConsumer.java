package io.github.sagapoctryone.testing;

import io.github.sagapoctryone.configuration.MovementConsumer;
import io.github.sagapoctryone.service.Foo;
import io.github.sagapoctryone.service.FooRepository;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.RestController;

import static lombok.AccessLevel.PRIVATE;


@RestController
@RequiredArgsConstructor
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class OurHappyConsumer extends MovementConsumer {

    FooRepository fooRepository;


    @Override
    public void execute() {
        var foo = new Foo();
        foo.setBar("tvoja mama se gleda sa nama");
        fooRepository.save(foo);
    }

    @Override
    public String getSource() {
        return "B";
    }
}
