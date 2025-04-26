package io.github.sagapoctryone.configuration;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.backoff.UniformRandomBackOffPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;

import static lombok.AccessLevel.PRIVATE;

@Configuration
@RequiredArgsConstructor
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class GenericConfiguration {


    @Bean
    @ConditionalOnMissingBean(RetryTemplate.class)
    public RetryTemplate retryTemplate() {
        RetryTemplate retryTemplate = new RetryTemplate();

        UniformRandomBackOffPolicy fixedBackOffPolicy = new UniformRandomBackOffPolicy();
        fixedBackOffPolicy.setMinBackOffPeriod(100);
        fixedBackOffPolicy.setMaxBackOffPeriod(1000);
        retryTemplate.setBackOffPolicy(fixedBackOffPolicy);

        SimpleRetryPolicy retryPolicy = new SimpleRetryPolicy();
        retryPolicy.setMaxAttempts(3);
        retryTemplate.setRetryPolicy(retryPolicy);

        return retryTemplate;
    }
}
