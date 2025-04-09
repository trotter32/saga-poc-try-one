package io.github.sagapoctryone.service;


import io.github.sagapoctryone.model.Auxilary;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.MDC;
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
public class FooAspect {

    AuxRepository auxRepository;

    @Before("@annotation(transactional)")
    //@Transactional
    public void beforeTransactionMethod(JoinPoint joinPoint, Transactional transactional) {
        if (!TransactionSynchronizationManager.isSynchronizationActive()) {
            throw new IllegalStateException("Transaction is not active");
        }

        if (MDC.get("choreographyId") == null) {
            MDC.put("choreographyId", UUID.randomUUID().toString());
        }

        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void beforeCompletion() {
                //todo check if transaction marked for rollback skips further calls; Can this call be redundant?
                var aux = new Auxilary();
                aux.setChoreographyId(MDC.get("choreographyId"));
                auxRepository.save(aux);

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
}
