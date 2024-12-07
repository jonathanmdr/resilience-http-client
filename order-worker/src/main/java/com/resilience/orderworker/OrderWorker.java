package com.resilience.orderworker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class OrderWorker {

    public static void main(final String ... args) {
        SpringApplication.run(OrderWorker.class, args);
    }

}
