package io.github.sagapoctryone;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Base64;

@SpringBootTest
class SagaPocTryOneApplicationTests {

    @Test
    void contextLoads() throws JsonProcessingException, JSONException {
        var string = "\"{\\\"id\\\": {\\\"$binary\\\": \\\"/I2L2/TJT4uwbarnr+N/MA==\\\",\\\"$type\\\": \\\"04\\\"},\\\"uid\\\": {\\\"$binary\\\": \\\"47DEQpj8HBSa+/TImW+5JCeuQeRkm5NMpJWZG3hSuFU=\\\",\\\"$type\\\": \\\"00\\\"}}:1"
            .replace("\\\"", "\"");
        var length = string.length();
        var suffix = string.substring(length - 1);
        var json =  //(string.substring(0, length - 2));
                new ObjectMapper().readTree(string.substring(1, length - 2));

        var decodedBytes =
                Base64.getDecoder().decode(json.path("uid").path("$binary").asText());
        var stringBuilder = new StringBuilder();
        for (byte b : decodedBytes) {
            stringBuilder.append(String.format("%02X", b));
        }

        stringBuilder.append("-");
        stringBuilder.append(suffix);

        System.out.println(stringBuilder);
    }
}
