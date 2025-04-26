package io.github.sagapoctryone.service;

import io.debezium.engine.ChangeEvent;
import io.debezium.engine.DebeziumEngine;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class DebeziumConsumer {}/*implements DebeziumEngine.ChangeConsumer<ChangeEvent<String, byte[]>> {

    @Override
    public void handleBatch(List<ChangeEvent<String, byte[]>> list, DebeziumEngine.RecordCommitter<ChangeEvent<String, byte[]>> recordCommitter) throws InterruptedException {

        list.forEach(changeevent -> {
            try {
                System.out.println("hello working code maybe");
                recordCommitter.markProcessed(changeevent);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
        recordCommitter.markBatchFinished();
    }
}

*/