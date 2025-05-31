package com.resilience.orderworker;

import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.cloud.stream.binder.test.TestChannelBinderConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

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
        "listeners=CONTROLLER://0.0.0.0:9093,EXTERNAL://0.0.0.0:9092",
        "listener.security.protocol.map=CONTROLLER:PLAINTEXT,EXTERNAL:PLAINTEXT",
        "advertised.listeners=EXTERNAL://127.0.0.1:9092",
        "controller.listener.names=CONTROLLER",
        "port=9092"
    },
    controlledShutdown = true,
    kraft = true
)
@Import(TestChannelBinderConfiguration.class)
@ExtendWith(CleanupDatabaseExtension.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public @interface KafkaIntegrationTest {

}
