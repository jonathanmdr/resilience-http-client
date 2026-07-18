package com.resilience.auditworker.changestreamcheckpoint.persistence;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "change_stream_checkpoints")
public class ChangeStreamCheckpointDocument {

    @Id
    private String stream;

    @Field(value = "resume_token")
    private String resumeToken;

    public ChangeStreamCheckpointDocument() {
    }

    public ChangeStreamCheckpointDocument(final String stream, final String resumeToken) {
        this.stream = stream;
        this.resumeToken = resumeToken;
    }

    public String resumeToken() {
        return this.resumeToken;
    }
}
