package io.github.sagapoctryone.service.aspect;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.sagapoctryone.model.AuxiliaryMovement;
import io.github.sagapoctryone.repository.AuxiliaryMovementRepository;
import io.github.sagapoctryone.service.RetryCommand;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.MDC;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.ConfigurableTransactionManager;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static lombok.AccessLevel.PRIVATE;

@Aspect
@Component
@RequiredArgsConstructor
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class TransactionalAspect {

    AuxiliaryMovementRepository auxiliaryMovementRepository;
    ObjectMapper objectMapper;

    //todo  Implement using reactive stuff, check concurrency
    @Around("@annotation(transactional)")
    @Order(2)
    @Transactional(rollbackFor = TransactionSystemException.class)
    public Object beforeTransactionMethod(ProceedingJoinPoint joinPoint, Transactional transactional) throws Throwable {
        Object result;
        System.out.println("!!! usli u around");
        //todo  treba da bude rollback flag
        if (MDC.get("movementFlag") != null) {
            return joinPoint.proceed();
        }

        var choreographyId = UUID.randomUUID().toString();
        if (MDC.get("choreographyId") == null) {
            MDC.put("choreographyId", choreographyId);
        }

        result = joinPoint.proceed();

        var auxiliaryId = createAuxiliaryMovement(choreographyId);
        MDC.put("auxiliaryId", auxiliaryId);

        if (MDC.get("retryCommand") == null) {
            var retryCommandJson = createRetryCommand(joinPoint);
            MDC.put("retryCommand", retryCommandJson);
        }

        return result;
    }

    private String createAuxiliaryMovement(String choreographyId) {
        var auxiliaryMovement = new AuxiliaryMovement();
        auxiliaryMovement.setChoreographyId(choreographyId);
        var savedAuxiliary = auxiliaryMovementRepository.save(auxiliaryMovement);
        return savedAuxiliary.getId();
    }

    private String createRetryCommand(ProceedingJoinPoint joinPoint) throws JsonProcessingException {
        var retryCommand = new RetryCommand();
        retryCommand.setClassName(joinPoint.getTarget().getClass().getName());
        retryCommand.setMethodName(joinPoint.getSignature().getName());
        retryCommand.setParams(joinPoint.getArgs());
        return objectMapper.writeValueAsString(retryCommand);
    }
}
