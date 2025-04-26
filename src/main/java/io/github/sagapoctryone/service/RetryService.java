package io.github.sagapoctryone.service;


import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.context.ApplicationContext;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationTargetException;

import static lombok.AccessLevel.PRIVATE;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class RetryService {

    RetryTemplate retryTemplate;
    ApplicationContext applicationContext;


    public void retryTransaction(RetryCommand retryCommand) {
        retryTemplate.execute(context -> {
            try {
                var transactionClass = applicationContext.getBean(Class.forName(retryCommand.getClassName()));
                var method = transactionClass.getClass().getMethod(retryCommand.getMethodName());
                return method.invoke(transactionClass, retryCommand.getParams());
            } catch (ClassNotFoundException | NoSuchMethodException | InvocationTargetException |
                     IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }, context -> {
            //todo rollback +
            //todo send rollback message
            return null;
        });
    }
}
