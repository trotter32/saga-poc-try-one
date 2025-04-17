package io.github.sagapoctryone.service.choreography;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.hazelcast.map.IMap;
import com.hazelcast.multimap.MultiMap;
import io.github.sagapoctryone.model.Choreography;
import io.github.sagapoctryone.repository.AuxiliaryMovementRepository;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.context.ApplicationContext;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RestController;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;

import static java.util.Collections.singletonList;
import static lombok.AccessLevel.PRIVATE;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class GenericCompensationChoreographyReceiver extends CompensationChoreographyReceiver {

    AuxiliaryMovementRepository auxiliaryMovementRepository;
    ObjectMapper objectMapper;
    ApplicationContext applicationContext;
    MultiMap<String, String> debeziumMovementMap;
    IMap<String, String> auxiliaryMovementMap;
    MultiMap<String, Map<String, String>> auxiliaryMovementStepsMap;


    @Override
    @Transactional
    public void onMessage(ConsumerRecord<String, Choreography> data) {
        try {
            onMessageInternal(data.value().getChoreographyId());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    private void onMessageInternal(String choreographyId) throws JsonProcessingException, ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        var auxiliary = auxiliaryMovementRepository.findByChoreographyId(choreographyId);
        var auxiliaryMovementSteps = (List<Map<String, String>>) auxiliaryMovementStepsMap.get(auxiliary.getId());
        var debeziumMovements = (List<String>) debeziumMovementMap.get(auxiliaryMovementMap.get(auxiliary.getChoreographyId()));

        for (int i = 0; i < debeziumMovements.size(); i++) {
            var auxMovementSteps = auxiliaryMovementSteps.get(i);
            var debeziumMovement = objectMapper.readTree(debeziumMovements.get(i));

            var rollbackObjectJson = (ObjectNode) debeziumMovement.path("payload");
            if (rollbackObjectJson.get("_id") != null) {
                rollbackObjectJson.set("id", rollbackObjectJson.get("_id").get("$oid"));
            }

            var rollbackObject = objectMapper.treeToValue(rollbackObjectJson,
                    Class.forName(auxMovementSteps.get("argumentClass")));
            var repository = (CrudRepository<?, ?>) applicationContext.getBean(
                    Class.forName(auxMovementSteps.get("repositoryClas")));

            var method = repository.getClass().getMethod(methodNameResolver(debeziumMovement), Object.class);
            method.invoke(repository, rollbackObject);
        }

        //todo handle errors
        //
    }

    private String methodNameResolver(JsonNode debeziumMovement) {
        return switch (debeziumMovement.path("op").asText()) {
            case "c" -> "delete";
            case "d" -> "save";
            default -> throw new IllegalStateException("Unexpected value: " + debeziumMovement.path("op").asText());
        };
    }

    //todo  Ovde treba da cupam iz liste svih povezanih servisa
    @Override
    public List<String> getSteps() {
        return singletonList("B");
    }
}
