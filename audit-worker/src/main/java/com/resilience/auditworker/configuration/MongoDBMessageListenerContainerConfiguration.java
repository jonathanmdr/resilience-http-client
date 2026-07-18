package com.resilience.auditworker.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.messaging.DefaultMessageListenerContainer;
import org.springframework.data.mongodb.core.messaging.MessageListenerContainer;

@Configuration(proxyBeanMethods = false)
public class MongoDBMessageListenerContainerConfiguration {

    @Bean
    public MessageListenerContainer messageListenerContainer(final MongoTemplate mongoTemplate) {
        return new DefaultMessageListenerContainer(mongoTemplate);
    }

}
