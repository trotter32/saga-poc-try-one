package io.github.sagapoctryone.service.movement;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.hazelcast.function.PredicateEx;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Configuration;

import static lombok.AccessLevel.PRIVATE;


@Configuration
@RequiredArgsConstructor
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class MovementFilterProvider {

    public static PredicateEx<ObjectNode> cdcMovementPreFilter() {
        return node ->
                !StringUtils.containsAny(extractSchemaName(node), ".auxiliaryMovementSteps.", "TransactionMetadataValue");
    }

    public static PredicateEx<ObjectNode> auxiliaryMovemeventFilter() {
        return node -> extractSchemaName(node).contains(".auxiliaryMovement.");
    }

    public static PredicateEx<ObjectNode> debeziumEventFilter() {
        return node -> {
            System.out.println("+++" + extractSchemaName(node));

           return extractSchemaName(node).contains("debeziumMovement");
        };
    }

    private static String extractSchemaName(ObjectNode node) {
        return node.path("schema").path("name").asText();
    }
}
