package com.resilience.auditworker.configuration.command;

import com.resilience.auditworker.authorization.consumer.model.CdcAuthorizationEvent;
import com.resilience.auditworker.authorization.command.CreateAuthorizationCdcCommand;
import com.resilience.auditworker.authorization.command.DeleteAuthorizationCdcCommand;
import com.resilience.auditworker.authorization.command.UpdateAuthorizationCdcCommand;
import com.resilience.auditworker.common.CdcPayloadEvent.CdcOperation;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;
import java.util.function.Consumer;

@Configuration
public class AuthorizationCdcCommandHandlerFactory {

    @Bean
    public Map<CdcOperation, Consumer<CdcAuthorizationEvent>> authorizationCdcCommandHandlers(
        final CreateAuthorizationCdcCommand createHandler,
        final UpdateAuthorizationCdcCommand updateHandler,
        final DeleteAuthorizationCdcCommand deleteHandler
    ) {
        return Map.of(
            createHandler.op(), createHandler::handle,
            updateHandler.op(), updateHandler::handle,
            deleteHandler.op(), deleteHandler::handle
        );
    }

}
