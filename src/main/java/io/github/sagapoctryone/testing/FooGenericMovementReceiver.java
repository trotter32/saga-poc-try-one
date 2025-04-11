package io.github.sagapoctryone.testing;


import io.github.sagapoctryone.model.Movement;
import io.github.sagapoctryone.service.movement.GenericMovementReceiver;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static java.util.Collections.singletonList;
import static lombok.AccessLevel.PRIVATE;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class FooGenericMovementReceiver extends GenericMovementReceiver {

    FooRepository fooRepository;


    @Override
    public void onMessage(ConsumerRecord<String, Movement> data) {
        System.out.println("++++ FooMovement: " + data);

        var foo = new Foo();
        foo.setBar("tvoja mama se gleda sa nama");
        fooRepository.save(foo);
    }


    @Override
    public List<String> getSteps() {
        return singletonList("B");
    }

    // take orchestrationId
}
