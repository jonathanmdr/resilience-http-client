package com.resilience.orderworker;

import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.cloud.stream.binder.test.TestChannelBinderConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.core.annotation.AliasFor;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@EnableConfigurationProperties
@ActiveProfiles("integration-test")
@EnableKafka
@SpringBootTest(webEnvironment = WebEnvironment.NONE)
@EmbeddedKafka(
    brokerProperties = {
        "listeners=PLAINTEXT://localhost:9092",
        "port=9092"
    },
    controlledShutdown = true,
    kraft = true
)
@Import(TestChannelBinderConfiguration.class)
@ExtendWith(CleanupDatabaseExtension.class)
@ContextConfiguration(classes = OrderWorker.class)
public @interface KafkaIntegrationTest {

    @AliasFor(annotation = SpringBootTest.class, attribute = "classes")
    Class<?>[] classes() default {};

    @AliasFor(annotation = SpringBootTest.class, attribute = "properties")
    String[] properties() default {};

}
