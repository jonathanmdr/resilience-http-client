package com.resilience.authorizationapi.configuration;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.function.Predicate;

@Configuration
public class LoggingRequestFilterConfiguration extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(LoggingRequestFilterConfiguration.class);
    private static final List<String> PATHS_TO_IGNORE_LOGGING_FILTER = List.of("/health", "/metrics", "/swagger-ui", "/api-docs");

    @Override
    protected void doFilterInternal(final HttpServletRequest request, final HttpServletResponse response, final FilterChain filterChain) throws ServletException, IOException {
        try {
            MDC.put("http_method", request.getMethod());
            MDC.put("http_request_uri", request.getRequestURI());
            MDC.put("http_response_status", String.valueOf(response.getStatus()));

            filterChain.doFilter(request, response);

            if (is2xxSuccessfulLoggable(request, response)) {
                log.info("Request successful");
            }
        } finally {
            MDC.clear();
        }
    }

    private boolean is2xxSuccessfulLoggable(final HttpServletRequest request, final HttpServletResponse response) {
        return shouldBeLogging().test(request.getRequestURI())
            && HttpStatus.valueOf(response.getStatus()).is2xxSuccessful();
    }

    private static Predicate<String> shouldBeLogging() {
        return requestUri -> PATHS_TO_IGNORE_LOGGING_FILTER.stream()
            .noneMatch(requestUri::contains);
    }

}
