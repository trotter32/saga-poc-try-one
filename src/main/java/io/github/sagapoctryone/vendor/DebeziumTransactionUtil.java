package io.github.sagapoctryone.vendor;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.RestController;

import java.util.Base64;

import static lombok.AccessLevel.PRIVATE;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class DebeziumTransactionUtil {

    ObjectMapper objectMapper;


    // MongoDb vendor
    //todo   LOOOOL ovo izgleda ne treba xD
    public String mapTransactionId(String rawTransactionId) throws JsonProcessingException {
        var helper = rawTransactionId.replace("\\\"", "\"");
        var length = helper.length();
        var suffix = helper.substring(length - 1);

        var decodedBytes = Base64.getDecoder().decode(
                objectMapper.readTree(helper.substring(0, length - 2))
                        .path("uid").path("$binary").asText());

        var stringBuilder = new StringBuilder();
        for (byte b : decodedBytes) {
            stringBuilder.append(String.format("%02X", b));
        }

        stringBuilder.append("-");
        stringBuilder.append(suffix);

        return stringBuilder.toString();
    }

}
