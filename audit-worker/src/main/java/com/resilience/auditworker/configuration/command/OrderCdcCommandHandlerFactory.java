package com.resilience.auditworker.configuration.command;

import com.resilience.auditworker.common.CdcPayloadEvent.CdcOperation;
import com.resilience.auditworker.order.consumer.model.CdcOrderEvent;
import com.resilience.auditworker.order.command.CreateOrderCdcCommand;
import com.resilience.auditworker.order.command.DeleteOrderCdcCommand;
import com.resilience.auditworker.order.command.UpdateOrderCdcCommand;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;
import java.util.function.Consumer;

@Configuration
public class OrderCdcCommandHandlerFactory {

    @Bean
    public Map<CdcOperation, Consumer<CdcOrderEvent>> orderCdcCommandHandlers(
        final CreateOrderCdcCommand createHandler,
        final UpdateOrderCdcCommand updateHandler,
        final DeleteOrderCdcCommand deleteHandler
    ) {
        return Map.of(
            createHandler.op(), createHandler::handle,
            updateHandler.op(), updateHandler::handle,
            deleteHandler.op(), deleteHandler::handle
        );
    }

}
