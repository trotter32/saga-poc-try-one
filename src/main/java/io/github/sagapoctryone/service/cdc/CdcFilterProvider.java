package io.github.sagapoctryone.service.cdc;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.hazelcast.function.PredicateEx;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.context.annotation.Configuration;

import static lombok.AccessLevel.PRIVATE;


@Configuration
@RequiredArgsConstructor
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class CdcFilterProvider {

    public static PredicateEx<ObjectNode> cdcEventPreFilter() {
        return node -> !extractSchemaName(node).contains("repoCallArg");
    }

    public static PredicateEx<ObjectNode> auxiliaryEventFilter() {
        return node -> extractSchemaName(node).contains("auxiliary");
    }

    public static PredicateEx<ObjectNode> debeziumEventFilter() {
        return node -> {
            System.out.println("+++" + extractSchemaName(node));

           return extractSchemaName(node).contains("debeziumEvent");
        };
    }

    private static String extractSchemaName(ObjectNode node) {
        return node.path("schema").path("name").asText();
    }
}
