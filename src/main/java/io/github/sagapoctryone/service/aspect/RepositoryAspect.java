package io.github.sagapoctryone.service.aspect;

import com.hazelcast.multimap.MultiMap;
import io.github.sagapoctryone.repository.AuxiliaryMovementRepository;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.HashMap;
import java.util.Map;

import static lombok.AccessLevel.PRIVATE;

@Aspect
@Component
@RequiredArgsConstructor
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class RepositoryAspect {

    MultiMap<String, Map<String, String>> auxiliaryMovementStepsMap;


    @Around("execution(* org.springframework.data.repository.CrudRepository+.*(..))")
    public Object AroundRepositoryCall(ProceedingJoinPoint joinPoint) throws Throwable {
        Object result;

        if (!TransactionSynchronizationManager.isSynchronizationActive()) {
            throw new IllegalStateException("Transaction is not active");
        }

        result = joinPoint.proceed();

        if (joinPoint.getTarget().getClass().getInterfaces()[0].equals(AuxiliaryMovementRepository.class)
                || MDC.get("movementFlag") != null) {
            return result;
        }

        saveAuxiliaryMovementSteps(joinPoint);

        return result;
    }

    private void saveAuxiliaryMovementSteps(ProceedingJoinPoint joinPoint) {
        Map<String, String> movementSteps = new HashMap<>();
        var argumentClass = joinPoint.getArgs()[0].getClass().toString();
        movementSteps.put("argumentClass", argumentClass.split(" ")[1]);
        var repositoryClass = joinPoint.getTarget().getClass().getInterfaces()[0].toString();
        movementSteps.put("repositoryClass", repositoryClass.split(" ")[1]);

        auxiliaryMovementStepsMap.put(MDC.get("auxiliaryId"), movementSteps);
    }
}
