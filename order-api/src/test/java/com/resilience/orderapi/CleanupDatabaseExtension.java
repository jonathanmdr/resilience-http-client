package com.resilience.orderapi;

import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ActiveProfiles("integration-test")
public class CleanupDatabaseExtension implements BeforeEachCallback {

    @Override
    public void beforeEach(final ExtensionContext context) {
        final var applicationContext = SpringExtension.getApplicationContext(context);
        applicationContext.getBeansWithAnnotation(Repository.class)
            .values()
            .forEach(repository -> ((JpaRepository<?, ?>) repository).deleteAllInBatch());
    }

}
