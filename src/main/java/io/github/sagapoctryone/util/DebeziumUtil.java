package io.github.sagapoctryone.util;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.experimental.UtilityClass;

@UtilityClass
public class DebeziumUtil {




    public static boolean filterPayloadStatus(final JsonNode node, final String payloadStatus) {
        var schemaNameNode = node.path("schema").path("name");
        var payloadStatusNode = node.path("payload").path("status");
        return filterOutTransaction(schemaNameNode)
                && !payloadStatusNode.isMissingNode()
                && payloadStatusNode.asText().equals(payloadStatus);
    }


    public static boolean filterOutTransaction(final JsonNode node) {
        var schemaNameNode = node.path("schema").path("name");
        return !schemaNameNode.isMissingNode()
                && !schemaNameNode.asText().equals("io.debezium.connector.common.TransactionMetadataValue");
    }
}
