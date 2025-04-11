package io.github.sagapoctryone.service.aspect;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.MDC;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import static lombok.AccessLevel.PRIVATE;

@Aspect
@Component
@RequiredArgsConstructor
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class MovementReceiverAspect {


    @Around("execution(* io.github.sagapoctryone.service.movement.MovementReceiver+.onMessage(..))")
    @Order(1)
    public Object AroundRepositoryCall(ProceedingJoinPoint joinPoint) {
        Object result;

        MDC.put("movementFlag", "true");

        try {
            result = joinPoint.proceed();
        } catch (Throwable e) {
            throw new RuntimeException(e);
        } finally {
            MDC.remove("movementFlag");
        }

        // todo save incoming message as evidence of movement (or compensasting movement) executing

        return result;
    }
}
