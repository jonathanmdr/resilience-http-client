package com.resilience.auditworker.order.listener;

import com.mongodb.client.model.changestream.ChangeStreamDocument;
import com.resilience.auditworker.common.ChangeStream;
import com.resilience.auditworker.configuration.Json;
import com.resilience.auditworker.changestreamcheckpoint.persistence.ChangeStreamCheckpointDocument;
import com.resilience.auditworker.changestreamcheckpoint.persistence.ChangeStreamCheckpointRepository;
import com.resilience.auditworker.order.persistence.OrderDocument;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.messaging.Message;
import org.springframework.data.mongodb.core.messaging.MessageListener;
import org.springframework.stereotype.Component;

@Component
public class OrderChangeStreamListener implements MessageListener<ChangeStreamDocument<Document>, OrderDocument> {

    private static final Logger LOGGER = LoggerFactory.getLogger(OrderChangeStreamListener.class);

    private final ChangeStreamCheckpointRepository changeStreamCheckpointRepository;

    public OrderChangeStreamListener(final ChangeStreamCheckpointRepository changeStreamCheckpointRepository) {
        this.changeStreamCheckpointRepository = changeStreamCheckpointRepository;
    }

    @Override
    public void onMessage(final Message<ChangeStreamDocument<Document>, OrderDocument> message) {
        final String jsonDocument = Json.writeValueAsString(message.getBody());

        if (message.getRaw() != null) {
            final String resumeToken = message.getRaw().getResumeToken().toJson();
            final ChangeStreamCheckpointDocument job = new ChangeStreamCheckpointDocument(ChangeStream.ORDER.streamId(), resumeToken);
            this.changeStreamCheckpointRepository.save(job);
        }

        LOGGER.info("Order listener received an event: {}", jsonDocument);
    }

}
