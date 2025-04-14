package com.resilience.auditworker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class AuditWorker {

    public static void main(final String ... args) {
        SpringApplication.run(AuditWorker.class, args);
    }

}
