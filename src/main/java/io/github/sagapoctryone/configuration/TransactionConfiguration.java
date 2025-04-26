package io.github.sagapoctryone.configuration;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.sagapoctryone.service.RetryCommand;
import io.github.sagapoctryone.service.RetryService;
import jakarta.annotation.PostConstruct;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.slf4j.MDC;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.ConfigurableTransactionManager;
import org.springframework.transaction.TransactionExecution;
import org.springframework.transaction.TransactionExecutionListener;

import java.util.List;

import static lombok.AccessLevel.PRIVATE;


@Configuration
@RequiredArgsConstructor
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class TransactionConfiguration {


    ConfigurableTransactionManager transactionManager;
    ObjectMapper objectMapper;
    RetryService retryService;


    @PostConstruct
    public void transactionManager() {
        transactionManager.setTransactionExecutionListeners(List.of(new TransactionExecutionListener() {
            @Override
            public void afterCommit(@NonNull TransactionExecution transactionExecution, Throwable exception) {
                if (exception != null && MDC.get("retryExecuted") == null) {
                    MDC.put("retryExecuted", "true");

                    System.out.println("!!! op op usli u retry");
                    System.out.println(exception);

                    RetryCommand retryCommand;
                    try {
                        retryCommand = objectMapper.readValue(MDC.get("retryCommand"), RetryCommand.class);
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException(e);
                    }

                    retryService.retryTransaction(retryCommand);
                }
            }

            @Override
            public void afterRollback(TransactionExecution transaction, Throwable rollbackFailure) {
                //todo send message for rollback movement
            }
        }));
    }
}
