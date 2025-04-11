package io.github.sagapoctryone;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@SpringBootApplication
@EnableMongoRepositories
@EnableAspectJAutoProxy(proxyTargetClass = true)
public class SagaPocTryOneApplication {

    public static void main(String[] args) {
        SpringApplication.run(SagaPocTryOneApplication.class, args);
    }

}
