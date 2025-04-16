package io.github.sagapoctryone.service.aspect;


import io.github.sagapoctryone.model.Auxiliary;
import io.github.sagapoctryone.repository.AuxiliaryRepository;
import io.github.sagapoctryone.service.movement.MovementReceiver;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.MDC;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.UUID;

import static lombok.AccessLevel.PRIVATE;

@Aspect
@Component
@RequiredArgsConstructor
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class TransactionalAspect {

    AuxiliaryRepository auxiliaryRepository;

    @Around("@annotation(transactional)")
    @Order(2)
    public Object beforeTransactionMethod(ProceedingJoinPoint joinPoint, Transactional transactional) throws Throwable {
        Object result;

        try {
            if (MDC.get("movementFlag") != null) {
                return joinPoint.proceed();
            }

            var choreographyId = UUID.randomUUID().toString();
            if (MDC.get("choreographyId") == null) {
                MDC.put("choreographyId", choreographyId);
            }
            var auxiliary = new Auxiliary();
            auxiliary.setChoreographyId(choreographyId);
            var savedAuxiliary = auxiliaryRepository.save(auxiliary);
            MDC.put("auxiliaryId", savedAuxiliary.getId());

            result = joinPoint.proceed();

            //todo upitan check
            var interfaces = joinPoint.getTarget().getClass().getInterfaces();
            if (interfaces.length > 0 && interfaces[1].equals(MovementReceiver.class)) {
                return result;
            }
        } finally {
/*            System.out.println("~~~ " + Arrays.toString(joinPoint.getArgs()));
            System.out.println("~~~ " + joinPoint.getSignature());
            System.out.println("~~~ " + joinPoint.getClass());
            System.out.println("~~~ " + joinPoint.getTarget());
            System.out.println("~~~ " + joinPoint.getTarget().getClass().getInterfaces()[0]);*/

            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void beforeCompletion() {
                    TransactionSynchronization.super.beforeCompletion();
                }



                @Override
                public void afterCompletion(int status) {
                    if (status == STATUS_ROLLED_BACK) {
                        System.out.println("~~~ Transaction rolled back for: " + MDC.get("choreographyId"));
                        // odje zove producera za rollback
                    }
                    TransactionSynchronization.super.afterCompletion(status);
                }
            });
        }

        return result;
    }
}
