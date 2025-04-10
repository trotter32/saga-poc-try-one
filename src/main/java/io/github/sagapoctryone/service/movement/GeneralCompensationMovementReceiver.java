package io.github.sagapoctryone.service.movement;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.hazelcast.map.IMap;
import com.hazelcast.multimap.MultiMap;
import io.github.sagapoctryone.model.Movement;
import io.github.sagapoctryone.repository.AuxiliaryRepository;
import io.github.sagapoctryone.repository.RepoCallArgRepository;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.context.ApplicationContext;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RestController;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import static java.util.Collections.singletonList;
import static lombok.AccessLevel.PRIVATE;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class GeneralCompensationMovementReceiver extends CompensationMovementReceiver {

    AuxiliaryRepository auxiliaryRepository;
    RepoCallArgRepository repoCallArgRepository;
    ObjectMapper objectMapper;
    ApplicationContext applicationContext;
    MultiMap<String, String> debeziumEventMap;
    IMap<String, String> choreographyIdTransactionIdMap;

    @Override
    @Transactional
    public void onMessage(ConsumerRecord<String, Movement> data) {
        try {
            onMessageInternal(data.value().getChoreographyId());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    private void onMessageInternal(String choreographyId) throws JsonProcessingException, ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        var auxiliary = auxiliaryRepository.findByChoreographyId(choreographyId);
        var repoCallArguments = repoCallArgRepository.findByAuxiliaryIdOrderByIdAsc(auxiliary.getId());
        var debeziumEvents = debeziumEventMap.get(choreographyIdTransactionIdMap.get(auxiliary.getChoreographyId()))
                .stream().map(String::valueOf).toList();

        for (int i = 0; i < debeziumEvents.size(); i++) {
            var argument = repoCallArguments.get(i);
            var debeziumEvent = objectMapper.readTree(debeziumEvents.get(i));
            String rollbackObjectString;
            String methodName;

            if (debeziumEvent.path("op").asText().equals("c")) {
                rollbackObjectString = debeziumEvent.get("after").asText();
                methodName = "delete";
            } else {
                rollbackObjectString = debeziumEvent.get("before").asText();
                methodName = "save";
            }

            var rollbackObjectJson = (ObjectNode) objectMapper.readTree(rollbackObjectString);
            if (rollbackObjectJson.get("_id") != null) {
                rollbackObjectJson.set("id", rollbackObjectJson.get("_id").get("$oid"));
            }

            var rollbackObject = objectMapper.treeToValue(rollbackObjectJson,
                    Class.forName(argument.getArgumentClass()));
            var repository = (CrudRepository<?, ?>) applicationContext.getBean(
                    Class.forName(argument.getRepositoryClass()));

            var method = repository.getClass().getMethod(methodName, Object.class);
            method.invoke(repository, rollbackObject);
        }

        //todo handle errors
        //
    }

    //todo  Ovde treba da cupam iz liste svih povezanih servisa
    @Override
    public List<String> getSteps() {
        return singletonList("B");
    }
}
