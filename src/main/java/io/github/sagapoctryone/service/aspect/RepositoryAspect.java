package io.github.sagapoctryone.service.aspect;

import io.github.sagapoctryone.model.RepoCallArg;
import io.github.sagapoctryone.repository.AuxiliaryRepository;
import io.github.sagapoctryone.repository.RepoCallArgRepository;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import static lombok.AccessLevel.PRIVATE;

@Aspect
@Component
@RequiredArgsConstructor
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class RepositoryAspect {

    private final RepoCallArgRepository repoCallArgRepository;


    @Around("execution(* org.springframework.data.repository.CrudRepository+.*(..))")
    public Object AroundRepositoryCall(ProceedingJoinPoint joinPoint) throws Throwable {
        Object result = null;

        if (!TransactionSynchronizationManager.isSynchronizationActive()) {
            throw new IllegalStateException("Transaction is not active");
        }

        result = joinPoint.proceed();

        if (joinPoint.getTarget().getClass().getInterfaces()[0].equals(AuxiliaryRepository.class)
                || joinPoint.getTarget().getClass().getInterfaces()[0].equals(RepoCallArgRepository.class)
    || MDC.get("movementFlag") != null) {
            return result;
        }

        var repoCallArgs = new RepoCallArg();
        repoCallArgs.setAuxiliaryId(MDC.get("auxiliaryId"));

        var argumentClass = joinPoint.getArgs()[0].getClass().toString();
        repoCallArgs.setArgumentClass(argumentClass.split(" ")[1]);
        var repositoryClass = joinPoint.getTarget().getClass().getInterfaces()[0].toString();
        repoCallArgs.setRepositoryClass(repositoryClass.split(" ")[1]);

        repoCallArgRepository.save(repoCallArgs);

        return result;
    }
}
