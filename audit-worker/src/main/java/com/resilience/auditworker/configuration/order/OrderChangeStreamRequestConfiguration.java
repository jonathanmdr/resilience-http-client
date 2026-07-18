package com.resilience.auditworker.configuration.order;

import com.mongodb.client.model.changestream.ChangeStreamDocument;
import com.resilience.auditworker.changestreamcheckpoint.persistence.ChangeStreamCheckpointRepository;
import com.resilience.auditworker.common.ChangeStream;
import com.resilience.auditworker.common.ChangeStreamDefinition;
import com.resilience.auditworker.order.persistence.OrderDocument;
import org.bson.BsonDocument;
import org.bson.Document;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.messaging.ChangeStreamRequest;
import org.springframework.data.mongodb.core.messaging.MessageListener;
import org.springframework.data.mongodb.core.messaging.MessageListenerContainer;
import org.springframework.data.mongodb.core.messaging.Subscription;

@Configuration(proxyBeanMethods = false)
public class OrderChangeStreamRequestConfiguration {

    @Bean
    public Subscription orderChangeStreamSubscription(
        final MessageListenerContainer messageListenerContainer,
        final MessageListener<ChangeStreamDocument<Document>, OrderDocument> orderChangeStreamRequestMessageListenerContainer,
        final ChangeStreamCheckpointRepository changeStreamCheckpointRepository
    ) {
        final ChangeStreamDefinition changeStreamDefinition = ChangeStream.ORDER;
        final ChangeStreamRequest.ChangeStreamRequestBuilder<OrderDocument> builder = ChangeStreamRequest.builder(orderChangeStreamRequestMessageListenerContainer)
            .collection(changeStreamDefinition.collectionName())
            .filter(changeStreamDefinition.aggregation());
        changeStreamCheckpointRepository.findById(changeStreamDefinition.streamId())
            .ifPresent(jobDocument -> {
                final BsonDocument resumeToken = BsonDocument.parse(jobDocument.resumeToken());
                builder.resumeAfter(resumeToken);
            });
        return messageListenerContainer.register(builder.build(), OrderDocument.class);
    }

}
